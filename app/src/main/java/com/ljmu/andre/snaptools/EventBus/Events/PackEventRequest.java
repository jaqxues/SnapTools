package com.ljmu.andre.snaptools.EventBus.Events;

import com.google.common.base.MoreObjects;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackEventRequest {
    private EventRequest request;
    private String packName;

    public PackEventRequest(EventRequest request, String packname) {
        this.request = request;
        this.packName = packname;
    }

    public EventRequest getRequest() {
        return request;
    }

    public PackEventRequest setRequest(EventRequest request) {
        this.request = request;
        return this;
    }

    public String getPackName() {
        return packName;
    }

    public PackEventRequest setPackName(String packName) {
        this.packName = packName;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("request", request)
                .add("packName", packName)
                .toString();
    }

    public enum EventRequest {
        UNLOAD, LOAD, DELETE, DOWNLOAD, DOWNLOAD_TUTORIAL, SHOW_ROLLBACK, SHOW_CHANGELOG
    }

    public interface EventHandler {
        void handleEvent(PackEventRequest packEventRequest);

        EventHandler ignoreEvents = packEventRequest -> Timber.d("Ignoring event #%d", packEventRequest.request.ordinal());
    }
}
