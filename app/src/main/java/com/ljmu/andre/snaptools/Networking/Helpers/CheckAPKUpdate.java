package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;

import com.android.volley.Request.Method;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.Dialogs.Content.ApkUpdate;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.Utils.InstallUtils;

import java.util.Objects;

import io.reactivex.annotations.Nullable;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.IGNORED_UPDATE_VERSION_CODE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_APK_UPDATE_CHECK;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LATEST_APK_VERSION_CODE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CheckAPKUpdate {
    private static final String APK_RELEASE_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Apks/JSON/APKUpdates/LatestApkVersion.json";
    private static final String APK_BETA_URL = "https://raw.githubusercontent.com/jaqxues/SnapTools_DataProvider/master/Apks/JSON/APKUpdates/LatestBetaApkVersion.json";

    public static void checkApkUpdate(Activity activity, boolean reportNoUpdates) {
        checkApkUpdate(activity, reportNoUpdates, false);
    }

    public static void checkApkUpdate(Activity activity, boolean reportNoUpdates, boolean bypassIgnoredVersion) {
        //noinspection ConstantConditions
        String url = BuildConfig.FLAVOR.equals("beta")
                ? APK_BETA_URL : APK_RELEASE_URL;

        new AppUpdaterUtils(activity)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(url)
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        putPref(LAST_APK_UPDATE_CHECK, System.currentTimeMillis());

                        if (update != null)
                            putPref(LATEST_APK_VERSION_CODE, update.getLatestVersionCode());

                        if (isUpdateAvailable && update != null) {
                            Integer latestVerCode = update.getLatestVersionCode();

                            if (latestVerCode == null) {
                                Timber.e("NULL UPDATE VERSION CODE");
                                return;
                            }

                            Integer lastIgnoredVer = getPref(IGNORED_UPDATE_VERSION_CODE);

                            if (!bypassIgnoredVersion && lastIgnoredVer != null && Objects.equals(lastIgnoredVer, latestVerCode))
                                return;

                            if (latestVerCode > BuildConfig.VERSION_CODE) {
                                new ThemedDialog(activity)
                                        .setTitle("New APK update available!")
                                        .setExtension(
                                                new ApkUpdate()
                                                        .setActivity(activity)
                                                        .setUpdate(update)
                                        )
                                        .show();
                            }
                        } else {
                            if (!reportNoUpdates)
                                return;

                            DialogFactory.createBasicMessage(
                                    activity,
                                    "No Update Found",
                                    "You're on the latest APK within your update channel."
                            ).show();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Timber.e("Something went wrong %s", error.name());
                    }
                })
                .start();
    }

    public static void updateApk(Activity activity, String url, String directory, String filename) {
        updateApk(activity, url, directory, filename, null);
    }

    public static void updateApk(Activity activity, String url, String directory, String filename,
                                 @Nullable ObjectResultListener<Boolean> resultListener) {
        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                activity,
                "Downloading File",
                "Downloading latest APK",
                "apk_download", true
        );

        boolean[] hasCancelled = new boolean[1];

        progressDialog.setOnCancelListener(dialog -> {
            hasCancelled[0] = true;

            if (resultListener != null)
                resultListener.success(null, false);
        });

        progressDialog.show();

        new DownloadFile()
                .setUrl(url)
                .setDirectory(directory)
                .setFilename(filename)
                .setContext(activity)
                .setMethod(Method.GET)
                .addDownloadListener(
                        (state, message, outputFile, responseCode) -> {
                            progressDialog.dismiss();

                            if (hasCancelled[0]) {
                                CheckAPKUpdate.failedDownload(activity, "User Cancelled Download", responseCode);

                                if (resultListener != null)
                                    resultListener.success(null, false);
                                return;
                            }

                            if (!state || outputFile == null) {
                                CheckAPKUpdate.failedDownload(activity, message, responseCode);

                                if (resultListener != null)
                                    resultListener.success(null, false);
                                return;
                            }

                            InstallUtils.install(activity, outputFile, false);
                            activity.finish();

                            if (resultListener != null)
                                resultListener.success(null, true);
                        }
                )
                .download();
    }

    private static void failedDownload(Activity activity, String reason, int responseCode) {
        if (activity != null && !activity.isFinishing()) {
            DialogFactory.createErrorDialog(
                    activity,
                    "Download Failed",
                    "Downloading SnapTools APK failed:"
                            + "\n" + reason,
                    responseCode
            ).show();
        }
    }
}
