package com.ljmu.andre.snaptools;

import android.app.Application;
import com.github.anrwatchdog.ANRWatchDog;
import com.ljmu.andre.ErrorLogger.ErrorLogger;
import com.ljmu.andre.snaptools.Networking.VolleyHandler;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.TimberUtils;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class STApplication extends Application {
    public static final boolean DEBUG = false;
    public static final String MODULE_TAG = "SnapTools";
    public static final String PACKAGE = STApplication.class.getPackage().getName();

    private static STApplication mInstance;

    @Override
    public void onCreate() {
        TimberUtils.plantAppropriateTree();

        Timber.d("Starting Application [BuildVariant: RELEASE]");
        ContextHelper.set(getApplicationContext());

        VolleyHandler.init(getApplicationContext());
        ErrorLogger.init();

        Timber.d("Initialising Activities");
        super.onCreate();
        mInstance = this;

        try {
            ANRWatchDog.setDefaultUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
            new ANRWatchDog(10000)
                    .setANRListener(
                            error -> Timber.e(error, /*Application Not Responding*/ decryptMsg(new byte[]{34, 88, -109, -83, -45, -77, 82, -43, -96, -105, 55, 55, 56, -32, -107, 8, 91, -118, -25, -73, -5, -99, -83, 126, 116, -9, -125, 104, 103, 81, -29, -17}))
                    )
                    .start();
        } catch (Throwable t) {
            Timber.e(t, /*Error initialising ANRWatchdog*/ decryptMsg(new byte[]{-115, -14, 106, -44, -4, -6, -31, -74, 127, 14, -25, -105, -44, -95, -1, -31, 27, 119, 2, 43, 31, 65, -26, -114, -109, -16, 30, 44, -124, -90, 27, -75}));
        }
    }

    public static synchronized STApplication getInstance() {
        Timber.d("Instance: " + mInstance);
        return mInstance;
    }
}
