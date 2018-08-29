package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import com.ljmu.andre.snaptools.STApplication;
import timber.log.Timber;

import java.lang.ref.WeakReference;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ContextHelper {
    private static WeakReference<Context> moduleContextRef;
    private static WeakReference<Activity> activityRef;

    public static void set(Context moduleContext) {
        ContextHelper.moduleContextRef = new WeakReference<>(moduleContext);
    }

    public static Resources getModuleResources(Context context) {
        return getModuleContext(context).getResources();
    }

    public static Context getModuleContext(Context context) {
        return getModuleContext(context, false);
    }

    @RequiresFramework(73)
    public static Context getModuleContext(Context context, boolean nullable) {
        if (moduleContextRef == null || moduleContextRef.get() == null) {
            if (context == null) {
                if (nullable)
                    return null;

                throw new IllegalStateException("Module context couldn't be created");
            }

            moduleContextRef = new WeakReference<>(createModuleContext(context));
        }

        return moduleContextRef.get();
    }

    private static Context createModuleContext(Context context) {
        try {
            return context.createPackageContext(
                    STApplication.PACKAGE, Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            Timber.e(e);
            throw new IllegalStateException(e);
        }
    }

    public static Activity getActivity() {
        return activityRef.get();
    }

    public static void setActivity(Activity activity) {
        ContextHelper.activityRef = new WeakReference<>(activity);
    }
}
