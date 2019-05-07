package com.ljmu.andre.snaptools.Utils;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public interface Callable<T> {
    void call(T t);

    public static interface Provider<O> {
        O call();
    }
}
