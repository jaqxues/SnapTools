package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.common.io.Files;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.SharingFragment;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodReplacement;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENUM_BATCHED_SNAP_POSITION;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.BATCHED_MEDIA_LIMITER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CAMERA_IS_VISIBLE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.REPLACE_SHARED_IMAGE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.REPLACE_SHARED_VIDEO;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.BATCHED_MEDIA_ITEM_BOOLEAN;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.BATCHED_MEDIA_LIST;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BATCHED_MEDIA_CAP;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_SHARING_TUTORIAL;

//import com.crashlytics.android.answers.Answers;
//import com.crashlytics.android.answers.CustomEvent;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Sharing extends ModuleHelper {
    public static final int FAILSAFE_BATCHED_MEDIA_CAP = 36;

    public Sharing(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{
                new SharingFragment()
        };
    }

    // ===========================================================================

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        if (getPref(SHOW_SHARING_TUTORIAL)) {
            hookMethod(
                    CAMERA_IS_VISIBLE,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            Intent intent = ContextHelper.getActivity().getIntent();

                            if (intent == null || ContextHelper.getActivity().isFinishing()) {
                                Timber.d("Null Intent");
                                return;
                            }

                            if (intent.getBooleanExtra("IS_SHARE", false)) {
                                if (intent.getStringExtra("image_url") != null) {
                                    DialogFactory.createConfirmation(
                                            ContextHelper.getActivity(),
                                            "Shared Image Detected",
                                            "Found an image that has been shared to Snapchat"
                                                    + "\nPress No to cancel the share" +
                                                    "\nPress Yes to continue, then take a regular snap for the media to be replaced"
                                            ,
                                            new ThemedClickListener() {
                                                @Override
                                                public void clicked(ThemedDialog themedDialog) {
                                                    themedDialog.dismiss();
                                                }
                                            },
                                            new ThemedClickListener() {
                                                @Override
                                                public void clicked(ThemedDialog themedDialog) {
                                                    intent.removeExtra("image_url");
                                                    intent.removeExtra("IS_SHARE");
                                                    themedDialog.dismiss();
                                                }
                                            }
                                    ).show();
                                } else if (intent.getStringExtra("video_url") != null) {
                                    DialogFactory.createConfirmation(
                                            ContextHelper.getActivity(),
                                            "Shared Video Detected",
                                            "Found a video that has been shared to Snapchat"
                                                    + "\nPress No to cancel the share" +
                                                    "\nPress Yes to continue, then take a regular video for the media to be replaced"
                                            ,
                                            new ThemedClickListener() {
                                                @Override
                                                public void clicked(ThemedDialog themedDialog) {
                                                    themedDialog.dismiss();
                                                }
                                            },
                                            new ThemedClickListener() {
                                                @Override
                                                public void clicked(ThemedDialog themedDialog) {
                                                    intent.removeExtra("image_url");
                                                    intent.removeExtra("IS_SHARE");
                                                    themedDialog.dismiss();
                                                }
                                            }
                                    ).show();

                                } else
                                    intent.removeExtra("IS_SHARE");
                            }
                        }
                    }
            );
        }

        hookMethod(
                REPLACE_SHARED_IMAGE,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Intent intent = ContextHelper.getActivity().getIntent();

                        Timber.d("Called camera a(bitmap, bool, bool)");

                        if (intent == null) {
                            Timber.d("Null Intent");
                            return;
                        }

                        if (intent.getBooleanExtra("IS_SHARE", false)) {
                            intent.removeExtra("IS_SHARE");
                            Timber.d("It's a shared item");
                            String imgPath = intent.getStringExtra("image_url");
                            intent.removeExtra("image_url");
                            Timber.d("ImgPath: " + imgPath);

                            if (imgPath == null) {
                                SafeToastAdapter.showErrorToast(
                                        ContextHelper.getActivity(),
                                        "Shared image path not found"
                                );

//								Answers.safeLogEvent(
//										new CustomEvent("SharedMedia")
//												.putCustomAttribute("Type", "Image")
//												.putCustomAttribute("Success", "FALSE")
//								);
                                return;
                            }

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);

                            if (bitmap == null) {
                                SafeToastAdapter.showErrorToast(
                                        ContextHelper.getActivity(),
                                        "Failed to load shared media"
                                );

//								Answers.safeLogEvent(
//										new CustomEvent("SharedMedia")
//												.putCustomAttribute("Type", "Image")
//												.putCustomAttribute("Success", "FALSE")
//								);
                                return;
                            }

                            param.args[0] = bitmap;

//							Answers.safeLogEvent(
//									new CustomEvent("SharedMedia")
//											.putCustomAttribute("Type", "Image")
//											.putCustomAttribute("Success", "TRUE")
//							);
                        }
                    }
                }
        );

        hookMethod(
                REPLACE_SHARED_VIDEO,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        if (ContextHelper.getActivity() == null || ContextHelper.getActivity().isDestroyed() || ContextHelper.getActivity().isFinishing()) {
                            Timber.w("SnapActivity not valid for shared video");
                            return;
                        }

                        Intent intent = ContextHelper.getActivity().getIntent();

                        if (intent == null) {
                            Timber.d("Null Intent");
                            return;
                        }

                        try {
                            if (intent.getBooleanExtra("IS_SHARE", false)) {
                                intent.removeExtra("IS_SHARE");
                                Timber.d("It's a shared item");
                                String videoPath = intent.getStringExtra("video_url");
                                intent.removeExtra("video_url");
                                Timber.d("VidPath: " + videoPath);

                                if (videoPath == null) {
                                    SafeToastAdapter.showErrorToast(
                                            ContextHelper.getActivity(),
                                            "Shared video path not found"
                                    );

//									Answers.safeLogEvent(
//											new CustomEvent("SharedMedia")
//													.putCustomAttribute("Type", "Video")
//													.putCustomAttribute("Success", "FALSE")
//									);
                                    return;
                                }
                                File sourceFile = new File(videoPath);

                                if (!sourceFile.exists()) {
                                    SafeToastAdapter.showErrorToast(
                                            ContextHelper.getActivity(),
                                            "Shared video doesn't exist"
                                    );

//									Answers.safeLogEvent(
//											new CustomEvent("SharedMedia")
//													.putCustomAttribute("Type", "Video")
//													.putCustomAttribute("Success", "FALSE")
//									);
                                    return;
                                }

                                Uri snapPath = (Uri) param.args[0];

                                if (snapPath == null) {
                                    SafeToastAdapter.showErrorToast(
                                            ContextHelper.getActivity(),
                                            "Recorded video path not found"
                                    );

//									Answers.safeLogEvent(
//											new CustomEvent("SharedMedia")
//													.putCustomAttribute("Type", "Video")
//													.putCustomAttribute("Success", "FALSE")
//									);
                                    return;
                                }

                                File sharedVideoFile = new File(videoPath);
                                File snapFile = new File(snapPath.getPath());
                                Files.copy(sharedVideoFile, snapFile);

//								Answers.safeLogEvent(
//										new CustomEvent("SharedMedia")
//												.putCustomAttribute("Type", "Video")
//												.putCustomAttribute("Success", "TRUE")
//								);
                            }
                        } catch (Throwable t) {
                            Timber.e(t, "Error with shared video");
                        }
                    }
                }
        );

