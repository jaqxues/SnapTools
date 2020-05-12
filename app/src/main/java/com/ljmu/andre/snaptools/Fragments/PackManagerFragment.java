package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ItemExpandedEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.Fragments.Tutorials.PackManagerTutorial;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackManagerFragment extends FragmentHelper {
    public static final String TAG = "Pack Manager";
    public static final int MENU_ID = R.id.nav_pack_manager;
    Unbinder unbinder;
    @BindView(R.id.tab_layout)
    DachshundTabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.txt_app_version)
    TextView txtAppVersion;
    @BindView(R.id.txt_sc_version)
    TextView txtScVersion;

    private boolean hasExpanded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_pack_manager, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);
        Timber.d("CreateView");

        txtAppVersion.setText(BuildConfig.VERSION_NAME);
        assignScVersion();

        PackSettingsPagerAdapter adapter =
                new PackSettingsPagerAdapter(getChildFragmentManager());

        tabLayout.setupWithViewPager(pager, true);
        pager.setAdapter(adapter);

        Timber.d("CreateView");
        return layoutContainer;
    }

    private void assignScVersion() {
        String scVersion = MiscUtils.getInstalledSCVer(getContext());

        if (scVersion == null)
            scVersion = "Unknown";

        txtScVersion.setText(scVersion);
    }

    @Override
    public void onPause() {
        super.onPause();
        hasExpanded = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.soleUnregister(this);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Integer getMenuId() {
        return MENU_ID;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        buildTutorialList();
    }

    private void buildTutorialList() {
        LinearLayout slidingTabStrip = (LinearLayout) tabLayout.getChildAt(0);
        View selectorTab = slidingTabStrip.getChildAt(0);
        View downloaderTab = slidingTabStrip.getChildAt(1);

        setTutorialDetails(
                PackManagerTutorial.getTutorials(
                        this, selectorTab, downloaderTab, pager
                )
        );
    }

    @Override
    public List<TutorialDetail> getTutorials() {
        return tutorialDetails;
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

    @Override
    public void triggerTutorial(boolean isFullTutorial) {
        super.triggerTutorial(isFullTutorial);
        hasExpanded = false;

        PackDownloaderFragment downloaderFragment = getChildFragment(PackDownloaderFragment.class);
        downloaderFragment.triggerTutorial(isFullTutorial);
        downloaderFragment.generateTutorialData();

        PackSelectorFragment selectorFragment = getChildFragment(PackSelectorFragment.class);
        selectorFragment.triggerTutorial(isFullTutorial);
        selectorFragment.generateTutorialData();
    }

    @SuppressWarnings("RestrictedApi")
    public <T extends FragmentHelper> T getChildFragment(Class<T> helperClass) {
        for (Fragment frag : getChildFragmentManager().getFragments()) {
            if (frag.getClass().equals(helperClass))
                return (T) frag;
        }

        return null;
    }

    @Override
    public void finishTutorial() {
        super.finishTutorial();
        resetTutorialItems();
    }

    @Override
    public void closeTutorial() {
        super.closeTutorial();
        resetTutorialItems();
    }

    private void resetTutorialItems() {
        hasExpanded = false;
        PackDownloaderFragment downloaderFragment = getChildFragment(PackDownloaderFragment.class);
        downloaderFragment.resetTutorial();
        downloaderFragment.generateMetaData(false);

        PackSelectorFragment selectorFragment = getChildFragment(PackSelectorFragment.class);
        selectorFragment.resetTutorial();
        selectorFragment.generateMetaData();
    }

    @Subscribe
    public void handleItemExpandedEvent(ItemExpandedEvent expandedEvent) {
        Timber.d("Item Expanded: " + expandedEvent);

        if (runningTutorial && !hasExpanded &&
                expandedEvent.getExpandedItem() instanceof ServerPackMetaData) {
            hasExpanded = true;
            progressTutorial();
        }
    }

    @Subscribe
    public void handlePackEventRequest(PackEventRequest eventRequest) {
        EventRequest request = eventRequest.getRequest();
        Timber.d("New Event Request: " + request.toString());

        switch (request) {
            case DOWNLOAD_TUTORIAL:
                DialogFactory.createConfirmation(
                        getActivity(),
                        "Download Pack",
                        "Are you sure you wish to download pack: " + eventRequest.getPackName(),
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                progressTutorial();
                                themedDialog.dismiss();
                            }
                        },
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                showCancelTutorialDialog();
                                themedDialog.dismiss();
                            }
                        }
                ).show();
                break;
            default:
                Timber.d("Ignoring Unhandled Request: " + request);
        }
    }

    private void showCancelTutorialDialog() {
        DialogFactory.createConfirmation(
                getActivity(),
                "Can't continue tutorial",
                "You must successfully install the pack before continuing\n\n" +
                        "Press No to end tutorial / Yes to try again",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                    }
                },
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        finishTutorial();
                        themedDialog.dismiss();
                    }
                }
        ).show();
    }

    public static class PackSettingsPagerAdapter extends FragmentPagerAdapter {
        PackSettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pack Selector";
                case 1:
                    return "Pack Downloader";
                default:
                    throw new IllegalArgumentException("Item Position: " + position);
            }
        }

        @Override
        public Fragment getItem(int position) {
            Timber.d("Getting item: " + position);

            switch (position) {
                case 0:
                    return new PackSelectorFragment();
                case 1:
                    return new PackDownloaderFragment();
                default:
                    throw new IllegalArgumentException("Item Position: " + position);
            }
        }
    }
}
