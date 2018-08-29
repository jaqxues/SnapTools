package com.ljmu.andre.snaptools.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.RemoteConfigDefaults;
import timber.log.Timber;

import java.util.Map;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SupportFragment extends FragmentHelper {
    public static final String TAG = "Support";
    public static final int MENU_ID = R.id.nav_support;
    Unbinder unbinder;
    @BindView(R.id.txt_discord)
    TextView txtDiscord;
    @BindView(R.id.txt_server)
    TextView txtServer;
    @BindView(R.id.txt_reddit)
    TextView txtReddit;
    @BindView(R.id.txt_xda)
    TextView txtXda;
    @BindView(R.id.txt_twitter)
    TextView txtTwitter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_support, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);
        Timber.d("CreateView");

        Map<String, Object> defaultConfig = RemoteConfigDefaults.get();
        txtDiscord.setText(getSpannedHtml((String) defaultConfig.get("support_discord_text")));
        txtServer.setText(getSpannedHtml((String) defaultConfig.get("support_website_text")));
        txtReddit.setText(getSpannedHtml((String) defaultConfig.get("support_reddit_text")));
        txtXda.setText(getSpannedHtml((String) defaultConfig.get("support_xda_text")));
        txtTwitter.setText(getSpannedHtml((String) defaultConfig.get("support_twitter_text")));

        return layoutContainer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Integer getMenuId() {
        return MENU_ID;
    }

    @OnClick({R.id.layout_discord, R.id.layout_server, R.id.layout_reddit, R.id.layout_xda, R.id.layout_twitter})
    public void onViewClicked(View view) {
        Intent linkIntent;

        switch (view.getId()) {
            case R.id.layout_discord:
                DialogFactory.createBasicMessage(
                        getActivity(),
                        "Discord Rules Reminder",
                        "When first joining the SnapTools Discord community, you will be initially messaged by our Rules Bot. Make sure your Privacy settings allow you to receive this message."
                                + "\nOnce you follow the Rules Bots' instructions, the full community will be unlocked for you.",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                themedDialog.dismiss();

                                Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snaptools.org/discord"));
                                startActivity(linkIntent);
                            }
                        }
                ).show();
                return;
            case R.id.layout_server:
                linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snaptools.io"));
                break;
            case R.id.layout_reddit:
                linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/r/SnapTools/"));
                break;
            case R.id.layout_xda:
                linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) RemoteConfigDefaults.get().get("support_xda_link")));
                break;
            case R.id.layout_twitter:
                linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) RemoteConfigDefaults.get().get("support_twitter_link")));
                break;
            default:
                throw new IllegalArgumentException("Unknown click event");
        }

        startActivity(linkIntent);
    }
}
