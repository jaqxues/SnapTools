package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.SavingSettingsFragment;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification.ToastType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SaveTriggerManager;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.ChatImageSnap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.ChatVideoSnap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.GroupSnap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.ReceivedSnap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.SentSnap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.Builder;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SaveState;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapTypeDef;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.StorySnap;
import com.ljmu.andre.snaptools.ModulePack.Utils.SavingLayout;
import com.ljmu.andre.snaptools.ModulePack.Utils.SavingViewPool;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.CustomObservers.ErrorObserver;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Callable;

import de.robv.android.xposed.XposedHelpers;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.RECEIVED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SENT_IMAGE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SENT_VIDEO;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_IMAGE_GET_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_VIDEO_GET_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_VIDEO_PATH;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CONSTRUCTOR_OPERA_PAGE_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.DIRECT_GET_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.ENCRYPTION_ALGORITHM_STREAM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GROUP_ALGORITHM_UNWRAPPED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GROUP_GET_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.OPENED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SENT_BATCHED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SENT_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SNAP_GET_MEDIA_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SNAP_GET_TIMESTAMP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SNAP_GET_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_DISPLAYED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_GET_ALGORITHM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_GET_OBJECT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_INSERT_OBJECT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STREAM_TYPE_CHECK_BYPASS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.CHAT_METADATA_MEDIA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.GROUP_ALGORITHM_WRAPPER_FIELD;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SENT_BATCHED_VIDEO_MEDIAHOLDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SENT_MEDIA_BATCH_DATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SENT_MEDIA_BITMAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SENT_MEDIA_TIMESTAMP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SENT_MEDIA_VIDEO_URI;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SNAP_IS_ZIPPED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_ADVANCER_DISPLAY_STATE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_ADVANCER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STREAM_TYPE_CHECK_BOOLEAN;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_SENT_SNAPS;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TEMP_PATH;
import static com.ljmu.andre.snaptools.Utils.XposedUtils.logEntireClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
public class Saving extends ModuleHelper {
    private static final String KEY_HEADER = "SNAP_KEY";
    public static boolean hasLoadedHooks;
    private static String yourUsername = "";

    public Saving(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{
                new SavingSettingsFragment()
        };
    }

    // ===========================================================================


    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        SaveTriggerManager.init();

