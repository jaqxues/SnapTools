package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MessageNotBuiltException extends Exception {
    private static final long serialVersionUID = -8438760880213965750L;

    public MessageNotBuiltException() {
    }

    public MessageNotBuiltException(String message) {
        super(message);
    }

    public MessageNotBuiltException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageNotBuiltException(Throwable cause) {
        super(cause);
    }
}
