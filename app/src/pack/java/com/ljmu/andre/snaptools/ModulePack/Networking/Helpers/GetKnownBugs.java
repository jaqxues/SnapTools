package com.ljmu.andre.snaptools.ModulePack.Networking.Helpers;

import android.app.Activity;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Exceptions.KeyNotFoundException;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.KnownBugObject;
import com.ljmu.andre.snaptools.ModulePack.Networking.Packets.KnownBugsPacket;
import com.ljmu.andre.snaptools.Networking.NetworkUtils;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.jaqxues.akrolyb.prefs.PrefManagerKt.putPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.LAST_CHECK_KNOWN_BUGS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetKnownBugs {
    private static final long BUGS_CHECK_COOLDOWN = STApplication.DEBUG ? 0 : TimeUnit.HOURS.toMillis(12);
    private static final String KNOWN_BUGS_BASE_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Packs/JSON/KnownBugs/";
    private static final String FETCH_BUGS_TAG = "fetching_bugs";

    public static void getBugs(Activity activity, String scVersion, String packVersion,
                               ObjectResultListener<List<KnownBugObject>> resultListener) {
        if (shouldUseCache()) {
            Timber.d("Getting bugs from cache");
            getBugsFromCache(activity, scVersion, packVersion, resultListener);
        } else {
            Timber.d("Getting bugs from server");
            getBugsFromServer(activity, scVersion, packVersion, resultListener);
        }
    }

    public static boolean shouldUseCache() {
        return MiscUtils.calcTimeDiff(getPref(LAST_CHECK_KNOWN_BUGS)) < BUGS_CHECK_COOLDOWN;
    }

    public static void getBugsFromCache(Activity activity, String scVersion, String packVersion,
                                        ObjectResultListener<List<KnownBugObject>> resultListener) {
        Timber.d("Getting bugs from cache");
        Collection<KnownBugObject> knownBugObjects = CacheDatabase.getTable(KnownBugObject.class).getAll();

        Timber.d("Pulled %s bugs from cache", knownBugObjects.size());

        if (knownBugObjects.isEmpty()) {
            getBugsFromServer(activity, scVersion, packVersion, resultListener);
            return;
        }

        ArrayList<KnownBugObject> bugObjectList = new ArrayList<>(knownBugObjects);

        String expectedBugKey = KnownBugObject.createKey(scVersion, packVersion);
        String foundBugKey = bugObjectList.get(0).key;

        if (foundBugKey == null || !foundBugKey.equals(expectedBugKey)) {
            Timber.d("Bug key mismatch. Found %s, expected %s", foundBugKey, expectedBugKey);
            getBugsFromServer(activity, scVersion, packVersion, resultListener);
            return;
        }

        resultListener.success(null, new ArrayList<>(knownBugObjects));
    }

    public static void getBugsFromServer(Activity activity, String scVersion, String packVersion,
                                         ObjectResultListener<List<KnownBugObject>> resultListener) {
        Timber.d("Getting bugs from server (SCVersion: %s, PackVersion: %s", scVersion, packVersion);

        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                activity,
                "Fetching Known Bugs",
                "Fetching known bugs from the server... This can take up to 30 seconds to complete",
                FETCH_BUGS_TAG,
                true
        );

        progressDialog.setOnCancelListener(
                dialog -> resultListener.error("User cancelled Bug Fetching", null, -1)
        );

        progressDialog.show();
        new WebRequest.Builder()
                .setUrl(getKnownBugsUrl(scVersion))
                .setType(RequestType.STRING)
                .setMethod(Request.Method.GET)
                .setContext(activity)
                .setTag(FETCH_BUGS_TAG)
                .useDefaultRetryPolicy()
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) throws Throwable {
                        progressDialog.dismiss();

                        String json = webResponse.getResult();
                        KnownBugsPacket packet = null;
                        try {
                            packet = NetworkUtils.extractPacket(json, KnownBugsPacket.class, packVersion);
                        } catch (KeyNotFoundException e) {
                            resultListener.error("No KnownBugs found for this Pack Version", e, 202);
                        }

                        if (packet == null) {
                            packet = new Gson().fromJson(
                                    "{\"bugs\":[{\"category\": \"KnownBugs Fetching\",\"bugs\":[\"No KnownBugs Entry for this Version\",\"How ironic that this is the error\"]}]}",
                                    KnownBugsPacket.class);
                        }

                        if (packet.bugs == null) {
                            resultListener.error("Empty bugs list pulled from server", null, webResponse.getResponseCode());
                            return;
                        }

                        packet.assignChildrensKeys(scVersion, packVersion);

                        putPref(LAST_CHECK_KNOWN_BUGS, System.currentTimeMillis());
                        resultListener.success(null, packet.bugs);

                        CBITable<KnownBugObject> knownBugsTable = CacheDatabase.getTable(KnownBugObject.class);

                        if (knownBugsTable == null)
                            return;

                        knownBugsTable.insertAll(packet.bugs);
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        progressDialog.dismiss();

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
                }).performRequest();
    }

    public static String getKnownBugsUrl(String scVersion) {
        return KNOWN_BUGS_BASE_URL + "KnownBugs_SC_v" + scVersion + ".json";
    }
}
