package com.ljmu.andre.snaptools.ModulePack.Events;

import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensObject;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LensEventRequest {
    private LensEvent lensEvent;
    private LensObject lens;

    public LensEventRequest(LensEvent lensEvent, LensObject lens) {
        this.lensEvent = lensEvent;
        this.lens = lens;
    }

    public LensEvent getLensEvent() {
        return lensEvent;
    }

    public LensObject getLens() {
        return lens;
    }

    public enum LensEvent {
        LOAD, UNLOAD, DELETE, ACTION_MENU
    }
}
