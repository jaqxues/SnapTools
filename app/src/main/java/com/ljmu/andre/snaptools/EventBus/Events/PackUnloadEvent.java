package com.ljmu.andre.snaptools.EventBus.Events;

import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackUnloadEvent {
    private LocalPackMetaData packMetaData;

    public PackUnloadEvent(LocalPackMetaData packMetaData) {
        this.packMetaData = packMetaData;
    }

    public LocalPackMetaData getPackMetaData() {
        return packMetaData;
    }
}
