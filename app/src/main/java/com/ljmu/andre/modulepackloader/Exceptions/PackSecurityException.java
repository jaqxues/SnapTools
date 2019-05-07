package com.ljmu.andre.modulepackloader.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class PackSecurityException extends Exception {
    public PackSecurityException() {
    }

    public PackSecurityException(String message) {
        super(message);
    }

    public PackSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackSecurityException(Throwable cause) {
        super(cause);
    }
}
