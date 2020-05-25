package com.ljmu.andre.snaptools;

import android.app.Application;

import com.jaqxues.akrolyb.logger.FileLogger;
import com.ljmu.andre.snaptools.Networking.VolleyHandler;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PathProvider;
import com.ljmu.andre.snaptools.Utils.TimberUtils;

import timber.log.Timber;

import java.io.File;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class STApplication extends Application {
    public static final boolean DEBUG = true;
    public static final String MODULE_TAG = "SnapTools";
    public static String PACKAGE = STApplication.class.getPackage().getName();

    private static STApplication mInstance;

    @Override
    public void onCreate() {
        TimberUtils.plantAppropriateTree();

        Timber.d("Starting Application [BuildVariant: DEBUG]");
        ContextHelper.set(getApplicationContext());

        VolleyHandler.init(getApplicationContext());

        Timber.d("Initialising Activities");
        super.onCreate();
        mInstance = this;
    }

    public static synchronized STApplication getInstance() {
        Timber.d("Instance: " + mInstance);
        return mInstance;
    }
}
