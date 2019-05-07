package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import android.widget.Toast;

import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.SafeToast;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SafeToastAdapter {
    public static boolean showErrorToast(Activity snapActivity, String text) {
        if (Constants.getApkVersionCode() >= 57) {
            return SafeToast.show(
                    snapActivity,
                    text,
                    true
            );
        }

        return SafeToast.show(
                snapActivity,
                text,
                Toast.LENGTH_LONG
        );
    }

    public static boolean showDefaultToast(Activity snapActivity, String text) {
        return SafeToast.show(
                snapActivity,
                text,
                Toast.LENGTH_LONG
        );
    }
}
