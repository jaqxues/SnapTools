package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StoryBlockerViewProvider;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StoryBlockingSettingsFragment extends FragmentHelper {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new StoryBlockerViewProvider().getContainer(getActivity());
    }

    @Override
    public String getName() {
        return "Story Blocker";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
