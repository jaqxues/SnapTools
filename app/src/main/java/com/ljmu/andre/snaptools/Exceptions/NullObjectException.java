package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class NullObjectException extends Exception {
    @SuppressWarnings("unused")
    public static final String TAG = NullObjectException.class.getSimpleName();
    private static final long serialVersionUID = -4221668985038919489L;
    private final String objectName;

    public NullObjectException(String message, String objectName) {
        super(message);
        this.objectName = objectName;
    }

    public String getNullName() {
        return objectName;
    }
}
