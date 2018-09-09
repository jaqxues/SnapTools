package com.ljmu.andre.snaptools.Dialogs.Content;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedDialogExtension;
import com.ljmu.andre.snaptools.Networking.Helpers.DownloadModulePack;
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket;
import com.ljmu.andre.snaptools.Utils.ContextHelper;

import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.IGNORED_PACK_UPDATE_VERSION;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackUpdate implements ThemedDialogExtension {
    private Activity activity;
    private PackDataPacket packDataPacket;

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
                        "Snapchat Version: " + packDataPacket.getSCVersion()
                                + "\nPack Type: " + packDataPacket.getPackType()
                                + "\nInstalled Version: " + packDataPacket.getCurrentModVersion()
                                + "\nLatest Version: " + packDataPacket.getModVersion()
                                + "\n\nRelease Notes:\n" + packDataPacket.getChangelog()
                )
        );

        btnIgnoreVersion.setOnClickListener(
                v -> {
                    putPref(IGNORED_PACK_UPDATE_VERSION, packDataPacket.getModVersion());
                    themedDialog.dismiss();
                }
        );

        btnDismiss.setOnClickListener(
                v -> themedDialog.dismiss()
        );

        btnUpdate.setOnClickListener(
                v -> updatePack(themedDialog)
        );
    }

    private void updatePack(ThemedDialog themedDialog) {
        new DownloadModulePack(
                ContextHelper.getActivity(),
                packDataPacket.getPackName(),
                packDataPacket.getSCVersion(),
                packDataPacket.getPackType(),
                packDataPacket.isDevelopment(),
                packDataPacket.getModVersion(),
                packDataPacket.getFlavour()
        ).download(
                (state, message, outputFile, responseCode) -> {
                    themedDialog.dismiss();

                    if (state) {
                        DialogFactory.createBasicMessage(
                                activity,
                                "Pack Downloaded",
                                message
                        ).show();
                    } else {
                        failedDownload(message, responseCode);
                    }
                }
        );
    }

    private void failedDownload(String reason, int responseCode) {
        DialogFactory.createErrorDialog(
                activity,
                "Download Failed",
                "Downloading Pack failed:"
                        + "\n" + reason,
                responseCode
        ).show();
    }

    public PackUpdate setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public PackUpdate setPackDataPacket(PackDataPacket packDataPacket) {
        this.packDataPacket = packDataPacket;
        return this;
    }
}
