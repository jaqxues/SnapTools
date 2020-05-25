package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;

import com.android.volley.Request.Method;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Databases.Tables.ServerPackObject;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;
import com.ljmu.andre.snaptools.Networking.Packets.ServerPacksPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ServerListResultListener;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import com.ljmu.andre.snaptools.Utils.PackUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.putPref;
import static com.ljmu.andre.snaptools.Utils.Constants.PACK_CHECK_COOLDOWN;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_PACKS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetServerPacks {
    private static final String GET_PACKS_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Packs/JSON/ServerPacks.json";

    public static void getServerPacks(Activity activity, ServerListResultListener<ServerPackMetaData> resultListener) {
        getServerPacks(activity, false, resultListener);
    }

    public static void getServerPacks(Activity activity, boolean invalidateCache, ServerListResultListener<ServerPackMetaData> resultListener) {
        if (!invalidateCache && shouldUseCache())
            getPacksFromCache(activity, resultListener);
        else
            getPacksFromServer(activity, resultListener);
    }

    public static boolean shouldUseCache() {
        return PackUtils.timeSinceLastPackCheck() < PACK_CHECK_COOLDOWN;
    }

    private static void getPacksFromCache(Activity activity, ServerListResultListener<ServerPackMetaData> resultListener) {
        Observable.fromCallable((Callable<List<ServerPackMetaData>>) () -> {
            Timber.d("Getting server packs from cache");

            Collection<ServerPackObject> serverPackObjects = CacheDatabase.getTable(ServerPackObject.class).getAll(
                    serverPackObject -> {
                        if (serverPackObject.flavour == null)
                            serverPackObject.flavour = serverPackObject.isBeta ? "beta" : "prod";
                    }
            );

            Timber.d("Pulled %s server packs from cache", serverPackObjects.size());

            if (serverPackObjects.isEmpty()) {
                getPacksFromServer(activity, resultListener);
                return Collections.emptyList();
            }
            Map<String, LocalPackMetaData> installedMetaData = PackUtils.getInstalledMetaData();

            return processPacks(serverPackObjects, installedMetaData);
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<ServerPackMetaData>>("Couldn't retrieve server packs from cache") {
                    @Override
                    public void onNext(List<ServerPackMetaData> serverPackMetaData) {
                        if (serverPackMetaData.isEmpty()) {
                            getPacksFromServer(activity, resultListener);
                            return;
                        }

                        resultListener.success(serverPackMetaData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getPacksFromServer(activity, resultListener);
                    }
                });
    }

    public static void getPacksFromServer(Activity activity, ServerListResultListener<ServerPackMetaData> serverPackResult) {
        new WebRequest.Builder()
                .setUrl(GET_PACKS_URL)
                .setContext(activity)
                .setMethod(Method.GET)
                .setType(RequestType.PACKET)
                .setPacketClass(ServerPacksPacket.class)
                // ===========================================================================
                .shouldClearCache(true)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        ServerPacksPacket packsPacket = webResponse.getResult();

                        if (packsPacket.banned) {
                            serverPackResult.error(packsPacket.getBanReason(), null, packsPacket.getErrorCode());
                            return;
                        }

                        if (packsPacket.getPacks() == null) {
                            serverPackResult.success(Collections.emptyList());
                            return;
                        }

                        List<ServerPackObject> cacheTableList = new ArrayList<>();

                        Map<String, LocalPackMetaData> installedMetaData = PackUtils.getInstalledMetaData();

                        for (ServerPackMetaData metaData : packsPacket.getPacks()) {
                            String name = PackMetaData.getFileNameFromTemplate(
                                    metaData.getType(),
                                    metaData.getScVersion(),
                                    metaData.getFlavour(),
                                    metaData.getPackVersion()
                            );

                            metaData.setName(name);
                            Timber.d("Name: " + name);

                            if (installedMetaData != null) {
                                LocalPackMetaData localMetaData = installedMetaData.get(metaData.getName());

                                if (localMetaData != null) {
                                    metaData.setInstalled(true);
                                    metaData.setHasUpdate(MiscUtils.versionCompare(
                                            metaData.getPackVersion(), localMetaData.getPackVersion()) > 0);
                                }
                            }

                            metaData.completedBinding();
                            cacheTableList.add(ServerPackObject.fromPackMetaData(metaData));
                        }

                        Timber.d("Fetched %s packs", cacheTableList.size());

                        if (cacheTableList.isEmpty()) {
                            serverPackResult.error(
                                    "Server replied with empty ModulePacks list",
                                    null,
                                    -1
                            );

                            return;
                        }

                        CacheDatabase.getTable(ServerPackObject.class).deleteAll();
                        CacheDatabase.getTable(ServerPackObject.class).insertAll(cacheTableList);
                        putPref(LAST_CHECK_PACKS, System.currentTimeMillis());
                        serverPackResult.success(packsPacket.getPacks());
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

    public static Observable<List<ServerPackMetaData>> getCachedMetaDataObservable() {
        return Observable.fromCallable(() -> {
            Collection<ServerPackObject> packObjects = CacheDatabase.getTable(ServerPackObject.class).getAll();

            if (packObjects.isEmpty())
                return Collections.emptyList();

            Map<String, LocalPackMetaData> installedMetaData = PackUtils.getInstalledMetaData();
            return processPacks(packObjects, installedMetaData);
        });
    }

    private static List<ServerPackMetaData> processPacks(Collection<ServerPackObject> packObjects, Map<String, LocalPackMetaData> installedMetaData) {
        List<ServerPackMetaData> serverMetaDataList = new ArrayList<>();
        for (ServerPackObject packObject : packObjects) {
            if (packObject.development != STApplication.DEBUG)
                continue;

            ServerPackMetaData serverMetaData = new ServerPackMetaData(PackEventRequest.EventHandler.ignoreEvents);
            packObject.bindMetaData(serverMetaData);

            if (installedMetaData != null) {
                LocalPackMetaData localMetaData = installedMetaData.get(serverMetaData.getName());

                if (localMetaData != null) {
                    serverMetaData.setInstalled(true);
                    serverMetaData.setHasUpdate(MiscUtils.versionCompare(
                            serverMetaData.getPackVersion(), localMetaData.getPackVersion()) > 0
                    );
                }
            }

            serverMetaData.completedBinding();
            serverMetaDataList.add(serverMetaData);
        }
        return serverMetaDataList;
    }

    public interface ServerPackResultListener {
        void success(List<ServerPackMetaData> metaDataList);

        void error(String message, Throwable t);
    }
}
