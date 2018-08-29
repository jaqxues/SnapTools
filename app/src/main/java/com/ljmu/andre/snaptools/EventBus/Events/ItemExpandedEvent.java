package com.ljmu.andre.snaptools.EventBus.Events;

import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ItemExpandedEvent {
    ExpandableItemEntity expandedItem;

    public ItemExpandedEvent(ExpandableItemEntity expandedItem) {
        this.expandedItem = expandedItem;
    }

    public ExpandableItemEntity getExpandedItem() {
        return expandedItem;
    }
}
