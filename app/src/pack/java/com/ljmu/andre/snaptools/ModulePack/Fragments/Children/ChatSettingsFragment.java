package com.ljmu.andre.snaptools.ModulePack.Fragments.Children;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.ChatSettingsViewProvider;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatSettingsFragment extends FragmentHelper {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ChatSettingsViewProvider().getMainContainer(getActivity());
    }

    @Override
    public String getName() {
        return /*Chat Settings*/ decryptMsg(new byte[]{72, -125, 37, -36, 49, -23, 79, 9, -78, -126, 1, -75, -36, -108, -11, -62});
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
