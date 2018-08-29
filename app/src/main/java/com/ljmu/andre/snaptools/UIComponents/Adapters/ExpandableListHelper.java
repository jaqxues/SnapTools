package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class ExpandableListHelper extends BaseExpandableListAdapter {
    protected ExpandableListView listView;
    private int lastExpandedGroupPosition = -1;

    public <G, C> ExpandableListHelper(ExpandableListView listView, ItemLongClickListener<G, C> longClickListener) {
        this.listView = listView;

        if (longClickListener != null) {
            listView.setOnItemLongClickListener(
                    (parent, view, position, id) -> {
                        long packedPosition = listView.getExpandableListPosition(position);
                        int itemType = ExpandableListView.getPackedPositionType(packedPosition);

                        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                            longClickListener.onGroupLongClick((G) getGroup(groupPosition));
                            return true;
                        }

                        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
                        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
                            longClickListener.onChildLongClick((C) getChild(groupPosition, childPosition));

                        return true;
                    }
            );
        }
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if (lastExpandedGroupPosition != -1 && groupPosition != lastExpandedGroupPosition)
            listView.collapseGroup(lastExpandedGroupPosition);

        super.onGroupExpanded(groupPosition);
        lastExpandedGroupPosition = groupPosition;
    }

    public interface ItemLongClickListener<G, C> {
        void onChildLongClick(C child);

        void onGroupLongClick(G group);
    }
}
