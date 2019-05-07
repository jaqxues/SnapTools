package com.ljmu.andre.snaptools.Dialogs.Content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackChangelog implements ThemedDialog.ThemedDialogExtension {
    private String scVersion;
    private String packType;
    private String releaseNotes;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        Context modContext = inflater.getContext();

        inflater.inflate(getLayout(modContext, "dialog_pack_changelog"), content, true);

        TextView txtMessage = getView(content, getId(modContext, "txt_message"));

        txtMessage.setText(
                getSpannedHtml(
                        "Snapchat Version: " + scVersion
                                + "\nPack Type: " + packType
                                + "\n\nRelease Notes:\n" + releaseNotes
                )
        );

        getView(content, getId(modContext, "btn_okay")).setOnClickListener(
                v -> themedDialog.dismiss()
        );
    }

    public PackChangelog setSCVersion(String scVersion) {
        this.scVersion = scVersion;
        return this;
    }

    public PackChangelog setPackType(String packType) {
        this.packType = packType;
        return this;
    }

    public PackChangelog setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
        return this;
    }
}
