package com.ljmu.andre.snaptools.Fragments.Tutorials;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.google.common.collect.ImmutableList;
import com.ljmu.andre.snaptools.Fragments.PackDownloaderFragment;
import com.ljmu.andre.snaptools.Fragments.PackManagerFragment;
import com.ljmu.andre.snaptools.Fragments.PackSelectorFragment;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;
import com.ljmu.andre.snaptools.Utils.TutorialDetail.MessagePosition;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackManagerTutorial {
    public static ImmutableList<TutorialDetail> getTutorials(PackManagerFragment managerFragment, View selectorTab,
                                                             View downloaderTab, ViewPager pager) {
        return new ImmutableList.Builder<TutorialDetail>()

                .add(
                        new TutorialDetail()
                                .setViewId(R.id.tab_layout)
                                .setTitle("Pack Manager Tabs")
                                .setMessage(
                                        "Here you have your Pack Manager tabs, tapping one of these tabs will open up the corresponding menus"
                                )
                )

                /**
                 * ===========================================================================
                 * Initial Tutorial describing the Pack Selector tab
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> {
                                    pager.setCurrentItem(0, false);
                                    return selectorTab;
                                })
                                .setTitle("Pack Selector")
                                .setMessage(
                                        "This is your Pack Selector.\n\n"
                                                + "SnapTools uses a unique \"Pack\" system to customize and manage its features & version support.\n\n"
                                                + "Packs that you have installed will show up in here and can be configured to taste."
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewId(R.id.recycler_pack_selector)
                                .setTitle("Installed Packs List")
                                .setMessagePosition(MessagePosition.MIDDLE)
                                .setMessage(
                                        "Here are your currently installed packs."
                                                + "\nThe Snapchat version that they "
                                                + "\nActive packs will be displayed with a ticked yellow circle"
                                )
                )

                /**
                 * ===========================================================================
                 * Explanation of the pack configurations
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(
                                        activity -> {
                                            pager.setCurrentItem(0, false);
                                            PackSelectorFragment selectorFragment = managerFragment.getChildFragment(PackSelectorFragment.class);

                                            View packView = selectorFragment.getRecyclerView().getChildAt(0);

                                            if (packView == null)
                                                return selectorTab;

                                            return packView;
                                        }
                                )
                                .setTitle("Pack Tile")
                                .setMessage(
                                        "Each pack will be displayed with the compatible Snapchat version."
                                                + "\nPacks will not work on Snapchat versions other than the one displayed in the name"
                                )
                )
                .add(
                        new TutorialDetail()
                                .setViewProcessor(
                                        activity -> {
                                            pager.setCurrentItem(0, false);
                                            PackSelectorFragment selectorFragment = managerFragment.getChildFragment(PackSelectorFragment.class);
                                            int packIndex = 0;

                                            selectorFragment.getAdapter().expand(packIndex);
                                            View packView = selectorFragment.getRecyclerView().getChildAt(packIndex + 1);

                                            if (packView == null)
                                                return selectorTab;

                                            return packView;
                                        }
                                )
                                .setTitle("Pack Configuring")
                                .setMessage(
                                        "You can configure your packs by expanding it and clicking the controls within\n" +
                                                "\u2022 The Toggle Pack switch will load/unload the pack\n" +
                                                "\u2022 The X on the right will delete a pack (Confirmation required)\n" +
                                                "\u2022 When a pack is ACTIVE it will display a Settings icon on the left\n"
                                )
                )

                /**
                 * ===========================================================================
                 * Description of the Pack Downloader section
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(activity -> {
                                    pager.setCurrentItem(1, false);
                                    return downloaderTab;
                                })
                                .setTitle("Pack Downloader")
                                .setMessage(
                                        "This is your Pack Downloader.\n\n"
                                                + "On this page, you can download the pack(s) needed for your Snapchat version and the features you want"
                                )
                )

                /**
                 * ===========================================================================
                 * Example of expanding downloadable packs
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(
                                        activity -> {
                                            pager.setCurrentItem(1, false);
                                            PackDownloaderFragment downloaderFragment = managerFragment.getChildFragment(PackDownloaderFragment.class);
                                            int packIndex = 0;
                                            ExpandableItemEntity item = downloaderFragment.getAdapter().getItem(packIndex);

                                            if (item != null && item.isExpanded())
                                                downloaderFragment.getAdapter().collapse(packIndex);

                                            return downloaderFragment.getRecyclerView().getChildAt(packIndex);
                                        }
                                )
                                .setTitle("Downloading Packs")
                                .setMessage(
                                        "To download a pack you have to tap the pack you want to expand it."
                                )
                )

                /**
                 * ===========================================================================
                 * Example of downloading a pack
                 * ===========================================================================
                 */
                .add(
                        new TutorialDetail()
                                .setViewProcessor(
                                        activity -> {
                                            pager.setCurrentItem(1, false);
                                            PackDownloaderFragment downloaderFragment = managerFragment.getChildFragment(PackDownloaderFragment.class);
                                            int packIndex = 0;

                                            downloaderFragment.getAdapter().expand(packIndex);

                                            return downloaderFragment.getRecyclerView().getChildAt(packIndex + 1);
                                        }
                                )
                                .setTitle("Pack Triggers")
                                .setMessage(
                                        "In this panel is the Pack History and the Download buttons."
                                                + "\n\n\u2022 The Pack History button (Left) will display a dialog that shows all previous versions of the pack. Tapping an old version will download the pack."
                                                + "\n\u2022 The Download button (Middle) will download the latest pack and if successful it will show up in the Pack Selector page"
                                                + "\n\u2022 The Changelog button (Middle) will display all of the changelogs that the pack has been released with"
                                )
                )

                .build();
    }
}
