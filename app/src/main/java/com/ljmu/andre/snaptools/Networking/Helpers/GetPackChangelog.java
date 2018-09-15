package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.ljmu.andre.snaptools.Exceptions.KeyNotFoundException;
import com.ljmu.andre.snaptools.Networking.NetworkUtils;
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.PacketResultListener;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetPackChangelog {
    public static final String TAG = "get_changelog";
    private static final String GET_CHANGELOG_BASE_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Packs/JSON/";

    public static void performCheck(
            @Nullable Activity activity,
            @NonNull String packType,
            @NonNull String snapVersion,
            @NonNull String packFlavour,
            PacketResultListener<PackDataPacket> packetResultListener) {

        new WebRequest.Builder()
                .setUrl(getChangelogUrl(snapVersion))
                .setTag(TAG)
                .setMethod(Request.Method.GET)
                .setType(WebRequest.RequestType.STRING)
                .shouldClearCache(true)
                .setContext(activity)
                // ===========================================================================
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        PackDataPacket packDataPacket;
                        try {
                            packDataPacket = NetworkUtils.extractPack(webResponse.getResult(), PackDataPacket.class, packFlavour);
                        } catch (KeyNotFoundException e) {
                            packetResultListener.error(
                                    "No changelog for this Version",
                                    e,
                                    204
                            );
                            return;
                        }
                        if (packDataPacket == null) {
                            packetResultListener.error(
                                    "Received Empty Result!",
                                    null,
                                    203
                            );

                            return;
                        }

                        if (packDataPacket.banned) {
                            packetResultListener.error(
                                    packDataPacket.getBanReason(),
                                    null,
                                    105
                            );

                            return;
                        }

                        packDataPacket.setPackType(packType);
                        packDataPacket.setScVersion(snapVersion);

                        packetResultListener.success(
                                "Success",
                                packDataPacket
                        );
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        if (webResponse.getException() != null)
                            Timber.e(webResponse.getException(), webResponse.getMessage());
                        else
                            Timber.w(webResponse.getMessage());

                        packetResultListener.error(
                                webResponse.getMessage(),
                                webResponse.getException(),
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();
    }

    private static String getChangelogUrl(String snapVersion) {
        return GET_CHANGELOG_BASE_URL + "PackChangelog_SC_v" + snapVersion + ".json";
    }
}
