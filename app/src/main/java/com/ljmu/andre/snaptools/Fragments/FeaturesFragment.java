package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Fragments.ViewProviders.FeaturesViewProvider;
import com.ljmu.andre.snaptools.Networking.Helpers.GetFeatures;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FeaturesFragment extends FragmentHelper {
    private FeaturesViewProvider viewProvider;

    // ===========================================================================
    private ViewGroup mainContainer;
    private ViewGroup basicContent;
    private ViewGroup premiumContent;
    private List<SwipeRefreshLayout> refreshLayouts;
    // ===========================================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.soleRegister(this);

        viewProvider = new FeaturesViewProvider(
                getActivity(),
                viewGroup -> basicContent = viewGroup,
                viewGroup -> premiumContent = viewGroup
        );

        mainContainer = viewProvider.getMainContainer();

        refreshLayouts = new ArrayList<>(2);
        refreshLayouts.add(getDSLView(basicContent, "basic_refresh"));
        refreshLayouts.add(getDSLView(premiumContent, "premium_refresh"));
        assignRefreshListeners();

        // Super lazy code practice here =============================================
        basicContent = getDSLView(basicContent, "content_basic");
        premiumContent = getDSLView(premiumContent, "content_premium");

        buildFeaturesContent(false);

        return mainContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayouts.clear();
    }

    private void assignRefreshListeners() {
        for (SwipeRefreshLayout refreshLayout : refreshLayouts) {
            refreshLayout.setOnRefreshListener(() -> {
                setRefreshing(true);
                buildFeaturesContent(true);
            });
        }
    }

    private void setRefreshing(boolean isRefreshing) {
        for (SwipeRefreshLayout refreshLayout : refreshLayouts) {
            refreshLayout.setRefreshing(isRefreshing);
        }
    }

    private void buildFeaturesContent(boolean bypassCache) {
        setRefreshing(true);

        new GetFeatures()
                .setBypassCache(bypassCache)
                .smartFetch(
                        getActivity(),
                        new ObjectResultListener<byte[]>() {
                            @Override
                            public void success(String message, byte[] object) {
                                setRefreshing(false);

                                String cachedFileContent = new String(object, Charset.defaultCharset());

                                try {
                                    String[] splitString = cachedFileContent.split("\n");
                                    Timber.d("Split: " + Arrays.toString(splitString));

                                    handleFeaturesResult(splitString);
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            }

                            @Override
                            public void error(String message, Throwable t, int errorCode) {
                                setRefreshing(false);

                                DialogFactory.createErrorDialog(
                                        getActivity(),
                                        "Error fetching features list",
                                        "Failed to fetch the Features List"
                                ).show();
                            }
                        }
                );
    }

    private void handleFeaturesResult(String[] featuresArray) {
        premiumContent.removeAllViews();
        basicContent.removeAllViews();

        ViewGroup currentBasicHeader = null;
        ViewGroup currentPremiumHeader = null;

        for (String featuresItem : featuresArray) {
            if (featuresItem == null || featuresItem.isEmpty())
                continue;

            Timber.d("Working on item: " + featuresItem);

            String item = featuresItem.substring(1);

            if (item.startsWith("--")) {
                item = item.substring(2);
                boolean attachToRoot = false;

                if (item.startsWith("<root>")) {
                    attachToRoot = true;
                    item = item.substring(6);
                }

                TextView itemText = new TextView(getActivity());
                itemText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                itemText.setTextAppearance(getActivity(), R.style.DefaultText);
                itemText.setText(getSpannedHtml(item));

                if (item.startsWith("<center>")) {
                    item = item.substring(8);
                    itemText.setGravity(Gravity.CENTER);
                }

                if (featuresItem.startsWith("/")) {
                    if (attachToRoot) {
                        premiumContent.addView(itemText);
                        continue;
                    }

                    if (currentPremiumHeader == null)
                        throw new IllegalStateException("No premium header defined");

                    currentPremiumHeader.addView(itemText);
                } else if (featuresItem.startsWith("\\")) {
                    if (attachToRoot) {
                        basicContent.addView(itemText);
                        continue;
                    }

                    if (currentBasicHeader == null)
                        throw new IllegalStateException("No basic header defined");

                    currentBasicHeader.addView(itemText);
                }
            } else if (item.startsWith("-")) {
                item = item.substring(1);

                ViewGroup headerItem = viewProvider.getHeader(item);

                if (featuresItem.startsWith("/")) {
                    currentPremiumHeader = getDSLView(headerItem, "header_feature_container");
                    premiumContent.addView(headerItem);
                } else if (featuresItem.startsWith("\\")) {
                    currentBasicHeader = getDSLView(headerItem, "header_feature_container");
                    basicContent.addView(headerItem);
                }
            }
        }

        AnimationUtils.sequentGroup(basicContent);
        AnimationUtils.sequentGroup(premiumContent);
    }

    @Override
    public String getName() {
        return "Features";
    }

    @Override
    public Integer getMenuId() {
        return R.id.nav_features;
    }
}
