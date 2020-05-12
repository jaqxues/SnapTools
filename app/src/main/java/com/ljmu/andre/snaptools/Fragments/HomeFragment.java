package com.ljmu.andre.snaptools.Fragments;

import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Fragments.Tutorials.HomeTutorial;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOWN_ANDROID_P_WARNING;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

/**
 * Class modified by Ethan J on 8/6/2017
 */

public class HomeFragment extends FragmentHelper {
    public static final String TAG = "Home";
    @IdRes
    public static final int MENU_ID = R.id.nav_home;
    private static final List<TutorialDetail> TUTORIAL_DETAILS = HomeTutorial.getTutorials();

    @BindView(R.id.auth_panel)
    LinearLayout authPanel;
    @BindView(R.id.home_logo)
    ImageView logo;
    @BindView(R.id.txt_app_version)
    TextView txtAppVersion;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_home, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);

        txtAppVersion.setText(BuildConfig.VERSION_NAME);
        setTutorialDetails(TUTORIAL_DETAILS);

        //resizeThis();

        if (!(boolean) getPref(SHOWN_ANDROID_P_WARNING) && VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DialogFactory.createErrorDialog(
                    getActivity(),
                    getString(R.string.android_p_warning_title),
                    getString(R.string.android_p_warning_message)
            ).show();

            putPref(SHOWN_ANDROID_P_WARNING, true);
        }

        return layoutContainer;
    }

    private void replaceFragmentContainer(Fragment newFragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_panel, newFragment);
        transaction.commit();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @IdRes
    @Override
    public Integer getMenuId() {
        return MENU_ID;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.soleUnregister(this);
    }

    @Override
    protected boolean shouldForceTutorial() {
        return false;
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

	/*@OnClick(R.id.btn_crash) public void onViewClicked() {
		//Timber.e("Random stuff");
		//SlackUtils.uploadToSlack("Test", "Test");
		try {
			throw new HookNotFoundException("Test Hook Not Found");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}*/
}
