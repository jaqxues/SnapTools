package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.bumptech.glide.request.target.ThumbnailImageViewTarget;
import com.google.common.io.Files;
import com.ljmu.andre.snaptools.STApplication;

import java.io.File;

import timber.log.Timber;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 31.10.2018 - Time 17:59.
 */

public class InstallUtils {
    public static void install(Activity activity, File file, boolean shouldTryRoot) {
        if (shouldTryRoot) {
            try {
                File target = new File(activity.getCodeCacheDir(), "tmp.apk");
                Files.copy(file, target);
                ShellUtils.sendCommandSync("pm install -t " + target);
                return;
            } catch (Throwable t) {
                Timber.e(t, "Could not install update with root");
            }
        }
        Intent intent;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Uri uri = ApkFileProvider.getUriForFile(
                    activity,
                    STApplication.PACKAGE + ".apk_provider",
                    file
            );

            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        } else {
            Uri uri = Uri.fromFile(file);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }

        if (activity != null && !activity.isFinishing()) {
            activity.startActivity(intent);
        }
    }

    public static void uninstallPackage(Activity activity, String packageName, boolean shouldTryRoot) {
        if (shouldTryRoot) {
            ShellUtils.sendCommandSync("pm uninstall " + packageName);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        if (activity != null && !activity.isFinishing())
            activity.startActivity(intent);
    }
}