        /**
         * ===========================================================================
         * Hook to inject the manual saving layout
         * ===========================================================================
         */
        hookConstructor(
                CONSTRUCTOR_OPERA_PAGE_VIEW,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        try {
                            FrameLayout operaLayout = (FrameLayout) param.thisObject;
                            SavingLayout savingLayout = SavingViewPool.requestLayout(ContextHelper.getActivity());

                            operaLayout.addView(savingLayout);
                        } catch (Throwable t) {
                            Timber.e(t);
                        }
                    }
                }
        );

        /**
         * ===========================================================================
         * Bypasses type checking on encrypted streams
         * This would cause a duplicate stream to become corrupt so cannot happen
         * ===========================================================================
         */
        hookMethod(
                STREAM_TYPE_CHECK_BYPASS,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        setObjectField(STREAM_TYPE_CHECK_BOOLEAN, param.thisObject, false);
                    }
                }
        );

        /**
         * ===========================================================================
         * New Sent Snap Detected -> Determine the type and extract the media
         * ===========================================================================
         */
        if (getPref(SAVE_SENT_SNAPS)) {
            hookMethod(
                    SENT_BATCHED_SNAP,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            Timber.i("Splitting process");

                            Object mediaHolder = getObjectField(SENT_BATCHED_VIDEO_MEDIAHOLDER, param.thisObject);

                            Observable.fromCallable(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    Uri videoUri = getObjectField(SENT_MEDIA_VIDEO_URI, mediaHolder);
                                    Timber.i("Handling splitting media");
                                    String videoPath = videoUri.getPath().replaceFirst("file:", "");
                                    File sourceMedia = new File(videoPath);

                                    if (!sourceMedia.exists() && !sourceMedia.createNewFile())
                                        Timber.w("Source tracked video doesn't exist and couldn't be created");

                                    File tempDir = getCreateDir(TEMP_PATH);
                                    File targetMedia = new File(tempDir, "Batched_Sent_Snap.mp4");

                                    Files.copy(sourceMedia, targetMedia);
                                    Timber.d("Copied batch file successfully");
                                    return new Object();
                                }
                            }).subscribeOn(Schedulers.computation())
                                    .subscribe(new ErrorObserver<>("Error duplicating sent batch"));

                            //Files.copy(file);
                        }
                    }
            );

            hookMethod(
                    SENT_SNAP,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            Object media = param.args[0];

                            SentSnap sentSnap = null;

                            try {
                                Class sentImage = HookResolver.resolveHookClass(SENT_IMAGE);
                                Class sentVideo = HookResolver.resolveHookClass(SENT_VIDEO);

                                if (media.getClass().equals(sentImage))
                                    sentSnap = handleSentSnap(ContextHelper.getActivity(), media, false);
                                else if (media.getClass().equals(sentVideo))
                                    sentSnap = handleSentSnap(ContextHelper.getActivity(), media, true);
                                else {
                                    Timber.e("Unhandled Sent Snap Type: %s", media.getClass());
                                }
                            } catch (Throwable t) {
                                Timber.e(t);
                            }

                            SaveNotification.show(
                                    ContextHelper.getActivity(),
                                    sentSnap != null ? SaveNotification.ToastType.GOOD : SaveNotification.ToastType.BAD,
                                    Toast.LENGTH_LONG,
                                    sentSnap
                            );
                        }
                    }
            );
        }


        /**
         * ===========================================================================
         * Story Snap Algorithm Injection
         * ===========================================================================
         */
        hookMethod(
                STORY_GET_ALGORITHM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        if (param.getResult() == null) {
                            Timber.w("Null Story Snap Algorithm");
                            return;
                        }

                        //Timber.d("Got original algorithm: " + param.thisObject.toString());
                        //logStackTrace();

                        String key = getOrCreateKey(param.thisObject);
                        boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, param.thisObject);
                        boolean isZipped = getObjectField(SNAP_IS_ZIPPED, param.thisObject);
                        String fileExtension = isVideo ? ".mp4" : ".jpg";

                        Long timestamp = callHook(SNAP_GET_TIMESTAMP, param.thisObject);
                        String username = callHook(SNAP_GET_USERNAME, param.thisObject);

                        if (username != null && username.equals(yourUsername)) {
                            Timber.i("Viewing your own story");
                            return;
                        }

                        // Build the snap ============================================================
                        new Builder()
                                .setContext(ContextHelper.getActivity())
                                .setKey(key)
                                .setUsername(username)
                                .setDateTime(timestamp)
                                .setSnapType(SnapTypeDef.STORY)
                                .setFileExtension(fileExtension)
                                .setIsZipped(isZipped)
                                .build(StorySnap.class)
                                // Signal the snap that it's retrieving the algorithm ========================
                                .providingAlgorithm();

                        setAdditionalInstanceField(param.getResult(), KEY_HEADER, key);
                    }
                }
        );

        /**
         * ===========================================================================
         * Direct Snap Algorithm Injection
         * ===========================================================================
         */
        hookMethod(
                DIRECT_GET_ALGORITHM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        if (param.getResult() == null) {
                            Timber.w("Null Direct Snap Algorithm");
                            return;
                        }

                        Timber.d("EncryptionHolder: " + param.getResult());

                        Object snapMetaData = param.args[0];
                        String key = getOrCreateKey(snapMetaData);
                        boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, snapMetaData);
                        boolean isZipped = getObjectField(SNAP_IS_ZIPPED, snapMetaData);
                        String fileExtension = isVideo ? ".mp4" : ".jpg";

                        Long timestamp = callHook(SNAP_GET_TIMESTAMP, snapMetaData);
                        String username = callHook(SNAP_GET_USERNAME, snapMetaData);

                        new Builder()
                                .setContext(ContextHelper.getActivity())
                                .setKey(key)
                                .setUsername(username)
                                .setDateTime(timestamp)
                                .setSnapType(SnapTypeDef.RECEIVED)
                                .setFileExtension(fileExtension)
                                .setIsZipped(isZipped)
                                .build(ReceivedSnap.class);

                        Object encryptionHolder = param.getResult();
                        Object encryptor = XposedHelpers.getObjectField(encryptionHolder, "c");
                        setAdditionalInstanceField(encryptor, KEY_HEADER, key);
                    }
                }
        );

        /**
         * ===========================================================================
         * Chat Image Algorithm Injection
         * ===========================================================================
         */
        hookMethod(
                CHAT_IMAGE_GET_ALGORITHM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        if (param.getResult() == null) {
                            Timber.w("Null Chat Video Algorithm");
                            return;
                        }

                        Object chatMetaData = param.args[0];

                        if (chatMetaData == null) {
                            Timber.w("Null ChatMetaData");
                            return;
                        }

                        String key = getOrCreateKey(chatMetaData);
                        boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, chatMetaData);

                        setAdditionalInstanceField(param.getResult(), KEY_HEADER, key);

                        // If it's a video, don't overwrite the existing stored data =================
                        if (isVideo)
                            return;

                        Long timestamp = callHook(SNAP_GET_TIMESTAMP, chatMetaData);
                        String username = callHook(SNAP_GET_USERNAME, chatMetaData);

                        new Snap.Builder()
                                .setContext(ContextHelper.getActivity())
                                .setKey(key)
                                .setUsername(username)
                                .setDateTime(timestamp)
                                .setSnapType(SnapTypeDef.CHAT)
                                .setFileExtension(".jpg")
                                .build(ChatImageSnap.class);
                    }
                }
        );

        /**
         * ===========================================================================
         * Chat Video Algorithm Injection
         * ===========================================================================
         */
        hookMethod(
                CHAT_VIDEO_GET_ALGORITHM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        Object chatMetaData = getObjectField(CHAT_METADATA_MEDIA, param.thisObject);

                        if (chatMetaData == null) {
                            Timber.w("Null ChatMetaData");
                            return;
                        }

                        Uri videoPath = callHook(CHAT_VIDEO_PATH, chatMetaData);

                        String key = getOrCreateKey(chatMetaData);

                        Long timestamp = callHook(SNAP_GET_TIMESTAMP, chatMetaData);
                        String username = callHook(SNAP_GET_USERNAME, chatMetaData);

                        ChatVideoSnap snap = new ChatVideoSnap.Builder()
                                .setVideoPath(videoPath)
                                .setContext(ContextHelper.getActivity())
                                .setKey(key)
                                .setUsername(username)
                                .setDateTime(timestamp)
                                .setSnapType(SnapTypeDef.CHAT)
                                .setFileExtension(".mp4")
                                .build();

                        snap.providingAlgorithm();
                    }
                }
        );

        /**
         * ===========================================================================
         * Group Snap Algorithm Injection
         * ===========================================================================
         */
        hookMethod(
                GROUP_GET_ALGORITHM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        if (param.getResult() == null) {
                            Timber.w("Null Group Snap Algorithm");
                            return;
                        }

                        Object groupMetaData = param.args[0];

                        if (groupMetaData == null) {
                            Timber.w("Null GroupMetaData");
                            return;
                        }

                        Class receivedSnapClass = HookResolver.resolveHookClass(RECEIVED_SNAP);

                        if (groupMetaData.getClass() == receivedSnapClass) {
                            Timber.i("Tried to process received snap in group handler");
                            return;
                        }

                        String key = getOrCreateKey(groupMetaData);
                        boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, groupMetaData);
                        String fileExtension = isVideo ? ".mp4" : ".jpg";

                        Object encryptionWrapper;
                        if (isVideo)
                            encryptionWrapper = callHook(GROUP_ALGORITHM_UNWRAPPED, param.getResult(), "video_first_frame_media_info");
                        else
                            encryptionWrapper = callHook(GROUP_ALGORITHM_UNWRAPPED, param.getResult(), "image_media_info");

                        Long timestamp = callHook(SNAP_GET_TIMESTAMP, groupMetaData);
                        String username = callHook(SNAP_GET_USERNAME, groupMetaData);

                        new Builder()
                                .setContext(ContextHelper.getActivity())
                                .setKey(key)
                                .setUsername(username)
                                .setDateTime(timestamp)
                                .setSnapType(SnapTypeDef.GROUP)
                                .setFileExtension(fileExtension)
                                .build(GroupSnap.class);

                        setAdditionalInstanceField(getObjectField(GROUP_ALGORITHM_WRAPPER_FIELD, encryptionWrapper), KEY_HEADER, key);
                    }
                }
        );

        /**
         * ===========================================================================
         * Encryption Algorithm Stream Wrapper
         * ===========================================================================
         */
        hookMethod(
                ENCRYPTION_ALGORITHM_STREAM,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        String key = (String) getAdditionalInstanceField(param.thisObject, KEY_HEADER);

                        if (key == null) {
                            return;
                        }

                        Snap snap = Snap.getSnapFromCache(key);

                        if (snap != null) {
                            InputStream inputStream = (InputStream) param.getResult();
                            Timber.d("InputStream: " + inputStream);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            try {
                                ByteStreams.copy(inputStream, outputStream);
                            } catch (IOException e) {
                                Timber.e(e);
                            }

                            ByteArrayInputStream copiedInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                            param.setResult(copiedInputStream);

                            SaveState saveState = snap.copyStream(outputStream);

                            Timber.d("Stream Save State: " + saveState);

                            if (saveState == null) {
                                Timber.d("Null savestate... Ignoring");
                                return;
                            }

                            ToastType toastType;

                            switch (saveState) {
                                case NOT_READY:
                                    Timber.i("Snap not ready");
                                    return;
                                case EXISTING:
                                    toastType = SaveNotification.ToastType.WARNING;
                                    break;
                                case FAILED:
                                    toastType = SaveNotification.ToastType.BAD;
                                    break;
                                case SUCCESS:
                                    toastType = SaveNotification.ToastType.GOOD;
                                    break;
                                default:
                                    Timber.e("Unhandled Save State: " + saveState);
                                    return;
                            }

                            SaveNotification.show(
                                    ContextHelper.getActivity(),
                                    toastType,
                                    Toast.LENGTH_LONG,
                                    snap
                            );
                        }
                    }
                }
        );

        /**
         * ===========================================================================
         * Force Stories to contain their matched MetaData
         * ===========================================================================
         */
        hookMethod(
                STORY_METADATA_BUILDER,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        Object storyMetadata = param.getResult();
                        callHook(STORY_METADATA_INSERT_OBJECT, storyMetadata, "STORY_REPLY_SNAP", param.args[0]);
                    }
                }
        );

        /**
         * ===========================================================================
         * Story Viewed -> Grab MetaData and draw the frame to a bitmap to save
         * ===========================================================================
         */
        hookMethod(
                STORY_DISPLAYED,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        Object displayState = getObjectField(STORY_ADVANCER_DISPLAY_STATE, param.thisObject);

                        if (displayState == null || !displayState.toString().equals("FULLY_DISPLAYED"))
                            return;

                        Object snapMetaData = getObjectField(STORY_ADVANCER_METADATA, param.thisObject);

                        Object storySnap = callHook(STORY_METADATA_GET_OBJECT, snapMetaData, "STORY_REPLY_SNAP");

                        if (storySnap == null) {
                            Timber.d("StorySnap null: Probably not a normal story");
                            return;
                        }

                        String username = callHook(SNAP_GET_USERNAME, storySnap);

                        if (username != null && username.equals(yourUsername)) {
                            Timber.i("Your story displayed");
                            return;
                        }

                        String key = getOrCreateKey(storySnap);
                        Snap snap = Snap.getSnapFromCache(key);

                        if (snap == null) {
                            Timber.e("Null Snap from Cache");
                            SaveNotification.show(
                                    ContextHelper.getActivity(),
                                    SaveNotification.ToastType.BAD,
                                    Toast.LENGTH_LONG
                            );
                            return;
                        }

                        SaveState saveState = snap.finalDisplayEvent();

                        Timber.d("Stream Save State: " + saveState);

                        if (saveState == null) {
                            Timber.d("Null savestate... Ignoring");
                            return;
                        }

                        ToastType toastType;

                        switch (saveState) {
                            case NOT_READY:
                                Timber.i("Snap not ready");
                                return;
                            case EXISTING:
                                toastType = SaveNotification.ToastType.WARNING;
                                break;
                            case FAILED:
                                toastType = SaveNotification.ToastType.BAD;
                                break;
                            case SUCCESS:
                                toastType = SaveNotification.ToastType.GOOD;
                                break;
                            default:
                                Timber.e("Unhandled Save State: " + saveState);
                                return;
                        }

                        SaveNotification.show(
                                ContextHelper.getActivity(),
                                toastType,
                                Toast.LENGTH_LONG,
                                snap
                        );
                    }
                }
        );

        /**
         * ===========================================================================
         * Received Snap Opened -> Grab MetaData and wait
         * ===========================================================================
         */
        hookMethod(
                OPENED_SNAP,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        Timber.d("Snap Type: " + param.thisObject.getClass());

                        if (!((boolean) param.args[0])) {
                            Timber.d("Direct Snap Input False");

                            return;
                        }

                        String key = getOrCreateKey(param.thisObject);
                        Snap snap = Snap.getSnapFromCache(key);

                        if (snap == null) {
                            Timber.e("Null Snap from Cache");
                            SaveNotification.show(ContextHelper.getActivity(), SaveNotification.ToastType.BAD, Toast.LENGTH_LONG);
                            return;
                        }

                        if (!(snap instanceof ReceivedSnap)) {
                            Timber.w("Tried to handle an incorrect type in the received snap handler: " + snap.getClass());
                            return;
                        }

                        SaveState saveState = snap.finalDisplayEvent();

                        Timber.d("Stream Save State: " + saveState);

                        if (saveState == null) {
                            Timber.d("Null savestate... Ignoring");
                            return;
                        }

                        ToastType toastType;

                        switch (saveState) {
                            case NOT_READY:
                                Timber.i("Snap not ready");
                                return;
                            case EXISTING:
                                toastType = SaveNotification.ToastType.WARNING;
                                break;
                            case FAILED:
                                toastType = SaveNotification.ToastType.BAD;
                                break;
                            case SUCCESS:
                                toastType = SaveNotification.ToastType.GOOD;
                                break;
                            default:
                                Timber.e("Unhandled Save State: " + saveState);
                                return;
                        }

                        SaveNotification.show(
                                ContextHelper.getActivity(),
                                toastType,
                                Toast.LENGTH_LONG,
                                snap
                        );

						/*TransferState transferState = ((ReceivedSnap) snap).markReady();

						if (transferState == null) {
							Timber.w("Direct snap not ready to save");
							return;
						}

						StackingDotNotification.showStatus(snapActivity, transferState.getToastType(), Toast.LENGTH_LONG);*/
                    }
                }
        );

        hasLoadedHooks = true;
    }

    @Override
    public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {
        // Safer to call User Prefs later, after Snapchat has been initialized
        try {
            yourUsername = callStaticHook(GET_USERNAME);
        } catch (HookNotFoundException e) {
            Timber.e(e);
            moduleLoadState.fail();
        }
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * Attempt to extract and save the appropriate media from the holder
     * ===========================================================================
     *
     * @return NULL if unsuccessful
     */
    private SentSnap handleSentSnap(Activity snapActivity, Object mediaHolder, boolean isVideo) {
        String username;

        try {
            username = callStaticHook(GET_USERNAME);
        } catch (HookNotFoundException e) {
            Timber.e(e);
            return null;
        }

        long timestamp = getObjectField(SENT_MEDIA_TIMESTAMP, mediaHolder);

        // Create Snap to get OutputFile =============================================
        SentSnap snap = new SentSnap()
                .setContext(snapActivity)
                .setKey(UUID.randomUUID().toString())
                .setUsername(username)
                .setDateTime(timestamp)
                .setFileExtension(isVideo ? ".mp4" : ".jpg")
                .setSnapType(SnapTypeDef.SENT);

        File outputFile = snap.getOutputFile();

        try {
            if (!outputFile.exists() && !outputFile.createNewFile())
                throw new IOException("Couldn't Create New File: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }

        // Create a closer to register all streams ===================================
        Closer closer = Closer.create();

        try {
            FileOutputStream outputStream = closer.register(new FileOutputStream(outputFile));

            if (!isVideo) {

                // Compress the image into the stream ========================================
                Bitmap sentImage = getObjectField(SENT_MEDIA_BITMAP, mediaHolder);
                if (sentImage == null) {
                    Timber.w("No sendable image?");
                    return null;
                }

                sentImage.compress(CompressFormat.JPEG, 100, outputStream);

            } else {

                Timber.d("MediaHolder: " + mediaHolder.getClass());
                // Copy the video file into our output stream ================================
                Object batchHolder = getObjectField(SENT_MEDIA_BATCH_DATA, mediaHolder);

                Timber.d("BatchHolder: " + batchHolder);
                logEntireClass(batchHolder, 2);

                File videoFile;
                if (batchHolder != null) {
                    videoFile = new File(getCreateDir(TEMP_PATH), "Batched_Sent_Snap.mp4");
                } else {
                    Uri videoUri = getObjectField(SENT_MEDIA_VIDEO_URI, mediaHolder);

                    if (videoUri == null)
                        return null;

                    String videoPath = videoUri.getPath().replaceFirst("file:", "");
                    videoFile = new File(videoPath);

                    if (!videoFile.exists())
                        return null;
                }

                Files.copy(
                        videoFile,
                        outputFile
                );
            }

            snap.runMediaScanner(outputFile.getAbsolutePath());
            return snap;
        } catch (FileNotFoundException e) {
            Timber.e(e, "Input or Output files not found!");
        } catch (IOException e) {
            Timber.e(e, "Error copying the video to the output location");
        } finally {
            try {
                closer.close();
            } catch (IOException ignore) {
                // We tried our best to close the streams...
                // Nothing more we can do
            }
        }

        return null;
    }

    // ===========================================================================

    /**
     * ===========================================================================
     * Get an existing or create a unique key for a specific object
     * ===========================================================================
     */
    @Nullable
    private synchronized String getOrCreateKey(Object obj) {
        if (obj == null)
            return null;

        String key = (String) getAdditionalInstanceField(obj, KEY_HEADER);

        if (key == null) {
            key = UUID.randomUUID().toString();
            setAdditionalInstanceField(obj, KEY_HEADER, key);
            Timber.d("Applying [Key: %s] to [Obj: %s]", key, obj.toString());
        }

        return key;
    }
}
