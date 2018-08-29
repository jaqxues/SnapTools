package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModulePackFatalError extends Exception {
    @SuppressWarnings("unused")
    public static final String TAG = ModulePackFatalError.class.getSimpleName();
    private static final long serialVersionUID = -3282028503225055485L;

    public ModulePackFatalError(String message) {
        super(message);
    }

    public ModulePackFatalError(String message, Throwable cause) {
        super(message, cause);
    }
}
