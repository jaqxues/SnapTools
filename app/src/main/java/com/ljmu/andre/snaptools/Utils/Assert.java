package com.ljmu.andre.snaptools.Utils;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Assert {
    public static String stringExists(String errorMsg, String string) throws IllegalArgumentException {
        if (string == null || string.isEmpty())
            throw new IllegalArgumentException(errorMsg);

        return string;
    }

    public static void notNull(String errorMsg, Object... params) throws IllegalArgumentException {
        for (Object obj : params) {
            if (obj == null)
                throw new IllegalArgumentException(errorMsg);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T notNull(String errorMsg, T param) throws IllegalArgumentException {
        if (param == null)
            throw new IllegalArgumentException(errorMsg);

        return param;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable, T2> T2 notNull(T throwable, T2 param) throws T {
        if (param == null)
            throw throwable;

        return param;
    }

    @RequiresFramework(73)
    public static boolean nonFatalAssert(String errorMsgTemplate, Object... params) {
        boolean hasNull = false;

        int index = -1;
        for (Object obj : params) {
            index++;

            if (obj == null) {
                params[index] = "null";
                hasNull = true;
            }
        }

        if (hasNull) {
            String errorMessage = String.format(errorMsgTemplate, params);
            Timber.e(new Exception(errorMessage));
        }

        return hasNull;
    }
}
