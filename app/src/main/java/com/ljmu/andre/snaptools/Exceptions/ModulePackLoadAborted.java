package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModulePackLoadAborted extends Exception {
    public static final String TAG = ModulePackLoadAborted.class.getSimpleName();
    private static final long serialVersionUID = -5016975394520051205L;

    public ModulePackLoadAborted(String message) {
        super(message);
    }

    public ModulePackLoadAborted(String message, Throwable cause) {
        super(message, cause);
    }
}
