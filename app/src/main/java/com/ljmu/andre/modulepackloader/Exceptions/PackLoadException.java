package com.ljmu.andre.modulepackloader.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class PackLoadException extends Exception {
    public PackLoadException() {
    }

    public PackLoadException(String message) {
        super(message);
    }

    public PackLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackLoadException(Throwable cause) {
        super(cause);
    }
}
