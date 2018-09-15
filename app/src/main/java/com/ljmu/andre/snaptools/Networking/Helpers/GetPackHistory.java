package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;

import com.android.volley.Request;
import com.ljmu.andre.snaptools.Exceptions.KeyNotFoundException;
import com.ljmu.andre.snaptools.Networking.NetworkUtils;
import com.ljmu.andre.snaptools.Networking.Packets.PackHistoryListPacket;
import com.ljmu.andre.snaptools.Networking.Packets.PackHistoryObject;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ServerListResultListener;

import java.util.Collections;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetPackHistory {
    private static final String GET_PACK_HISTORY_BASE_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Packs/JSON/";

    public static void getPacksFromServer(Activity activity, String scVersion, String packType, String packFlavour,
                                          ServerListResultListener<PackHistoryObject> serverPackResult) {
        Timber.d("Fetching Pack history from Server");

        new WebRequest.Builder()
                .setUrl(getPackHistoryUrl(scVersion))
                .setContext(activity)
                .setMethod(Request.Method.GET)
                .setType(RequestType.STRING)
                // ===========================================================================
                .shouldClearCache(true)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        PackHistoryListPacket packsPacket;
                        try {
                            packsPacket = NetworkUtils.extractPack(webResponse.getResult(), PackHistoryListPacket.class, packFlavour);
                        } catch (KeyNotFoundException e) {
                            Timber.e(e);
                            serverPackResult.success(Collections.emptyList());
                            return;
                        }
                        if (packsPacket == null) {
                            Timber.e("Received Empty Pack. Source JSON: \"%s\"", webResponse.getResult());
                            serverPackResult.success(Collections.emptyList());
                            return;
                        }
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

    private static String getPackHistoryUrl(String scVersion) {
        return GET_PACK_HISTORY_BASE_URL + "PackHistory_SC_v" + scVersion + ".json";
    }
}
