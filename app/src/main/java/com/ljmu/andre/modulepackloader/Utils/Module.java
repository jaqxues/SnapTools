package com.ljmu.andre.modulepackloader.Utils;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 28.12.2018 - Time 18:42.
 */

public abstract class Module {
    protected ModuleLoadState moduleLoadState;
    private String name;
    private boolean canBeDisabled;
    private final Logger logger;

    public Module(String name, boolean canBeDisabled, Logger logger) {
        this.moduleLoadState = new ModuleLoadState(name);
        this.name = name;
        this.canBeDisabled = canBeDisabled;
        this.logger = logger;
    }

    public ModuleLoadState getModuleLoadState() {
        return moduleLoadState;
    }

    public String getName() {
        return name;
    }

    protected boolean canBeDisabled() {
        return canBeDisabled;
    }

    protected Logger getLogger() {
        return logger;
    }
}
