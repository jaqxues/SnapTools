package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Children.ChatConversationFragment;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Children.ChatSettingsFragment;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.dp;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.setContainerPadding;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatContentsFragment extends FragmentHelper {
    private DachshundTabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layoutContainer = new LinearLayout(getContext());
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        layoutContainer.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        );

        initTabLayout(layoutContainer);
        initViewPager(layoutContainer);
        return layoutContainer;
    }

    private void initTabLayout(LinearLayout layoutContainer) {
        tabLayout = new DachshundTabLayout(getActivity());
        tabLayout.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40, getActivity()))
        );
        tabLayout.setBackgroundResource(
                getDrawable(getActivity(), "tab_border")
        );
        tabLayout.setSelectedTabIndicatorColor(
                ContextCompat.getColor(
                        getActivity(),
                        getColor(getActivity(), "primaryLight")
                )
        );
        tabLayout.setTabTextColors(
                ContextCompat.getColor(
                        getActivity(),
                        getColor(getActivity(), "textTertiary")
                ),
                ContextCompat.getColor(
                        getActivity(),
                        getColor(getActivity(), "primaryLight")
                )
        );

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP)
            tabLayout.setElevation(4);

        layoutContainer.addView(tabLayout);
    }

    private void initViewPager(LinearLayout layoutContainer) {
        viewPager = new ViewPager(getActivity());
        viewPager.setId(getIdFromString(getName()));
        setContainerPadding(viewPager, 10);
        viewPager.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        ChatPagerAdapter adapter =
                new ChatPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        layoutContainer.addView(viewPager);
    }

    @Override
    public String getName() {
        return "Chat Contents";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }

    public static class ChatPagerAdapter extends FragmentPagerAdapter {
        ChatPagerAdapter(FragmentManager fm) {
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
                    return "Conversations";
                case 1:
                    return "Settings";
                default:
                    throw new IllegalArgumentException("Item Position: " + position);
            }
        }

        @Override
        public Fragment getItem(int position) {
            Timber.d("Getting item: " + position);

            switch (position) {
                case 0:
                    return new ChatConversationFragment();
                case 1:
                    return new ChatSettingsFragment();
                default:
                    throw new IllegalArgumentException("Item Position: " + position);
            }
        }
    }
}
