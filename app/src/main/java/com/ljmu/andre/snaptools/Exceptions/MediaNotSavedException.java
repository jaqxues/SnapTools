package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MediaNotSavedException extends Exception {

    private static final long serialVersionUID = 1788823538611033911L;

    public MediaNotSavedException(String message) {
        super(message);
    }

    public MediaNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
