package com.ljmu.andre.modulepackloader.Utils;


/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 27.12.2018 - Time 17:09.
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

    public int resolvedHook() {
        return ++successfulHooks;
    }

    public int unresolvedHook() {
        if (getState() == State.SUCCESS)
            setState(State.ISSUES);
        return ++failedHooks;
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
}
