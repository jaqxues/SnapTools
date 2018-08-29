package com.ljmu.andre.snaptools.Exceptions;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MessageLifespanException extends Exception {
    private static final long serialVersionUID = -6390549789436256506L;

    public MessageLifespanException() {
        super("Message lifespan expired");
    }
}
