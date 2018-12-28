package com.ljmu.andre.modulepackloader.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 27.12.2018 - Time 17:19.
 */

public class PackLoadState extends LoadState {
    private Map<String, ModuleLoadState> moduleLoadStates = new LinkedHashMap<>();
    private int failedModules;
    private int successfulModules;
    
    public PackLoadState() {
        super(null);
    }

    public Map<String, ModuleLoadState> getModuleLoadStates() {
        return moduleLoadStates;
    }

    public PackLoadState setModuleLoadStates(Map<String, ModuleLoadState> moduleLoadStates) {
        this.moduleLoadStates = moduleLoadStates;
        refreshPackLoadState();
        return this;
    }

    /**
     * ===========================================================================
     * Refresh the load state of the object to ensure that it accurately reflects
     * the states of the child ModuleLoadStates.
     * This should be performed whenever an item in {@link this#moduleLoadStates}
     * is added, removed, or altered.
     * ===========================================================================
     */
    public PackLoadState refreshPackLoadState() {
        int moduleIssues = 0;
        failedModules = 0;
        successfulModules = 0;

        for (ModuleLoadState loadState : moduleLoadStates.values()) {
            if (loadState.hasFailed())
                failedModules++;
            else if (loadState.getState() == State.CUSTOM)
                moduleIssues++;
            else
                successfulModules++;
        }

        // If there were any failures in the children, the pack has failed ===========
        if (failedModules > 0)
            setState(State.FAILED);
        else if (moduleIssues > 0)
            setState(State.ISSUES);
        else
            setState(State.SUCCESS);

        return this;

    }

    public PackLoadState addModuleLoadStates(ModuleLoadState... loadStates) {
        for (ModuleLoadState loadState : loadStates)
            moduleLoadStates.put(loadState.getName(), loadState);

        refreshPackLoadState();

        return this;
    }

    public PackLoadState addModuleLoadState(ModuleLoadState loadState) {
        moduleLoadStates.put(loadState.getName(), loadState);

        refreshPackLoadState();

        return this;
    }

    public PackLoadState removeModuleLoadState(ModuleLoadState loadState) {
        moduleLoadStates.remove(loadState.getName());

        refreshPackLoadState();

        return this;
    }

    public void fail() {
        setState(State.FAILED);
    }

    public int getFailedModules() {
        return failedModules;
    }

    public int getSuccessfulModules() {
        return successfulModules;
    }

    @Override
    public String getBasicBreakdown() {
        if (failedModules > 0)
            return String.format("[%s/%s]", failedModules, moduleLoadStates.size());

        return getState().getDisplay();
    }

    @Override
    public boolean hasFailed() {
        return getState() == State.FAILED || getState() == State.ISSUES || failedModules > 0;
    }
}
