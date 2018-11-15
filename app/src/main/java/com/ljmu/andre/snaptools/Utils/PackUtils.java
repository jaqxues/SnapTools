package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_PACKS;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.MODULES_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;
import static com.ljmu.andre.snaptools.Utils.MiscUtils.calcTimeDiff;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackUtils {
    private static final Object PACK_CHECKSUM_LOCK = new Object();
    private static File[] jarFileCache;
    private static long lastKilledSC;

    public static void killSCService(Activity activity) {
        Timber.i("Killing SC");
        if (calcTimeDiff(lastKilledSC) < TimeUnit.SECONDS.toMillis(5))
            return;

        lastKilledSC = System.currentTimeMillis();

        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

        if (VERSION.SDK_INT > VERSION_CODES.N_MR1) {
            activityManager.killBackgroundProcesses("com.snapchat.android");
            return;
        }

        Timber.i("ActivityManager: " + activityManager);

        for (RunningServiceInfo appProcessInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            String packageName = appProcessInfo.service.getPackageName();
            Timber.i("Process Name: " + packageName);

            if (packageName.equals("com.snapchat.android")) {

                Observable<Boolean> commandObservable = ShellUtils.sendCommand("am force-stop com.snapchat.android\n");

                commandObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Boolean>() {
                            @Override
                            public void onNext(@NonNull Boolean aBoolean) {
                                Timber.d("State: " + aBoolean);

                                if (aBoolean)
                                    SafeToast.show(activity, "Killed Snapchat in the background", Toast.LENGTH_SHORT);
                                else
                                    SafeToast.show(activity, "Failed to kill Snapchat in the background", Toast.LENGTH_LONG, true);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Timber.e(e);
                                SafeToast.show(activity, "Failed to kill Snapchat in the background", Toast.LENGTH_LONG, true);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                break;
            }
        }
    }

    public static String getFlavourFromAttributes(Attributes attributes, File file) {
        String flavour = attributes.getValue("Flavour");

        if (flavour == null)
            flavour = file.getName().contains("Beta") ? "beta" : "prod";

        return flavour;
    }

    public static Long timeSinceLastPackCheck() {
        return System.currentTimeMillis() - (Long) getPref(LAST_CHECK_PACKS);
    }

    @Nullable
    public static String generatePacksChecksum() {
        Set<String> selectPackSet = getPref(SELECTED_PACKS);
        if (selectPackSet == null || selectPackSet.isEmpty())
            return null;

        List<HashCode> packHashCodeList = new ArrayList<>(selectPackSet.size());
        ExecutorService executor = Executors.newCachedThreadPool();

        for (String selectedPack : selectPackSet) {
            Timber.d("Hashing Pack: " + selectedPack);
            File packDir = getCreateDir(MODULES_PATH);
            File modulePackFile = new File(packDir, selectedPack + ".jar");

            if (!modulePackFile.exists()) {
                Timber.d("Pack file doesn't exist: " + modulePackFile);
                continue;
            }

            executor.execute(() -> {
                try {
                    HashCode packHash = Files.hash(modulePackFile, Hashing.murmur3_128(6782590));
                    Timber.d("Pack Hash: " + packHash);

                    synchronized (PACK_CHECKSUM_LOCK) {
                        packHashCodeList.add(
                                packHash
                        );
                    }

                } catch (IOException e) {
                    Timber.e(e);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            Timber.d("Finished");
        } catch (InterruptedException e) {
            Timber.e(e);
            return null;
        }

        if (packHashCodeList.size() <= 0)
            return null;

        return Hashing.combineUnordered(packHashCodeList).toString();
    }
}
