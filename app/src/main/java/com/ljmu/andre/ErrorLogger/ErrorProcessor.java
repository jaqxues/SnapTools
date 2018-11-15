package com.ljmu.andre.ErrorLogger;

import android.os.Build.VERSION;
import android.util.Log;

import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.STApplication;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class ErrorProcessor implements Runnable {
    private static final long WRITE_DELAY = TimeUnit.SECONDS.toMillis(30);
    private final Object LOCK = new Object();
    private boolean isAlive;
    private boolean isInitialised;
    private List<ErrorHolder> errorHolders = new ArrayList<>();
    private File outputFile;
    private String directory;
    private String fileName;
    private long lastWriteTime;

    public ErrorProcessor() {
    }

    void init(String directory, String fileName) {
        this.directory = directory;
        this.fileName = fileName;

        isAlive = true;
        isInitialised = true;
    }

    void addError(Thread thread, int logLevel, Throwable error, String errorMessage) {
        Timber.d("Adding error: " + error.getMessage());

        checkInit();

        synchronized (LOCK) {
            errorHolders.add(new ErrorHolder(logLevel, error, errorMessage));
            LOCK.notifyAll();
        }
    }

    private void checkInit() {
        if (!isInitialised)
            throw new IllegalStateException("ErrorProcessor not Initialised");
    }

    void addError(int logLevel, String error) {
        checkInit();

        synchronized (LOCK) {
            errorHolders.add(new ErrorHolder(logLevel, error));
            LOCK.notifyAll();
        }
    }

    @Override
    public void run() {
        checkInit();

        while (isAlive) {
            try {
                synchronized (LOCK) {
                    while (errorHolders.isEmpty()) {
                        Timber.d("Waiting for entries");
                        LOCK.wait();
                        Timber.d("Resuming after wait");
                    }
                }

                long writeTimeDifference = System.currentTimeMillis() - lastWriteTime;

                if (writeTimeDifference < WRITE_DELAY) {
                    long adjustedWriteDelay = WRITE_DELAY - writeTimeDifference;
                    Timber.d("Sleeping for %sms", adjustedWriteDelay);
                    Thread.sleep(adjustedWriteDelay);
                    Timber.d("Awoken from sleep");
                }

                if (outputFile == null) {
                    outputFile = FileUtils.createFile(directory, fileName);

                    if (outputFile == null)
                        throw new IllegalStateException("Couldn't create output file: " + fileName);

                    sortAndThrowLogs();
                }

                writeCollectedErrors();
            } catch (InterruptedException e) {
                Timber.e(e, "Thread Interrupted");
            }
        }
    }

    private void sortAndThrowLogs() {
        File packFolder = new File(directory);

        if (!packFolder.exists())
            return;

        File[] files = packFolder.listFiles();

        if (files.length > 30) {
            Timber.d("Clearing [%s] logs", files.length - 30);

            Arrays.sort(
                    files,
                    (f1, f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified())
            );

            for (int i = 0; i < 30; i++) {
                try {
                    File file = files[i];
                    file.delete();
                } catch (Throwable ignore) {
                }
            }
        }
    }

    private void writeCollectedErrors() {
        CharSink charSink = Files.asCharSink(outputFile, Charset.defaultCharset(), FileWriteMode.APPEND);

        synchronized (LOCK) {
            try {
                for (ErrorHolder errorHolder : errorHolders) {
                    try {
                        String error = errorHolder.convertToOutput();

                        //SlackUtils.uploadToSlack(botName, System.currentTimeMillis() + "\n" + errorHolder.convertToOutput());

                        error = generateDebugData() + error;
                        charSink.write(error);
                    } catch (IOException e) {
                        Log.e(STApplication.MODULE_TAG, "Error writing error log", e);
                    }
                }

                errorHolders.clear();
            } catch (ConcurrentModificationException e) {
                Log.e(STApplication.MODULE_TAG, "Concurrent modification with ErrorLogger", e);
            }
        }

        lastWriteTime = System.currentTimeMillis();
    }

    private String generateDebugData() {
        try {
            StringBuilder output = new StringBuilder();

            if (System.currentTimeMillis() - lastWriteTime >= TimeUnit.MINUTES.toMillis(10)) {
                output.append("Framework Version: ").append(Constants.getApkVersionName()).append("\n");
                output.append("OS Version: ").append(VERSION.SDK_INT).append("\n");
            }

            output.append("[")
                    .append(StringUtils.HHmmssSSS.format(new Date(System.currentTimeMillis())))
                    .append("]");

            return output.toString();
        } catch (Throwable t) {
            Timber.d(t, "Error");
        }

        return "";
    }

    private void stop() {
        isAlive = false;
        LOCK.notify();
    }

    void addError(int logLevel, Throwable error) {
        Timber.d("Error added");

        checkInit();

        synchronized (LOCK) {
            errorHolders.add(new ErrorHolder(logLevel, error));
            LOCK.notifyAll();
        }
    }

    private static class ErrorHolder {
        private final int logLevel;
        private final Throwable throwable;
        private final String errorMessage;

        ErrorHolder(int logLevel, Throwable throwable) {
            this(logLevel, throwable, null);
        }

        ErrorHolder(int logLevel, Throwable throwable, String errorMessage) {
            this.logLevel = logLevel;
            this.throwable = throwable;
            this.errorMessage = errorMessage;
        }

        ErrorHolder(int logLevel, String errorMessage) {
            this(logLevel, null, errorMessage);
        }

        public int getLogLevel() {
            return logLevel;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        String convertToOutput() {
            if (errorMessage == null && throwable == null)
                return null;

            StringBuilder builder = new StringBuilder();

            switch (logLevel) {
                case Log.ASSERT:
                    builder.append(" [ASSERT] ");
                    break;
                case Log.ERROR:
                    builder.append(" [ERROR] ");
                    break;
                case Log.WARN:
                    builder.append(" [WARN] ");
                    break;
                default:
                    break;
            }

            if (errorMessage != null)
                builder.append(errorMessage);

            if (throwable != null) {
                if (errorMessage != null)
                    builder.append("\n");

                builder.append(Log.getStackTraceString(throwable));
            }

            builder.append("\n\n");

            return builder.toString();
        }
    }
}
