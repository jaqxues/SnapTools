package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;

import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import timber.log.Timber;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 06.01.2019 - Time 20:19.
 */

public class ForcedHooks extends ModuleHelper {
    public ForcedHooks(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[0];
    }

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        try {
            final Class nxz = findClass("nxz", snapClassLoader);
            findAndHookMethod(nxz, "a", String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    String str = ((String) param.args[0]).toUpperCase(Locale.ENGLISH);
                    if (str.equals("HLS_VIDEO"))
                        return callStaticMethod(nxz, "valueOf", "VIDEO");
                    return callStaticMethod(nxz, "valueOf", str);
                }
            });
        } catch (Throwable t) {
            Timber.e(t, "Failed setting up Error Suppress Hook");
            moduleLoadState.fail();
        }
    }
}
