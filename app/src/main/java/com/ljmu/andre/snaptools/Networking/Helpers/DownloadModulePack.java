package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import android.support.annotation.Nullable;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent.DownloadState;
import com.ljmu.andre.snaptools.Exceptions.NullObjectException;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Networking.Helpers.DownloadFile.DownloadListener;
import com.ljmu.andre.snaptools.RedactedClasses.Answers;
import com.ljmu.andre.snaptools.RedactedClasses.CustomEvent;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.DeviceIdManager;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import timber.log.Timber;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Networking.WebRequest.assertParam;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.KILL_SC_ON_CHANGE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.MODULES_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DownloadModulePack implements DownloadListener {
    private static final String VOLLEY_TAG = "pack_download";
    private static final String PACK_REQUEST_URL = "https://snaptools.org/SnapTools/Scripts/download_pack.php";

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
        Class cls = DownloadModulePack.class;
        String deviceId;

        try {
            deviceId = assertParam(cls, "Invalid Device ID", DeviceIdManager.getDeviceId(activity));
        } catch (IllegalArgumentException e) {
            Timber.e(e);
            downloadFinished(
                    false,
                    "Missing Authentication Parameters",
                    null,
                    202
            );
            return;
        }

        progressDialog = new ThemedDialog(ContextHelper.getActivity())
                .setTitle("Downloading Pack")
                .setExtension(
                        new Progress()
                                .setMessage("Downloading ModulePack")
                                .setVolleyTag(VOLLEY_TAG)
                );

        Map<String, String> params = new HashMap<>();

        if (packVersion != null) {
            params.put("pack_version",
                    packVersion);
        }

        new DownloadFile()
                .setUrl(PACK_REQUEST_URL)
                .setContext(activity)
                .setVolleyTag(VOLLEY_TAG)
                .setDirectory(getPref(MODULES_PATH))
                .setFilename(packName + ".jar")
                // ===========================================================================
                .setParams(params)
                .addParam("device_id", deviceId)
                .addParam("sc_version", snapVersion)
                .addParam("pack_type", packType)
                .addParam("development", development ? "1" : "0")
                .addParam("pack_flavour", flavour)
                // ===========================================================================
                .addDownloadListener(this)
                .download();

        progressDialog.show();
    }

    @Override
    public void downloadFinished(boolean state, String message, @Nullable File outputFile, int responseCode) {
        if (progressDialog != null)
            progressDialog.dismiss();

        PackMetaData metaData = null;

        if (outputFile != null) {
            try {
                metaData = PackUtils.getPackMetaData(outputFile);

                if (getPref(KILL_SC_ON_CHANGE))
                    PackUtils.killSCService(activity);
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

        if (state) {
            Answers.safeLogEvent(
                    new CustomEvent("Pack Download")
                            .putCustomAttribute("Snap Version", snapVersion)
                            .putCustomAttribute("Pack Type", packType)
                            .putCustomAttribute("Pack Version", packVersion)
                            .putCustomAttribute("Development", String.valueOf(development))
                            .putCustomAttribute("Flavour", flavour)
            );
        }
    }
}
