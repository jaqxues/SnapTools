package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.ljmu.andre.snaptools.Exceptions.NullObjectException;
import com.ljmu.andre.snaptools.Framework.MetaData.FailedPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
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
    private static Map<String, LocalPackMetaData> metaDataCache;
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

    public static boolean isPackInstalled(String packName) {
        Map<String, LocalPackMetaData> installedDataMap = getInstalledMetaData();

        return !(installedDataMap == null || installedDataMap.isEmpty())
                && installedDataMap.get(packName) != null;

    }

    @Nullable
    public static Map<String, LocalPackMetaData> getInstalledMetaData() {
        try {
            File packDirectory = new File((String) getPref(MODULES_PATH));

            File[] jarFileList = packDirectory.listFiles((file, s) -> s.endsWith(".jar"));

            if (jarFileList == null || jarFileList.length <= 0)
                return null;

            if (Arrays.equals(jarFileCache, jarFileList)) {
                Timber.d("They are equal");
                return metaDataCache;
            }

            Timber.d("They are NOT equal");

            Map<String, LocalPackMetaData> packMetaDataMap = new HashMap<>();

            for (File file : jarFileList) {
                try {
                    LocalPackMetaData packMetaData = getPackMetaData(file);
                    packMetaDataMap.put(packMetaData.getName(), packMetaData);
                } catch (Throwable t) {
                    Timber.e(t);
                }
            }

            metaDataCache = packMetaDataMap;
            jarFileCache = jarFileList;

            return packMetaDataMap;
        } catch (Throwable t) {
            Timber.e(t);
        }

        return null;
    }

    public static LocalPackMetaData getPackMetaData(File file) throws NullObjectException {
        LocalPackMetaData packMetaData = new LocalPackMetaData();
        bindPackMetaData(packMetaData, file);
        return packMetaData;
    }

    private static void bindPackMetaData(PackMetaData metaData, File file) throws NullObjectException {
        if (!file.getName().endsWith(".jar"))
            throw new IllegalArgumentException("Supplied Non Jar File");

        Attributes attributes = JarUtils.getAttributesFromJar(file);

        if (attributes == null)
            throw new NullObjectException("Module Pack Built Incorrectly!", "Attributes");

        metaData.setName(file.getName().replace(".jar", ""))
                .setType(attributes.getValue("Type"))
                .setPackVersion(attributes.getValue("PackVersion"))
                .setScVersion(attributes.getValue("SCVersion"))
                .setDevelopment(Boolean.valueOf(attributes.getValue("Development")))
                .setFlavour(getFlavourFromAttributes(attributes, file))
                .completedBinding();
    }

    public static String getFlavourFromAttributes(Attributes attributes, File file) {
        String flavour = attributes.getValue("Flavour");

        if (flavour == null)
            flavour = file.getName().contains("Beta") ? "beta" : "prod";

        return flavour;
    }

    public static Observable<Map<String, LocalPackMetaData>> getAllMetaData() {
        Callable<Map<String, LocalPackMetaData>> callable = () -> {
            File packDirectory = new File((String) getPref(MODULES_PATH));

            File[] jarFileList = packDirectory.listFiles((file, s) -> s.endsWith(".jar"));

            if (jarFileList == null || jarFileList.length <= 0)
                return Collections.emptyMap();

            Map<String, LocalPackMetaData> packMetaDataMap = new HashMap<>();

            for (File file : jarFileList) {
                LocalPackMetaData metaData;
                try {
                    metaData = getPackMetaData(file);
                } catch (Throwable t) {
                    metaData = (LocalPackMetaData) new FailedPackMetaData()
                            .setReason(t.getMessage())
                            .setName(file.getName().replace(".jar", ""))
                            .completedBinding();
                }

                packMetaDataMap.put(metaData.getName(), metaData);
            }

            Set<String> selectedPacks = getPref(SELECTED_PACKS);

            for (String packName : selectedPacks) {
                if (!packMetaDataMap.containsKey(packName)) {
                    FailedPackMetaData metaData = (FailedPackMetaData) new FailedPackMetaData()
                            .setReason("Pack is enabled but cannot be found in the installed list")
                            .setName(packName)
                            .completedBinding();

                    packMetaDataMap.put(metaData.getName(), metaData);
                }
            }

            return packMetaDataMap;
        };

        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
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
