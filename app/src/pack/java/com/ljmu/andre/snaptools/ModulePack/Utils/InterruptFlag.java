package com.ljmu.andre.snaptools.ModulePack.Utils;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class InterruptFlag {
    private boolean shouldInterrupt;

    public boolean shouldInterrupt() {
        return shouldInterrupt;
    }

    public InterruptFlag setShouldInterrupt(boolean shouldInterrupt) {
        this.shouldInterrupt = shouldInterrupt;
        return this;
    }

    public InterruptFlag interrupt() {
        this.shouldInterrupt = true;
        return this;
    }
}
