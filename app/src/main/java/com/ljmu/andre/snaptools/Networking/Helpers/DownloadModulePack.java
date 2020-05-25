package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent.DownloadState;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.Exceptions.NullObjectException;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Networking.Helpers.DownloadFile.DownloadListener;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.PathProvider;

import java.io.File;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DownloadModulePack implements DownloadListener {
    private static final String VOLLEY_TAG = "pack_download";
    private static final String PACK_BASE_URL = "https://github.com/jaqxues/SnapTools_DataProvider/blob/master/Packs/Files/";

    private Activity activity;
    private String packName;
    private String snapVersion;
    private String packType;
    private boolean development;
    @Nullable
    private String packVersion;
    private String flavour;

    @Nullable
    private DownloadListener sourceListener;
    private ThemedDialog progressDialog;

    public DownloadModulePack(Activity activity, String packName, String snapVersion, String packType,
                              boolean development, @Nullable String packVersion, String flavour) {
        this.activity = activity;
        this.packName = packName;
        this.snapVersion = snapVersion;
        this.packType = packType;
        this.development = development;
        this.packVersion = packVersion;
        this.flavour = flavour;

        Assert.notNull("Missing download parameters: "
                + toString(), activity, packName, snapVersion, packType);
    }

    public void download(DownloadListener downloadListener) {
        sourceListener = downloadListener;
        download();
    }

    public void download() {
        progressDialog = new ThemedDialog(ContextHelper.getActivity())
                .setTitle("Downloading Pack")
                .setExtension(
                        new Progress()
                                .setMessage("Downloading ModulePack")
                                .setVolleyTag(VOLLEY_TAG)
                );

        new DownloadFile()
                .setUrl(getDownloadUrl())
                .setMethod(Request.Method.GET)
                .setContext(activity)
                .setVolleyTag(VOLLEY_TAG)
                .setDirectory(PathProvider.getModulesPath())
                .setFilename(packName + ".jar")
                // ===========================================================================
                .addDownloadListener(this)
                .download();

        progressDialog.show();
    }

    private String getDownloadUrl() {
        return PACK_BASE_URL + packName + ".jar?raw=true";
    }

    @Override
    public void downloadFinished(boolean state, String message, @Nullable File outputFile, int responseCode) {
        if (progressDialog != null)
            progressDialog.dismiss();

        PackMetaData metaData = null;

        if (outputFile != null) {
            try {
                metaData = PackUtils.getPackMetaData(outputFile, PackEventRequest.EventHandler.ignoreEvents);
            } catch (NullObjectException e) {
                state = false;
                message = e.getMessage();
            }
        }

        if (sourceListener != null)
            sourceListener.downloadFinished(state, message, outputFile, -1);

        EventBus.getInstance().post(
                new PackDownloadEvent()
                        .setState(state ? DownloadState.SUCCESS : DownloadState.FAIL)
                        .setMessage(message)
                        .setMetaData(metaData)
                        .setOutputFile(outputFile)
                        .setResponseCode(responseCode)
        );
    }
}
