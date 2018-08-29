package com.ljmu.andre.snaptools.EventBus.Events;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LoadPackSettingsEvent {
    private String packName;

    public LoadPackSettingsEvent(String packName) {
        this.packName = packName;
    }

    public String getPackName() {
        return packName;
    }
}
