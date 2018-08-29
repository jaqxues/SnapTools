package com.ljmu.andre.snaptools.ModulePack.Networking.Helpers;

import android.app.Activity;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.KnownBugObject;
import com.ljmu.andre.snaptools.ModulePack.Networking.Packets.KnownBugsPacket;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.MiscUtils;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.LAST_CHECK_KNOWN_BUGS;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GetKnownBugs {
    private static final long BUGS_CHECK_COOLDOWN = STApplication.DEBUG ? 0 : TimeUnit.HOURS.toMillis(12);
    private static final String KNOWN_BUGS_URL = /*https://snaptools.org/SnapTools/Scripts/get_known_bugs.php*/ decryptMsg(new byte[]{-4, -118, -56, -116, -67, 26, -1, -39, 30, 104, 108, 5, -64, 78, 25, -2, -100, -63, 90, -124, 11, -84, 111, -37, 59, 25, -36, 92, -106, 78, -120, 10, 102, 8, -126, -10, 21, 70, -102, 61, -62, 102, -69, 4, 115, -86, -36, 74, -9, 19, 44, -6, -128, -117, -114, 34, -121, -49, -11, -16, -97, 39, -98, 7});
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
        Timber.d("Getting bugs from server");

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
                .setUrl(KNOWN_BUGS_URL)
                .setType(RequestType.PACKET)
                .setPacketClass(KnownBugsPacket.class)
                .setContext(activity)
                .setTag(FETCH_BUGS_TAG)
                .useDefaultRetryPolicy()
                .addParam("sc_version", scVersion)
                .addParam("pack_version", packVersion)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) throws Throwable {
                        progressDialog.dismiss();

                        KnownBugsPacket bugsPacket = webResponse.getResult();

                        if (bugsPacket.bugs == null) {
                            resultListener.error("Empty bugs list pulled from server", null, webResponse.getResponseCode());
                            return;
                        }

                        bugsPacket.assignChildrensKeys(scVersion, packVersion);

                        putPref(LAST_CHECK_KNOWN_BUGS, System.currentTimeMillis());
                        resultListener.success(null, bugsPacket.bugs);

                        CBITable<KnownBugObject> knownBugsTable = CacheDatabase.getTable(KnownBugObject.class);

                        if (knownBugsTable == null)
                            return;

                        knownBugsTable.insertAll(bugsPacket.bugs);
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
                })
                .performRequest();
    }
}
