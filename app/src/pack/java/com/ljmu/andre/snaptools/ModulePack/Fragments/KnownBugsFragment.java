package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.BulletSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.KnownBugObject;
import com.ljmu.andre.snaptools.ModulePack.Networking.Helpers.GetKnownBugs;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Constants;

import java.util.List;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.LAST_CHECK_KNOWN_BUGS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.dp;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.setContainerPadding;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getStyle;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class KnownBugsFragment extends FragmentHelper {
    private static String scVersion;
    private static String packVersion;
    private LinearLayout bugsContainer;
    private TextView txtLastChecked;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bugsContainer = new LinearLayout(getContext());
        bugsContainer.setOrientation(LinearLayout.VERTICAL);
        setContainerPadding(bugsContainer);

        LinearLayout mainContainer = new LinearLayout(getActivity());
        mainContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mainContainer.setOrientation(LinearLayout.VERTICAL);

        LayoutParams scrollParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        scrollParams.weight = 1;
        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(scrollParams);
        scrollView.addView(bugsContainer);

        mainContainer.addView(scrollView);

        mainContainer.addView(
                txtLastChecked = ViewFactory.getDefaultTextView(
                        getActivity(),
                        "",
                        12f,
                        getStyle(getActivity(), "DefaultText"),
                        Gravity.CENTER
                )
        );

        updateLastChecked();

        initBugs();

        return mainContainer;
    }

    private void initBugs() {
        GetKnownBugs.getBugs(
                getActivity(),
                scVersion,
                packVersion,
                new WebResponse.ObjectResultListener<List<KnownBugObject>>() {
                    @Override
                    public void success(String message, List<KnownBugObject> bugObjects) {
                        if (getActivity() == null || getActivity().isFinishing())
                            return;

                        if (bugObjects == null || bugObjects.isEmpty()) {
                            SafeToastAdapter.showErrorToast(
                                    getActivity(),
                                    "No KnownBugs Found"
                            );
                            return;
                        }

                        for (KnownBugObject bugObject : bugObjects) {
                            createAndAttachBug(bugObject);
                        }

                        if (Constants.getApkVersionCode() >= 66)
                            AnimationUtils.sequentGroup(bugsContainer);

                        updateLastChecked();
                    }

                    @Override
                    public void error(String message, Throwable t, int errorCode) {
                        if (getActivity() == null || getActivity().isFinishing())
                            return;

                        DialogFactory.createErrorDialog(
                                getActivity(),
                                "Known Bug Fetching Failed",
                                message,
                                errorCode
                        ).show();
                    }
                }
        );
    }

    private void createAndAttachBug(KnownBugObject bugObject) {
        bugsContainer.addView(
                ViewFactory.getHeaderLabel(
                        getActivity(),
                        bugObject.category
                )
        );

        bugsContainer.addView(generateBugLayout(bugObject.bugs));
    }

    private LinearLayout generateBugLayout(List<String> bugs) {
        int bugContainerMargin = dp(10, getActivity());
        LayoutParams bugContainerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bugContainerParams.setMargins(0, bugContainerMargin, 0, bugContainerMargin * 2);

        int bugMargin = dp(10, getActivity());
        LayoutParams bugParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bugParams.setMargins(bugMargin, 0, 0, bugMargin);

        LinearLayout bugContainer = new LinearLayout(getContext());
        bugContainer.setOrientation(LinearLayout.VERTICAL);
        bugContainer.setLayoutParams(bugContainerParams);

        int bulletGap = dp(10, getActivity());
        for (String bug : bugs) {
            SpannableString span = new SpannableString(bug);
            span.setSpan(new BulletSpan(bulletGap), 0, bug.length(), 0);

            TextView txtBug = new TextView(getActivity());
            txtBug.setLayoutParams(bugParams);
            txtBug.setText(span);
            bugContainer.addView(txtBug);
        }

        return bugContainer;
    }

	/*
	private void initSavingBugs(LinearLayout layoutContainer) {
		layoutContainer.addView(
				ViewFactory.getHeaderLabel(
						getActivity(),
						"Saving Bugs"
				)
		);

		layoutContainer.addView(generateBugLayout(SAVING_BUGS));
	}

	private void initLensCollectorBugs(LinearLayout layoutContainer) {
		layoutContainer.addView(
				ViewFactory.getHeaderLabel(
						getActivity(),
						"Lens System Bugs"
				)
		);

		layoutContainer.addView(generateBugLayout(LENS_BUGS));
	}

	private void initChatSavingBugs(LinearLayout layoutContainer) {
		layoutContainer.addView(
				ViewFactory.getHeaderLabel(
						getActivity(),
						"Chat Saving Bugs"
				)
		);

		layoutContainer.addView(generateBugLayout(CHAT_BUGS));
	}

	private void initSharingBugs(LinearLayout layoutContainer) {
		layoutContainer.addView(
				ViewFactory.getHeaderLabel(
						getActivity(),
						"Sharing Bugs"
				)
		);

		layoutContainer.addView(generateBugLayout(SHARING_BUGS));
	}

	private void initCompatibilityBugs(LinearLayout layoutContainer) {
		layoutContainer.addView(
				ViewFactory.getHeaderLabel(
						getActivity(),
						"Compatibility Bugs"
				)
		);

		layoutContainer.addView(generateBugLayout(COMPATIBILITY_BUGS));
	}*/

    private void updateLastChecked() {
        Long lastCheckedTimestamp = getPref(LAST_CHECK_KNOWN_BUGS);

        if (lastCheckedTimestamp == 0L) {
            if (txtLastChecked != null)
                txtLastChecked.setVisibility(View.GONE);
            return;
        }

        if (txtLastChecked == null)
            return;

        String formattedTime = (String) DateUtils.getRelativeDateTimeString(
                getActivity(),
                lastCheckedTimestamp,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );

        txtLastChecked.setText(getSpannedHtml(
                "Last Checked: " + htmlHighlight(formattedTime)
        ));
        txtLastChecked.setVisibility(View.VISIBLE);
    }

    public KnownBugsFragment buildMetaData(ModulePack modulePack) {
        scVersion = modulePack.getPackMetaData().getScVersion();
        packVersion = modulePack.getPackMetaData().getPackVersion();
        return this;
    }

    @Override
    public String getName() {
        return "Known Bugs";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
