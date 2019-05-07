package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class UnassignedException extends Exception {
    @SuppressWarnings("unused")
    public static final String TAG = UnassignedException.class.getSimpleName();
    private static final long serialVersionUID = 6737146204714796106L;

    public UnassignedException(String message) {
        super(message);
    }
}