//		findAndHookMethod(
//				"frj", snapClassLoader,
//				"onVideoRecordingSuccess",
//				new HookWrapper((HookBefore) param -> {
//					Timber.d("Video File: " + XposedHelpers.getObjectField(param.thisObject, "k"));
//					if (snapActivity == null || snapActivity.isDestroyed() || snapActivity.isFinishing()) {
//						Timber.w("SnapActivity not valid for shared video");
//						return;
//					}
//
//					Intent intent = snapActivity.getIntent();
//
//					if (intent == null) {
//						Timber.d("Null Intent");
//						return;
//					}
//
//					try {
//						if (intent.getBooleanExtra("IS_SHARE", false)) {
//							intent.removeExtra("IS_SHARE");
//							Timber.d("It's a shared item");
//							String videoPath = intent.getStringExtra("video_url");
//							intent.removeExtra("video_url");
//							Timber.d("VidPath: " + videoPath);
//
//							if (videoPath == null) {
//								SafeToastAdapter.showErrorToast(
//										snapActivity,
//										"Shared video path not found"
//								);
//
//								Answers.safeLogEvent(
//										new CustomEvent("SharedMedia")
//												.putCustomAttribute("Type", "Video")
//												.putCustomAttribute("Success", "FALSE")
//								);
//								return;
//							}
//							File sourceFile = new File(videoPath);
//
//							if (!sourceFile.exists()) {
//								SafeToastAdapter.showErrorToast(
//										snapActivity,
//										"Shared video doesn't exist"
//								);
//
//								Answers.safeLogEvent(
//										new CustomEvent("SharedMedia")
//												.putCustomAttribute("Type", "Video")
//												.putCustomAttribute("Success", "FALSE")
//								);
//								return;
//							}
//
//							File sharedVideoFile = new File(videoPath);
//							File snapFile = (File) XposedHelpers.getObjectField(param.thisObject, "k");
//							Files.copy(sharedVideoFile, snapFile);
//
//							Answers.safeLogEvent(
//									new CustomEvent("SharedMedia")
//											.putCustomAttribute("Type", "Video")
//											.putCustomAttribute("Success", "TRUE")
//							);
//						}
//					} catch (Throwable t) {
//						Timber.e(t, "Error with shared video");
//					}
//				})
//		);

        hookMethod(
                BATCHED_MEDIA_LIMITER,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            List<Object> batchedMediaList = getObjectField(BATCHED_MEDIA_LIST, param.thisObject);
                            Class typeEnum = HookResolver.resolveHookClass(ENUM_BATCHED_SNAP_POSITION);

                            if (batchedMediaList == null) {
                                Timber.i("Null batched media list");
                                return Enum.valueOf(typeEnum, "NONE");
                            }

                            int batchCap = Math.min(getPref(BATCHED_MEDIA_CAP), FAILSAFE_BATCHED_MEDIA_CAP);

                            List<Integer> arrayList = new ArrayList<>(batchCap);
                            int i = 0;

                            while (i < batchedMediaList.size() && i < batchCap) {
                                Object batchedMediaItem = batchedMediaList.get(i);
                                setObjectField(BATCHED_MEDIA_ITEM_BOOLEAN, batchedMediaItem, false);
                                arrayList.add(i);
                                i++;
                            }


                            if (arrayList.isEmpty()) {
                                Timber.i("Empty batch list");
                                return Enum.valueOf(typeEnum, "NONE");
                            }

                            Timber.i("Batched size: " + arrayList.size());

                            if (arrayList.size() == 1) {
                                int intValue = arrayList.get(0);
                                if (intValue == 0)
                                    return Enum.valueOf(typeEnum, "BEGIN");

                                if (intValue == i - 1)
                                    return Enum.valueOf(typeEnum, "END");

                                return Enum.valueOf(typeEnum, "MIDDLE");
                            } else if (arrayList.size() == 2 && arrayList.get(0) == 0 && arrayList.get(1) == i - 1)
                                return Enum.valueOf(typeEnum, "BEGIN_AND_END");
                            else
                                return Enum.valueOf(typeEnum, "OTHER");
                        } catch (Throwable t) {
                            Timber.e(t);
                            throw t;
                        }
                    }
                }
        );
    }
}
