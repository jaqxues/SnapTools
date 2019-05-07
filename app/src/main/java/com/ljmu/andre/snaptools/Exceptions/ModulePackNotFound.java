package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModulePackNotFound extends Exception {
    private static final long serialVersionUID = -8252214568248350849L;

    public ModulePackNotFound(String message) {
        super(message);
    }

    public ModulePackNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public ModulePackNotFound(Throwable cause) {
        super(cause);
    }
}
