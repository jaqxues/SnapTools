package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.ljmu.andre.snaptools.Dialogs.Content.PackUpdate;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Exceptions.KeyNotFoundException;
import com.ljmu.andre.snaptools.Networking.NetworkUtils;
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.Translation.Translator.translate;
import static com.ljmu.andre.snaptools.Networking.WebRequest.RequestType.STRING;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.IGNORED_PACK_UPDATE_VERSION;
import static com.ljmu.andre.snaptools.Utils.TranslationDef.PACK_UPDATE_AVAILABLE_TITLE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CheckPackUpdate {
    public static final String TAG = "check_updates";
    private static final String CHECK_UPDATE_BASE_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Packs/JSON/PackUpdates/";

    // TODO Setup JSON Error=True detection
    public static void performCheck(
            @Nullable Activity activity,
            @NonNull String packType,
            @NonNull String snapVersion,
            @NonNull String moduleVersion,
            @NonNull String packName,
            @NonNull String packFlavour) {

        new WebRequest.Builder()
                .setUrl(getPackUpdateCheckUrl(snapVersion))
                .setTag(TAG)
                .setType(STRING)
                .shouldClearCache(true)
                .setContext(activity)
                .setMethod(Request.Method.GET)
                // ===========================================================================
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        PackDataPacket packDataPacket;
                        try {
                            packDataPacket = NetworkUtils.extractPack(webResponse.getResult(), PackDataPacket.class, packFlavour);
                        } catch (KeyNotFoundException e) {
                            Timber.d(e, "Key %s not found", packFlavour);
                            Timber.e("Could not determine latest Pack Version, Flavour %s, SnapVersion: %s", packFlavour, snapVersion);
                            return;
                        }

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

    private static String getPackUpdateCheckUrl(String snapVersion) {
        return
                CHECK_UPDATE_BASE_URL + "LatestPack_SC_v" + snapVersion + ".json";
    }
}
