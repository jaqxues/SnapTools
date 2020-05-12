package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ReqLoadChatFragmentEvent;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Utils.Constants;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.removeClipping;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatManagerFragment extends FragmentHelper {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.soleRegister(this);

        LinearLayout layoutContainer = new LinearLayout(getContext());
        removeClipping(layoutContainer);
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        layoutContainer.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        );

        FrameLayout contents = new FrameLayout(getActivity());
        removeClipping(contents);
        contents.setId(getIdFromString("Contents"));
        layoutContainer.addView(contents);

        replaceFragmentContainer(new ChatContentsFragment());

        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void replaceFragmentContainer(FragmentHelper newFragment) {
        replaceFragmentContainer(newFragment, false);
    }

    private void replaceFragmentContainer(FragmentHelper newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(getIdFromString("Contents"), newFragment);
        transaction.commit();

        if (addToBackStack)
            transaction.addToBackStack("Contents");

        Timber.d("BackStack: " + getFragmentManager().getBackStackEntryCount());

        if (Constants.getApkVersionCode() >= 69)
            redirector = newFragment.getRedirector();
    }

    @Subscribe
    public void handleReqLoadChatFragmentEvent(ReqLoadChatFragmentEvent loadEvent) {
        replaceFragmentContainer(loadEvent.getChatFragment(), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("Destroyed");
        EventBus.soleUnregister(this);
    }


    @Override
    public String getName() {
        return "Chat Manager";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
