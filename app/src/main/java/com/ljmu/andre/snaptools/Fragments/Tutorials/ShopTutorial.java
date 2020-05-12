package com.ljmu.andre.snaptools.Fragments.Tutorials;

import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ShopTutorial {
    public static ImmutableList<TutorialDetail> getTutorials(RecyclerView shopRecycler, RecyclerView pendingRecycler) {
        return new ImmutableList.Builder<TutorialDetail>()
                .add(
                        new TutorialDetail()
                                .setTitle("Shop/Donations")
                                .setMessage(
                                        "Due to the significant amount of work required to update SnapTools to a new version of Snapchat, we have opted for a payment system that allows for maximum and continued motivation for the hard working developers."
                                                + "\n\nWhen you purchase a premium pack, for example, the 10.12.1.0 pack - you gain access to SnapTools Premium for that version only, and any subsequent updates in the 10.12.X.X version (Most likely major bugs introduced by Snapchat itself)"
                                                + "\n\nTo gain access to a higher version of Snapchat once a premium pack is bought, a new pack must be purchased that supports your required version"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setTitle("Shop Items")
                                .setMessage(
                                        "Items can be purchased via PayPal by click on and expanding the item"
                                                + "\nTry clicking the first item now to proceed"
                                )
                                .setViewProcessor(
                                        activity -> shopRecycler.getChildAt(0)
                                )
                                .setCanAdvance(false)
                )
                .build();
        // ===========================================================================
    }
}
