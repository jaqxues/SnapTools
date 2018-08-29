package com.ljmu.andre.modulepackloader.Utils;

import android.util.Log;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public abstract class Logger {
    public abstract void d(String debug);

    public abstract void i(String info);

    public abstract void v(String verbose);

    public abstract void w(String warning);

    public abstract void e(String error);

    public abstract void e(String error, Throwable throwable);

    public abstract void e(Throwable throwable);

    public abstract void wtf(String assertion);

    public abstract void wtf(String assertion, Throwable throwable);

    public abstract void wtf(Throwable throwable);

    public static class DefaultLogger extends Logger {
        private static final String DEFAULT_TAG = "ModulePack Loader";

        @Override
        public void d(String debug) {
            Log.d(DEFAULT_TAG, debug);
        }

        @Override
        public void i(String info) {
            Log.i(DEFAULT_TAG, info);
        }

        @Override
        public void v(String verbose) {
            Log.v(DEFAULT_TAG, verbose);
        }

        @Override
        public void w(String warning) {
            Log.w(DEFAULT_TAG, warning);
        }

        @Override
        public void e(String error) {
            Log.e(DEFAULT_TAG, error);
        }

        @Override
        public void e(String error, Throwable throwable) {
            Log.e(DEFAULT_TAG, error, throwable);
        }

        @Override
        public void e(Throwable throwable) {
            Log.e(DEFAULT_TAG, throwable.getLocalizedMessage(), throwable);
        }

        @Override
        public void wtf(String assertion) {
            Log.wtf(DEFAULT_TAG, assertion);
        }

        @Override
        public void wtf(String assertion, Throwable throwable) {
            Log.wtf(DEFAULT_TAG, assertion, throwable);
        }

        @Override
        public void wtf(Throwable throwable) {
            Log.wtf(DEFAULT_TAG, throwable);
        }
    }
}
