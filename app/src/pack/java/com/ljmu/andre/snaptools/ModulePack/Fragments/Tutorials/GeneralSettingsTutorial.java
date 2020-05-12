package com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials;

import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;
import com.ljmu.andre.snaptools.Utils.TutorialDetail.MessagePosition;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GeneralSettingsTutorial {

    public static ImmutableList<TutorialDetail> getTutorials(ExpandableItemAdapter expandableItemAdapter, RecyclerView recyclerView) {
        return new ImmutableList.Builder<TutorialDetail>()

                .add(
                        new TutorialDetail()
                                .setTitle("General Settings")
                                .setMessage(
                                        "The General Settings page is used as a master switch for individual modules"
                                                + "\nIt is used to disable an entire module without having to change the individual controls within"
                                )
                                .setMessagePosition(MessagePosition.MIDDLE)
                )

                .add(
                        new TutorialDetail()
                                .setTitle("Module Tile")
                                .setMessage("This is a Module Tile, you can tap it to toggle the entire module On/Off (As shown by the Red/Green icon on the right)")
                                .setViewId(getIdFromString("Saving"))
                )

                .add(
                        new TutorialDetail()
                                .setTitle("Module Description")
                                .setMessage("You can long press a module to display a description of what the modules purpose is")
                                .setViewProcessor(activity -> {
                                    expandableItemAdapter.expand(0, false);

                                    return recyclerView.getChildAt(1);
                                })
                )
                .build();
    }
}
