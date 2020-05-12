package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.RemoteConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;

/**
 * Created by ethan on 8/10/2017.
 */

public class AboutUsFragment extends FragmentHelper {
    public static final String TAG = "About Us";
    @IdRes
    public static final int MENU_ID = R.id.nav_about;
    @BindView(R.id.concept)
    TextView concept;
    @BindView(R.id.about_us)
    TextView aboutUs;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_about_us, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);

        RemoteConfig remoteConfig = RemoteConfig.getConfig();
        concept.setText(getSpannedHtml(remoteConfig.getString("about_us_concept")));
        aboutUs.setText(getSpannedHtml(remoteConfig.getString("about_us_description")));

        return layoutContainer;
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
