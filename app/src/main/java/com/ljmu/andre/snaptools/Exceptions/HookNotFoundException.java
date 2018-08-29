package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class HookNotFoundException extends Exception {
    @SuppressWarnings("unused")
    public static final String TAG = HookNotFoundException.class.getSimpleName();
    private static final long serialVersionUID = 2731618241718278132L;

    public HookNotFoundException(String message) {
        super(message);
    }

    public HookNotFoundException(Throwable e) {
        super(e);
    }
}
