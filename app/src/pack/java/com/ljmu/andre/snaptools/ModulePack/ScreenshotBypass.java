package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import de.robv.android.xposed.XC_MethodReplacement;

import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SCREENSHOT_BYPASS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SET_SCREENSHOT_COUNT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SET_SCREENSHOT_COUNT3;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SET_VIDEO_SCREENSHOT_COUNT;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ScreenshotBypass extends ModuleHelper {
	public ScreenshotBypass(String name, boolean canBeDisabled) {
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
				SCREENSHOT_BYPASS,
				XC_MethodReplacement.DO_NOTHING);

		hookMethod(
				SET_SCREENSHOT_COUNT,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) {
						param.args[0] = 0L;
					}
				});
		hookMethod(
				SET_VIDEO_SCREENSHOT_COUNT,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) {
						param.args[0] = 0L;
					}
				});
		hookMethod(
				SET_SCREENSHOT_COUNT3,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) {
						param.args[0] = 0L;
					}
				});
	}
}
