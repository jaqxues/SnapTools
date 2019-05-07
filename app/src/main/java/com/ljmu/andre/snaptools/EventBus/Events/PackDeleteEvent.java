package com.ljmu.andre.snaptools.EventBus.Events;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackDeleteEvent {
    private String name;

    public PackDeleteEvent(String name) {
        this.name = name;
    }

    public String getPackName() {
        return name;
    }
}
