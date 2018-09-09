package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ljmu.andre.snaptools.Dialogs.Content.PackUpdate;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.DeviceIdManager;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.Translation.Translator.translate;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.IGNORED_PACK_UPDATE_VERSION;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.PACK_UPDATE_AVAILABLE_TITLE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CheckPackUpdate {
    public static final String TAG = "check_updates";
    private static final String CHECK_UPDATE_URL = "https://snaptools.org/SnapTools/Scripts/check_version.php";

    // TODO Setup JSON Error=True detection
    public static void performCheck(
            @Nullable Activity activity,
            @NonNull String packType,
            @NonNull String snapVersion,
            @NonNull String moduleVersion,
            @NonNull String packName,
            @NonNull String packFlavour) {

        String device_id = DeviceIdManager.getDeviceId(activity);
        Assert.notNull("Null DeviceId", device_id);

        new WebRequest.Builder()
                .setUrl(CHECK_UPDATE_URL)
                .setTag(TAG)
                .setPacketClass(PackDataPacket.class)
                .shouldClearCache(true)
                .setContext(activity)
                // ===========================================================================
                .addParam("device_id", device_id)
                .addParam("pack_type", packType)
                .addParam("sc_version", snapVersion)
                .addParam("allow_dev", String.valueOf(STApplication.DEBUG))
                .addParam("pack_flavour", packFlavour)
                // ===========================================================================
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        PackDataPacket packDataPacket = webResponse.getResult();

                        String lastIgnoredVer = getPref(IGNORED_PACK_UPDATE_VERSION);

                        if (lastIgnoredVer.equals(packDataPacket.getModVersion()))
                            return;

                        int versionOffset = MiscUtils.versionCompare(packDataPacket.getModVersion(), moduleVersion);

                        boolean hasUpdate = versionOffset > 0;

                        if (hasUpdate) {
                            packDataPacket.setCurrentModVersion(moduleVersion);
                            packDataPacket.setPackName(packName);

                            new ThemedDialog(activity)
                                    .setTitle(translate(PACK_UPDATE_AVAILABLE_TITLE))
                                    .setExtension(
                                            new PackUpdate()
                                                    .setActivity(activity)
                                                    .setPackDataPacket(packDataPacket)
                                    )
                                    .show();
                        }
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        if (webResponse.getException() != null)
                            Timber.e(webResponse.getException(), webResponse.getMessage());
                        else
                            Timber.w(webResponse.getMessage());
                    }
                })
                .performRequest();
    }
}
