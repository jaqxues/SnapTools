package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENUM_SNAP_ADVANCE_MODES;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.RECEIVED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SNAP_COUNTDOWN_POSTER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SNAP_GET_MEDIA_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_INSERT_OBJECT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.TEXTURE_VIDVIEW_SETLOOPING;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.TEXTURE_VIDVIEW_START;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.MCANONICALDISPLAYNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.NO_AUTO_ADVANCE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.UNLIMITED_VIEWING_IMAGES;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.UNLIMITED_VIEWING_VIDEOS;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class UnlimitedViewing extends ModuleHelper {
    public UnlimitedViewing(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    @Override
    public FragmentHelper[] getUIFragments() {
        return null;
    }

    // ===========================================================================

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        boolean unlimitedViewingImages = getPref(UNLIMITED_VIEWING_IMAGES);
        boolean unlimitedViewingVideos = getPref(UNLIMITED_VIEWING_VIDEOS);

        hookMethod(
                TEXTURE_VIDVIEW_START,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        callHook(TEXTURE_VIDVIEW_SETLOOPING, param.thisObject, true);
                    }
                });


        hookMethod(
                SNAP_COUNTDOWN_POSTER,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        param.args[0] = TimeUnit.DAYS.toMillis(1);
                    }
                });

        try {
            Class enumClass = HookResolver.resolveHookClass(ENUM_SNAP_ADVANCE_MODES);
            Object NO_AUTO_ADVANCE_ENUM = getStaticObjectField(enumClass, NO_AUTO_ADVANCE.getVarName());
            String durationKey = "total_duration_sec";
            String autoAdvanceKey = "auto_advance_mode";

            hookMethod(
                    STORY_METADATA_INSERT_OBJECT,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            String key = (String) param.args[0];

                            if (key.equals(durationKey))
                                param.args[1] = TimeUnit.DAYS.toSeconds(1);
                            else if (key.equals(autoAdvanceKey))
                                param.args[1] = NO_AUTO_ADVANCE_ENUM;
                        }
                    });

        } catch (Throwable e) {
            Timber.e(e, "Couldn't find AdvanceMode Enum");
            moduleLoadState.fail();
        }

        hookAllConstructors(
                RECEIVED_SNAP,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, param.thisObject);

                        if (isVideo && !unlimitedViewingVideos)
                            return;

                        if (!isVideo && !unlimitedViewingImages)
                            return;

                        setObjectField(
                                MCANONICALDISPLAYNAME,
                                param.thisObject,
                                TimeUnit.DAYS.toSeconds(1)
                        );
                    }
                }
        );


		/*hookAllConstructors(
				GALLERY_SNAP,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						setObjectField(
								GALLERY_STORY_DURATION,
								param.thisObject,
								TimeUnit.DAYS.toSeconds(1)
						);
					}
				});

		hookMethod(
				GALLERY_SNAP_GET_DURATION,
				XC_MethodReplacement.returnConstant(TimeUnit.DAYS.toSeconds(1))
		);*/
    }
}
