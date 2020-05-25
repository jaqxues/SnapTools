package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Databases.Tables.ShopItem;
import com.ljmu.andre.snaptools.Networking.Packets.ShopItemsPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ServerListResultListener;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.ljmu.andre.snaptools.Utils.Constants.SHOP_CHECK_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_SHOP;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetShopItems {
    private static final String SHOP_ITEMS_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/General/ShopItems.json";

    public static boolean shouldUseCache() {
        return MiscUtils.calcTimeDiff(getPref(LAST_CHECK_SHOP)) < SHOP_CHECK_COOLDOWN;
    }

    public static Observable<List<ShopItem>> getCacheObservable() {
        return Observable.fromCallable(GetShopItems::getCache);
    }

    @NonNull
    public static List<ShopItem> getCache() {
        Collection<ShopItem> shopItems = CacheDatabase.getTable(ShopItem.class).getAll();

        if (shopItems.isEmpty())
            return Collections.emptyList();

        return new ArrayList<>(shopItems);
    }

    public static void getFromServer(Activity activity, ServerListResultListener<ShopItem> resultListener) {
//        Class cls = GetShopItems.class;
//        String token;
//        String email;
//        String deviceId;

//        try {
//            deviceId = assertParam(cls, "Invalid Device ID", DeviceIdManager.getDeviceId(activity));
//        } catch (IllegalArgumentException e) {
//            Timber.e(e);
//            resultListener.error(
//                    "Missing Authentication Parameters",
//                    e,
//                    202
//            );
//            return;
//        }

        new WebRequest.Builder()
                .setUrl(SHOP_ITEMS_URL)
                .setMethod(Request.Method.GET)
                .setType(RequestType.PACKET)
                .setPacketClass(ShopItemsPacket.class)
                .setContext(activity)
                .useDefaultRetryPolicy()
                // ===========================================================================
//                .addParam("device_id", deviceId)
                // ===========================================================================
                .setCallback(
                        new WebResponseListener() {
                            @Override
                            public void success(WebResponse webResponse) {
                                ShopItemsPacket itemsPacket = webResponse.getResult();

                                if (itemsPacket.banned) {
                                    resultListener.error(
                                            itemsPacket.getBanReason(),
                                            null,
                                            itemsPacket.getErrorCode()
                                    );
                                    return;
                                }

                                if (itemsPacket.shopItems == null || itemsPacket.shopItems.length <= 0) {
                                    resultListener.success(Collections.emptyList());
                                    return;
                                }

                                List<ShopItem> shopItems = Arrays.asList(itemsPacket.shopItems);
                                resultListener.success(shopItems);

                                CBITable<ShopItem> itemTable = CacheDatabase.getTable(ShopItem.class);
                                itemTable.deleteAll();
                                itemTable.insertAll(shopItems);
                            }

                            @Override
                            public void error(WebResponse webResponse) {
                                if (webResponse.getException() != null)
                                    Timber.e(webResponse.getException(), webResponse.getMessage());
                                else
                                    Timber.w(webResponse.getMessage());

                                resultListener.error(
                                        webResponse.getMessage(),
                                        webResponse.getException(),
                                        webResponse.getResponseCode()
                                );
                            }
                        }
                )
                .performRequest();
    }
}
