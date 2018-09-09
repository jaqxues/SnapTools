package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthLocationOverlay;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials.StealthTutorial;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StealthViewingFragment extends FragmentHelper {
    private StealthLocationOverlay locationOverlay;
    private Button btnShowButtonLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tutorialDetails = StealthTutorial.getTutorials();
        locationOverlay = new StealthLocationOverlay(getActivity());

        redirector = (id, defaultVal, params) -> {
            switch (id) {
                case "back_press":
                    return onBackPressed();
                default:
                    return defaultVal;
            }
        };

        ViewGroup mainContainer = new StealthViewProvider().getMainContainer(getActivity());
        btnShowButtonLocation = getDSLView(mainContainer, "button_stealth_snap_location");

        btnShowButtonLocation.setOnClickListener(v -> locationOverlay.show());

        return mainContainer;
    }

    private boolean showingOverlay() {
        return locationOverlay.getVisibility();
    }

    public boolean onBackPressed() {
        if (!showingOverlay())
            return false;

        locationOverlay.dismiss();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationOverlay.refreshStatusUiVisibility();
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

    @Override
    public String getName() {
        return "Stealth Viewing";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
