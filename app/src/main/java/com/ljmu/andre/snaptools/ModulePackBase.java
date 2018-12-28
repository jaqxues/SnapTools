package com.ljmu.andre.snaptools;

import com.ljmu.andre.modulepackloader.ModulePack;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class ModulePackBase extends ModulePack {
    private String baseTest;

    public ModulePackBase(String baseTest) {
        this.baseTest = baseTest;
    }

    @Override
    public String toString() {
        return "ModulePackBase{" +
                "baseTest='" + baseTest + '\'' +
                "} " + super.toString();
    }

    @Override
    public void loadModules() {

    }
}
