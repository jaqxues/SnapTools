package com.ljmu.andre.snaptools.Framework.Utils;

import com.google.common.base.MoreObjects;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModuleLoadState extends LoadState {
    private int failedHooks;
    private int successfulHooks;

    public ModuleLoadState(String name) {
        super(name);
    }

    public ModuleLoadState fail() {
        setState(State.FAILED);

        failedHooks++;
        return this;
    }

    public ModuleLoadState success() {
        successfulHooks++;
        return this;
    }

    public int getFailedHooks() {
        return failedHooks;
    }

    public int getSuccessfulHooks() {
        return successfulHooks;
    }

    /**
     * ===========================================================================
     * A breakdown of the LoadState that can be displayed to the user
     * to explain the reasoning.
     * ===========================================================================
     */
    @Override
    public String getBasicBreakdown() {
        if (failedHooks > 0)
            return String.format("[%s/%s]", failedHooks, failedHooks + successfulHooks);

        return getState().getDisplay();
    }

    @Override
    public boolean hasFailed() {
        return getState() == State.FAILED || getState() == State.ISSUES || failedHooks > 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", super.getName())
                .add("state", super.getState())
                .add("failedHooks", failedHooks)
                .add("successfulHooks", successfulHooks)
                .toString();
    }
}
