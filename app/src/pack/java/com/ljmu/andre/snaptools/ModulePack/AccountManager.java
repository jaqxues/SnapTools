package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.AccountManagerFragment;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AccountManager extends ModuleHelper {
    public AccountManager(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{new AccountManagerFragment()};
    }

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Activity snapActivity) {

    }
}
