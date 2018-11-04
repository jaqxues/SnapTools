package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;

import de.robv.android.xposed.XC_MethodReplacement;

import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.COUNTDOWNTIMER_VIEW_ONDRAW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.NEW_CONCENTRIC_TIMERVIEW_ONDRAW;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class RemoveSnapTimer extends ModuleHelper {
    public RemoveSnapTimer(String name, boolean canBeDisabled) {
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
        // Removed within snapchat ===================================================
//		hookMethod(
//				SNAPTIMERVIEW_ONDRAW,
//				XC_MethodReplacement.DO_NOTHING
//		);
//
//		hookMethod(
//				CONCENTRIC_TIMERVIEW_ONDRAW,
//				XC_MethodReplacement.DO_NOTHING
//		);

        hookMethod(
                NEW_CONCENTRIC_TIMERVIEW_ONDRAW,
                XC_MethodReplacement.DO_NOTHING
        );

        hookMethod(
                COUNTDOWNTIMER_VIEW_ONDRAW,
                XC_MethodReplacement.DO_NOTHING
        );
    }
}
