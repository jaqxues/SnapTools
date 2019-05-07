package com.ljmu.andre.snaptools.Utils;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FinalCounter {
    private int val;

    public FinalCounter(int initialVal) {
        val = initialVal;
    }

    public int increment() {
        return ++val;
    }

    public int decrement() {
        return --val;
    }

    public int get() {
        return val;
    }
}
