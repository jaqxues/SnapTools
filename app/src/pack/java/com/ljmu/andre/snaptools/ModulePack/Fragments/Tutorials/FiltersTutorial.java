package com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials;

import com.google.android.material.tabs.TabLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FiltersTutorial {
    public static ImmutableList<TutorialDetail> getTutorials(View mainContainer) {
        TabLayout tabLayout = getDSLView(mainContainer, "tab_layout");
        LinearLayout slidingTabStrip = (LinearLayout) tabLayout.getChildAt(0);
        View filtersTab = slidingTabStrip.getChildAt(0);
        View settingsTab = slidingTabStrip.getChildAt(1);

        return new ImmutableList.Builder<TutorialDetail>()
                .add(
                        new TutorialDetail()
                                .setTitle("Custom Filters")
                                .setMessage(
                                        "Welcome to the Custom Filters menu"
                                )
                )
                .build();
    }
}
