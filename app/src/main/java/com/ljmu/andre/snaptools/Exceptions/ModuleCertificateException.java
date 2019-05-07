package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModuleCertificateException extends Exception {
    @SuppressWarnings("unused")
    public static final String TAG = ModuleCertificateException.class.getSimpleName();
    private static final long serialVersionUID = -1384895674499889911L;

    public ModuleCertificateException(String message) {
        super(message);
    }

    public ModuleCertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
