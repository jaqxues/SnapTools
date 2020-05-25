package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;

import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Databases.Tables.PurchaseTable;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Networking.Helpers.CheckTrialMode;
import com.ljmu.andre.snaptools.Networking.Packets.TrialPacket;
import com.ljmu.andre.snaptools.Networking.WebResponse.PacketResultListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.jaqxues.akrolyb.prefs.PrefManagerKt.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TRIAL_ACTIVE_TIME;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TRIAL_MODE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TrialUtils {
    public static final int TRIAL_UNKNOWN = -1;
    public static final int TRIAL_AVAILABLE = 0;
    public static final int TRIAL_ACTIVE = 1;
    public static final int TRIAL_EXPIRED = 2;

    public static void confirmTrialMode(Activity activity, Callable<Integer> callable) {
        int trialMode = getPref(TRIAL_MODE);
        Long trialActiveTime = getPref(TRIAL_ACTIVE_TIME);

        if (trialMode == TRIAL_UNKNOWN || trialActiveTime == null) {
            CheckTrialMode.checkTrialMode(
                    activity,
                    new PacketResultListener<TrialPacket>() {
                        @Override
                        public void success(String message, TrialPacket packet) {
                            putPref(TRIAL_MODE, packet.trialMode);
                            putPref(TRIAL_ACTIVE_TIME, packet.getActiveTimestamp());

                            callable.call(packet.trialMode);
                        }

                        @Override
                        public void error(String message, Throwable t, int errorCode) {
                            callable.call(
                                    trialMode
                            );
                            Timber.w("Error fetching trial mode: " + errorCode);
                        }
                    }
            );
        } else {
            callable.call(
                    trialMode
            );
        }
    }

    public static void endTrialIfExpired(Activity activity) {
        int trialMode = getPref(TRIAL_MODE);
        Long trialActiveTime = getPref(TRIAL_ACTIVE_TIME);

        if (trialMode == TRIAL_ACTIVE && trialActiveTime != null) {
            if (trialActiveTime + TimeUnit.HOURS.toMillis(1) < System.currentTimeMillis()) {
                putPref(TRIAL_MODE, TRIAL_EXPIRED);

                DialogFactory.createBasicMessage(
                        activity,
                        "Trial Expired",
                        "Your 1 hour trial has expired, premium packs will no longer be able to be used."
                ).show();

                CBITable<PurchaseTable> purchaseTable = CacheDatabase.getTable(PurchaseTable.class);

                Map<String, LocalPackMetaData> installedPacks = PackUtils.getInstalledMetaData();

                if (installedPacks != null) {
                    String basicText = "Basic";
                    String trialText = "TRIAL";

                    for (LocalPackMetaData packMetaData : installedPacks.values()) {
                        if (!packMetaData.getType().equals(basicText)) {
                            PurchaseTable purchaseData = purchaseTable.getFirst(packMetaData.getScVersion());

                            if (purchaseData != null && purchaseData.purchaseToken != null &&
                                    !purchaseData.purchaseToken.contains(trialText))
                                continue;

                            FrameworkManager.deleteModPack(packMetaData.getName(), null);
                        }
                    }
                }
            }
        }
    }
}
