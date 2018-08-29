package com.ljmu.andre.snaptools.EventBus.Events;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BannerUpdateEvent {
    public static final int MASTER_SWITCH = 0;
    public static final int APK_UPDATE = 1;
    public static final int PACK_UPDATE = 2;

    public final int eventType;

    public BannerUpdateEvent(int eventType) {
        this.eventType = eventType;
    }
}
