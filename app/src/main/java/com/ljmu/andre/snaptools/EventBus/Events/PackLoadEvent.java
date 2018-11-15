package com.ljmu.andre.snaptools.EventBus.Events;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Framework.ModulePack;

import java.io.File;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackLoadEvent {
    private ModulePack modulePack;
    private String failReason;

    public PackLoadEvent(String failReason) {
        this.failReason = failReason;
    }

    public PackLoadEvent(ModulePack modulePack) {
        this.modulePack = modulePack;
    }

    public String getFailReason() {
        return failReason;
    }

    public ModulePack getModulePack() {
        return modulePack;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("modulePack", modulePack)
                .toString();
    }
}
