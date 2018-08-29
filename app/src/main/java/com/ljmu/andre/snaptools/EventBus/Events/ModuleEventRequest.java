package com.ljmu.andre.snaptools.EventBus.Events;

import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ModuleEventRequest {
    private EventRequest eventRequest;
    private String packName;
    private String moduleName;

    public ModuleEventRequest(EventRequest eventRequest, String packName, String moduleName) {
        this.eventRequest = eventRequest;
        this.packName = packName;
        this.moduleName = moduleName;
    }

    public ModuleEventRequest(EventRequest eventRequest, String moduleName) {
        this.eventRequest = eventRequest;
        this.moduleName = moduleName;
    }

    public EventRequest getEventRequest() {
        return eventRequest;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getPackName() {
        return packName;
    }
}
