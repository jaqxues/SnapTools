package com.ljmu.andre.modulepackloader.Utils;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 28.12.2018 - Time 18:42.
 *
 * <p>
 *     This Class is only the very basic of a Module.
 *     {@link com.ljmu.andre.modulepackloader.ModuleHelper} shows how your Module could look like.
 *     You can (and should) extend this class to provide your own methods for every Module:
 *     <ul>
 *         <li>Implement UI Controls for the Modules (ie a Method that returns a Fragment)</li>
 *         <li>A class that handles Unhooking and Error Managing that can occur during hooking</li>
 *         <li>...</li>
 *     </ul>
 * </p>
 */

public abstract class Module {
    /**
     * ModuleLoadState of this Module.
     */
    protected ModuleLoadState moduleLoadState;
    private String name;
    private boolean canBeDisabled;

    /**
     * Constructor of a Base Module
     *
     * @param name The Name of this Module
     * @param canBeDisabled Determines if this Module can be loaded or unloaded by the User
     */
    public Module(String name, boolean canBeDisabled) {
        this.moduleLoadState = new ModuleLoadState(name);
        this.name = name;
        this.canBeDisabled = canBeDisabled;
    }

    /**
     * @return ModuleLoadState of this Module
     */
    public ModuleLoadState getModuleLoadState() {
        return moduleLoadState;
    }

    /**
     * @return Name of this module
     */
    public String getName() {
        return name;
    }

    /**
     * @return If the Module can be loaded or unloaded by the User
     */
    protected boolean canBeDisabled() {
        return canBeDisabled;
    }
}
