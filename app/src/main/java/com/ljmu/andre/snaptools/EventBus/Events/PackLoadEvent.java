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
    private String packName;
    private String failReason;
    private File packFile;

    public PackLoadEvent(String packName, String failReason) {
        this.packName = packName;
        this.failReason = failReason;
    }

    public PackLoadEvent(ModulePack modulePack, File packFile) {
        this.packFile = packFile;
        this.modulePack = modulePack;
    }

    public String getPackName() {
        return packName;
    }

    public String getFailReason() {
        return failReason;
    }

    public File getPackFile() {
        return packFile;
    }

    public ModulePack getModulePack() {
        return modulePack;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("modulePack", modulePack)
                .add("packFile", packFile)
                .toString();
    }
}
