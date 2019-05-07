package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class UnAuthorisedError extends Exception {
    private static final long serialVersionUID = 640836913086298114L;

    public UnAuthorisedError() {
    }

    public UnAuthorisedError(String message) {
        super(message);
    }
}
