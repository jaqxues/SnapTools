package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import com.ljmu.andre.snaptools.Networking.Packets.PackHistoryListPacket;
import com.ljmu.andre.snaptools.Networking.Packets.PackHistoryObject;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ServerListResultListener;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.DeviceIdManager;
import timber.log.Timber;

import java.util.Collections;

import static com.ljmu.andre.snaptools.Networking.WebRequest.assertParam;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetPackHistory {
    private static final String GET_PACKS_URL = "https://snaptools.org/SnapTools/Scripts/get_pack_history.php";

    public static void getPacksFromServer(Activity activity, String scVersion, String packType, String packFlavour,
                                          ServerListResultListener<PackHistoryObject> serverPackResult) {
        Timber.d("Fetching Pack history from Server");

        Class cls = GetPackHistory.class;
        String deviceId;

        try {
            deviceId = assertParam(cls, "Invalid Device ID",
                    DeviceIdManager.getDeviceId(activity));
        } catch (IllegalArgumentException e) {
            Timber.e(e);
            serverPackResult.error(
                    "Missing Authentication Parameters",
                    e,
                    202
            );
            return;
        }

        new WebRequest.Builder()
                .setUrl(GET_PACKS_URL)
                .setContext(activity)
                .setType(RequestType.PACKET)
                .setPacketClass(PackHistoryListPacket.class)
                // ===========================================================================
                .addParam("device_id", deviceId)
                .addParam("developer", String.valueOf(STApplication.DEBUG))
                .addParam("sc_version", scVersion)
                .addParam("pack_type", packType)
                .addParam("pack_flavour", packFlavour)
                // ===========================================================================
                .shouldClearCache(true)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        PackHistoryListPacket packsPacket = webResponse.getResult();

                        if (packsPacket.banned) {
                            serverPackResult.error(
                                    packsPacket.getBanReason(),
                                    null,
                                    packsPacket.getErrorCode()
                            );
                            return;
                        }

                        if (packsPacket.getPackHistories() == null || packsPacket.getPackHistories().isEmpty()) {
                            serverPackResult.success(Collections.emptyList());
                            return;
                        }

                        serverPackResult.success(packsPacket.getPackHistories());
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        if (webResponse.getException() != null)
                            Timber.e(webResponse.getException(), webResponse.getMessage());
                        else
                            Timber.w(webResponse.getMessage());

                        serverPackResult.error(
                                webResponse.getMessage(),
                                webResponse.getException(),
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();
    }
}
