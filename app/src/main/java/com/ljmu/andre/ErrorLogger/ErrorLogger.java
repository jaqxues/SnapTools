package com.ljmu.andre.ErrorLogger;

import android.util.Log;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.StringUtils;
import timber.log.Timber;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ErrorLogger implements Thread.UncaughtExceptionHandler {
    private static ErrorLogger instance;
    private UncaughtExceptionHandler defaultHandler;
    private Thread thread;
    private ErrorProcessor processor;

    private ErrorLogger() {
    }

    public void addError(Thread t, int logLevel, Throwable error, String errorMessage) {
        checkAndCreateProcessor();
        processor.addError(t, logLevel, error, errorMessage);
    }

    private void checkAndCreateProcessor() {
        if (thread == null || !thread.isAlive()) {
            Timber.d("Creating and starting new Thread");
            thread = new Thread(processor);
            thread.start();
        }
    }

    public void addError(int logLevel, String error) {
        checkAndCreateProcessor();
        processor.addError(logLevel, error);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        addError(t, Log.ASSERT, e);
        defaultHandler.uncaughtException(t, e);
    }

    public void addError(Thread t, int logLevel, Throwable error) {
        checkAndCreateProcessor();
        processor.addError(t, logLevel, error, null);
    }

    public static ErrorLogger getInstance() {
        return instance;
    }

    public static ErrorLogger init() {
        instance = new ErrorLogger();

        String currentDate = StringUtils.ddMyyyy.format(
                new Date(System.currentTimeMillis()));

        instance.processor = new ErrorProcessor();
        instance.processor.init(
                Preferences.getExternalPath() + "/" + STApplication.MODULE_TAG + "/ErrorLogs",
                String.format("log_%s.log", currentDate)
        );

        instance.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(instance);

        return instance;
    }
}
