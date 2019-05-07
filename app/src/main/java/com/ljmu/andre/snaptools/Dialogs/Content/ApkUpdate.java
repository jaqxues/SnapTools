package com.ljmu.andre.snaptools.Dialogs.Content;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.javiersantos.appupdater.objects.Update;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Networking.Helpers.CheckAPKUpdate;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.IGNORED_UPDATE_VERSION_CODE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TEMP_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ApkUpdate implements ThemedDialog.ThemedDialogExtension {
    private Activity activity;
    private Update update;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context modContext = inflater.getContext();

        inflater.inflate(getLayout(modContext, "dialog_update"), content, true);

        TextView txtMessage = getView(content, getId(modContext, "txt_message"));
        Button btnIgnoreVersion = getView(content, getId(modContext, "btn_ignore_update"));
        Button btnDismiss = getView(content, getId(modContext, "btn_dismiss"));
        Button btnUpdate = getView(content, getId(modContext, "btn_update"));

        txtMessage.setText(
                getSpannedHtml(
                        "Installed Version: " + BuildConfig.VERSION_NAME
                                + "\nLatest Version: " + update.getLatestVersion()
                                + "\n\nRelease Notes:\n" + update.getReleaseNotes()
                )
        );

        btnIgnoreVersion.setOnClickListener(
                v -> {
                    putPref(IGNORED_UPDATE_VERSION_CODE, update.getLatestVersionCode());
                    themedDialog.dismiss();
                }
        );

        btnDismiss.setOnClickListener(
                v -> themedDialog.dismiss()
        );

        btnUpdate.setOnClickListener(
                v -> {
                    themedDialog.dismiss();
                    CheckAPKUpdate.updateApk(
                            activity,
                            update.getUrlToDownload().toString(),
                            getPref(TEMP_PATH),
                            "SnapTools_" + update.getLatestVersion() + ".apk"
                    );
                }
        );
    }

    public ApkUpdate setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public ApkUpdate setUpdate(Update update) {
        this.update = update;
        return this;
    }
}
