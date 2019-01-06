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
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;
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

	@Override public FragmentHelper[] getUIFragments() {
		return null;
	}

	// ===========================================================================

	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {

			hookMethod(
					TEXTURE_VIDVIEW_START,
					new ST_MethodHook() {
						@Override protected void before(MethodHookParam param) throws Throwable {
							callHook(TEXTURE_VIDVIEW_SETLOOPING, param.thisObject, true);
						}
					});


			hookMethod(
					SNAP_COUNTDOWN_POSTER,
					new ST_MethodHook() {
						@Override protected void before(MethodHookParam param) throws Throwable {
							param.args[0] = TimeUnit.DAYS.toMillis(1);
						}
					});

			try {
				Class enumClass = HookResolver.resolveHookClass(ENUM_SNAP_ADVANCE_MODES);
				Object NO_AUTO_ADVANCE_ENUM = getStaticObjectField(enumClass, NO_AUTO_ADVANCE.getVarName());
				String durationKey = /*total_duration_sec*/ decryptMsg(new byte[]{62, 36, 39, -124, 49, 38, -27, 3, 99, -100, 85, 105, -108, 89, -121, -85, 125, 15, 108, -109, 66, -101, -22, -6, 116, 16, -109, 62, -117, 20, -77, -118});
				String autoAdvanceKey = /*auto_advance_mode*/ decryptMsg(new byte[]{118, -85, -11, -84, -4, -108, 19, -68, -61, 100, 18, -78, -21, 83, -46, 85, 98, -26, -2, -18, -36, -10, 43, 28, 92, 30, 18, 97, 38, 112, 33, -43});

				hookMethod(
						STORY_METADATA_INSERT_OBJECT,
						new ST_MethodHook() {
							@Override protected void before(MethodHookParam param) throws Throwable {
								String key = (String) param.args[0];

								if (key.equals(durationKey))
									param.args[1] = TimeUnit.DAYS.toSeconds(1);
								else if (key.equals(autoAdvanceKey))
									param.args[1] = NO_AUTO_ADVANCE_ENUM;
							}
						});

			} catch (Throwable e) {
				Timber.e(e, /*Couldn't find AdvanceMode Enum*/ decryptMsg(new byte[]{-61, 67, 113, 19, -35, 15, -110, -5, 113, -75, -74, 76, -28, 109, 50, 31, -45, 28, 78, 69, -2, -11, -54, -69, 88, -57, 2, -61, 105, -20, 20, -38}));
				moduleLoadState.fail();
			}

			hookAllConstructors(
					RECEIVED_SNAP,
					new ST_MethodHook() {
						@Override protected void after(MethodHookParam param) throws Throwable {
							boolean isVideo = callHook(SNAP_GET_MEDIA_TYPE, param.thisObject);

							if(isVideo && !(boolean) getPref(UNLIMITED_VIEWING_VIDEOS))
								return;

							if(!isVideo && !(boolean) getPref(UNLIMITED_VIEWING_IMAGES))
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
