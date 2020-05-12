package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.SharingView;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SharingFragment extends FragmentHelper {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout sharingContainer = new SharingView().getContainer(getActivity());

		/*SwitchCompat switchShowTutorial = getDSLView(sharingContainer, "switch_sharing_show_tutorial");
		switchShowTutorial.setChecked(getPref(SHOW_SHARING_TUTORIAL));
		switchShowTutorial.setOnCheckedChangeListener(
				(buttonView, isChecked) -> putAndKill(SHOW_SHARING_TUTORIAL, isChecked, getActivity())
		);*/

        return sharingContainer;
    }

    @Override
    public String getName() {
        return "Sharing";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }


}
