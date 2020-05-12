package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljmu.andre.snaptools.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by ethan on 8/16/2017.
 */

public class LegalFragment extends FragmentHelper {
    private Unbinder unbinder;

    @Override
    public String getName() {
        return "Legal";
    }

    @Override
    public Integer getMenuId() {
        return R.id.nav_legal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_legal, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);

        return layoutContainer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

	/*	@OnClick(R.id.testNotify) public void onViewClicked() {
		NotificationLEDUtil.flashLED(getActivity(), NotificationColor.RED);
	}*/
}
