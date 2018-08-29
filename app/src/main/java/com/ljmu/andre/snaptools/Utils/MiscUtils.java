package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import com.github.javiersantos.appupdater.objects.Version;
import com.ljmu.andre.snaptools.STApplication;
import timber.log.Timber;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MiscUtils {
    public static boolean isInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager == null)
            return false;

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (STApplication.PACKAGE.equalsIgnoreCase(appProcess.processName))
                    return true;
            }
        }

        return false;
    }

    public static void restartActivity(Activity activity) {
        activity.finish();
        Intent intent = new Intent(activity, activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static long calcTimeDiff(Long input) {
        return System.currentTimeMillis() - input;
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * ===========================================================================
     * Returns 0 if two are equal
     * Returns 1 if str1 > str2
     * returns -1 if str1 < str2
     * ===========================================================================
     *
     * @deprecated Use {@link Version#compareTo(Version)}
     */
    @Deprecated
    public static int versionCompare(String str1, String str2) {
        try {
            String[] vals1 = str1.split("\\.");
            String[] vals2 = str2.split("\\.");
            int i = 0;
            // set index to first non-equal ordinal or length of shortest version string
            while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
                i++;
            }

            // compare first non-equal ordinal number
            if (i < vals1.length && i < vals2.length) {
                int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
                return Integer.signum(diff);
            }
            // the strings are equal or one string is a substring of the other
            // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
            return Integer.signum(vals1.length - vals2.length);
        } catch (Throwable t) {
            Timber.e(t);
        }

        return 0;
    }

    public static String getInstalledSCVer(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo("com.snapchat.android", 0);

            return pinfo.versionName;
        } catch (NameNotFoundException e) {
            Timber.e(e, "Unable to locate Snapchat on this device");
        }

        return null;
    }
}
