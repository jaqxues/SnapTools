package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Fragments.StealthViewingFragment;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;
import timber.log.Timber;

import java.util.*;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.*;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.*;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.*;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.detach;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.*;
import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StealthViewing extends ModuleHelper {
    private static final int MAX_STEALTH_BYPASS_SIZE = 50;
    public static boolean bypassNextStealthView;
    private List<FrameLayout> activeLayouts = new ArrayList<>();
    private Set<String> stealthySnapSet = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > 100;
        }
    });

    private Set<String> stealthBypassSet = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > MAX_STEALTH_BYPASS_SIZE;
        }
    });
    private StealthViewProvider viewProvider = new StealthViewProvider();

    public StealthViewing(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{
                new StealthViewingFragment()
        };
    }

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Activity snapActivity) {
        bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);

        if (getPref(SHOW_SNAP_STEALTH_BUTTON)) {
            hookConstructor(
                    CONSTRUCTOR_OPERA_PAGE_VIEW,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            Timber.d("Opera view created.... Assigning active view");
                            activeLayouts.add((FrameLayout) param.thisObject);
                        }
                    }
            );

            hookMethod(
                    OPENED_SNAP,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            Timber.d("Direct snap displayed... Binding stealth to active layout");

                            if (Looper.myLooper() != Looper.getMainLooper()) {
                                new Handler(Looper.getMainLooper()).post(() -> assignStealthToActiveLayout(snapActivity));
                            } else
                                assignStealthToActiveLayout(snapActivity);
                        }
                    }
            );

            if (!Saving.hasLoadedHooks) {
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
            }

            /**
             * ===========================================================================
             * Story displayed hook
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

                            Timber.d("Story snap displayed... Binding stealth to active layout");

                            if (Looper.myLooper() != Looper.getMainLooper()) {
                                new Handler(Looper.getMainLooper()).post(() -> assignStealthToActiveLayout(snapActivity));
                            } else
                                assignStealthToActiveLayout(snapActivity);
                        }
                    }
            );
        }

        /**
         * ===========================================================================
         * Snap status updater
         * ===========================================================================
         */
        try {
            Class statusEnumClass = HookResolver.resolveHookClass(SNAP_STATUS);
            Class storySnapClass = HookResolver.resolveHookClass(STORY_SNAP);

            Object unviewedEnum = Enum.valueOf(statusEnumClass, "UNVIEWED_AND_LOADED");
            Object receievedEnum = Enum.valueOf(statusEnumClass, "RECEIVED_AND_VIEWED");
            Object viewedEnum = Enum.valueOf(statusEnumClass, "VIEWED_AND_REPLAY_AVAILABLE");

            hookMethod(
                    SET_SNAP_STATUS,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            if (param.thisObject.getClass().equals(storySnapClass)) {
                                return;
                            }

                            String snapId = callHook(GET_SNAP_ID, param.thisObject);

                            Enum<?> statusEnum = (Enum<?>) param.args[0];

                            if (statusEnum.equals(receievedEnum) || statusEnum.equals(viewedEnum)) {
                                if (handleStealthCheck(snapId)) {
                                    param.args[0] = unviewedEnum;
                                }
                            }
                        }
                    }
            );
        } catch (HookNotFoundException | IllegalArgumentException e) {
            Timber.e(e);
            moduleLoadState.fail();
        }

        /**
         * ===========================================================================
         * Story status updater
         * ===========================================================================
         */
        hookMethod(
                MARK_STORY_VIEWED,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        String snapId = callHook(GET_SNAP_ID, param.args[1]);

                        if (handleStealthCheck(snapId) && !(boolean) getPref(STEALTH_MARK_STORY_VIEWED))
                            param.setResult(null);
                    }
                }
        );

        /**
         * ===========================================================================
         * Received snap update payload provider
         * ===========================================================================
         */
        hookMethod(
                GET_RECEIVED_SNAP_PAYLOAD,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Object snapPayloadHolder = getObjectField(RECEIVED_SNAP_PAYLOAD_HOLDER, param.thisObject);
                        Map<String, Object> snapMap = getObjectField(RECEIVED_SNAP_PAYLOAD_MAP, snapPayloadHolder);

                        for (String shouldntStealth : stealthySnapSet) {
                            snapMap.remove(shouldntStealth);
                        }

                        Timber.d("EntrySize: " + snapMap.size());
                    }
                }
        );

        /**
         * ===========================================================================
         * Story snap update payload provider
         * ===========================================================================
         */
        hookMethod(
                GET_STORY_SNAP_PAYLOAD,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        List<Object> storyUpdateList = getObjectField(STORY_UPDATE_METADATA_LIST, param.thisObject);

                        int storyUpdateListSize = storyUpdateList.size();

                        Iterator<Object> storyIterator = storyUpdateList.iterator();

                        while (storyIterator.hasNext()) {
                            Object storyMetaHolder = storyIterator.next();
                            Object storyMetaData = getObjectField(STORY_UPDATE_METADATA, storyMetaHolder);
                            String storyId = getObjectField(STORY_UPDATE_METADATA_ID, storyMetaData);

                            if (stealthySnapSet.contains(storyId))
                                storyIterator.remove();
                        }

                        stealthySnapSet.clear();

                        Timber.d("Stripped %s stories from update list", (storyUpdateListSize - storyUpdateList.size()));
                    }
                }
        );

        /**
         * ===========================================================================
         * General network manager hook
         * ===========================================================================
         */
        hookMethod(
                NETWORK_EXECUTE_SYNC,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        String url = (String) callMethod(param.thisObject, "getUrl");
                        Timber.d("ExecAsyncUrl: " + url);

                        if (!(boolean) getPref(DEFAULT_CHAT_STEALTH))
                            return;

                        if (url.endsWith("chat_typing")) {
                            param.setResult(null);
                        }
                    }
                }
        );

        /**
         * ===========================================================================
         * Chat network update hook
         * ===========================================================================
         */
        hookMethod(
                DISPATCH_CHAT_UPDATE,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Timber.d("Chat event");
                        Timber.d("Param1: " + param.args[0]);
                        Timber.d("Param2: " + param.args[1]);

                        if (getPref(DEFAULT_CHAT_STEALTH)) {
                            Timber.d("Bypassed");
                            param.setResult(null);
                        }
                    }
                }
        );

        /**
         * ===========================================================================
         * Chat Message hasBeenRead hook
         * ===========================================================================
         */
        hookMethod(
                MARK_DIRECT_CHAT_VIEWED_PRESENT,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Timber.d("Marking as read (Present while received): " + param.args[0]);

                        if (getPref(DEFAULT_CHAT_STEALTH))
                            param.setResult(false);
                    }
                }
        );

        hookMethod(
                MARK_DIRECT_CHAT_VIEWED_UNPRESENT,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Timber.d("Marking as read (Not present while received): " + param.args[1]);

                        if (getPref(DEFAULT_CHAT_STEALTH))
                            param.setResult(null);
                    }
                }
        );

        hookMethod(
                MARK_GROUP_CHAT_VIEWED,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Timber.d("KAL Dun got called: " + param.args[0]);

                        if (getPref(DEFAULT_CHAT_STEALTH))
                            param.setResult(null);
                    }
                }
        );

        /**
         * ===========================================================================
         * Chat Header Button hook
         * ===========================================================================
         */
        int cheetahHeaderId = getId(snapActivity, "chat_name_and_story_container");
        int oldHeaderId = getId(snapActivity, "chat_friends_name");

        if (getPref(SHOW_CHAT_STEALTH_BUTTON)) {
            hookAllConstructors(
                    CHAT_V10_BUILDER,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            View topPanel = getObjectField(CHAT_TOP_PANEL_VIEW, param.thisObject);
                            RelativeLayout headerTitle = getView(topPanel, getId(snapActivity, "chat_header_title"));

                            boolean isCheetah = getView(headerTitle, cheetahHeaderId) != null;

                            int headerId = isCheetah ?
                                    cheetahHeaderId : oldHeaderId;

                            headerTitle.addView(viewProvider.getStealthChatButton(snapActivity, headerId, getModuleContext(snapActivity), isCheetah));
                        }
                    }
            );
        }

        ViewGroup cheetahStealthContainer = viewProvider.getProfileContainer(snapActivity, getModuleContext(snapActivity));
        ViewGroup stealthContainer = viewProvider.getProfileContainer(snapActivity, getModuleContext(snapActivity));

        hookConstructor(
                CREATE_CHEETAH_PROFILE_SETTINGS_VIEW,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        LinearLayout snapcodeContainer = (LinearLayout) param.args[0];

                        snapcodeContainer.addView(detach(cheetahStealthContainer));
                    }
                }
        );

        /**
         * ===========================================================================
         * Profile Navigation Buttons hook
         * ===========================================================================
         */
        hookMethod(
                CREATE_PROFILE_SETTINGS_VIEW,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        ViewGroup contentView = (ViewGroup) param.getResult();
                        LinearLayout buttonsLayout = getView(contentView, getId(snapActivity, "profile_navigation_buttons"));

                        buttonsLayout.addView(detach(stealthContainer));
                    }
                });
    }

    private void assignStealthToActiveLayout(Activity snapActivity) {
        Iterator<FrameLayout> activeLayoutIterator = activeLayouts.iterator();

        while (activeLayoutIterator.hasNext()) {
            FrameLayout activeLayout = activeLayoutIterator.next();

            Timber.d("Testing layout: " + activeLayout);
            Timber.d("WindowVis: " + (int) activeLayout.getWindowVisibility());

            if (activeLayout.getWindowVisibility() == View.GONE) {
                Timber.d("Active layout is dead... removing from list");
                activeLayoutIterator.remove();
            } else {
                if (activeLayout.findViewById(getIdFromString("stealth_layout")) != null) {
                    Timber.i("Active layout already has stealth layout... skipping");
                    continue;
                }

                activeLayout.addView(viewProvider.getStealthSnapLayout(snapActivity, getModuleContext(snapActivity)));
            }
        }
    }

    private boolean handleStealthCheck(String snapId) {
        Timber.d("BypassActive: " + bypassNextStealthView);

        if (bypassNextStealthView) {
            Timber.d("Not using stealth for snap");
            bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);
            stealthBypassSet.add(snapId);
            stealthySnapSet.remove(snapId);
            return false;
        }

        if (stealthBypassSet.contains(snapId)) {
            Timber.d("Snap has already been marked as NON STEALTH");
            return false;
        }

        stealthySnapSet.add(snapId);
        bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);
        return true;
    }
}
