package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.EListAdapter.Listable;

import java.util.List;

import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class EListAdapter<G extends Listable, C> extends ExpandableListHelper {
    private List<G> listables;

    public EListAdapter(
            ExpandableListView listView,
            List<G> listables,
            ItemLongClickListener<G, C> longClickListener) {

        super(listView, longClickListener);
        this.listables = listables;
    }

    @Override
    public int getGroupCount() {
        return listables.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listables.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listables.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listables.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Listable listable = (Listable) getGroup(groupPosition);

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(
                            getModuleContext(parent.getContext())
                    ).inflate(
                            R.layout.item_listable_head, parent, false
                    );
        }

        ImageView imgArrow = (ImageView) convertView.findViewById(R.id.img_arrow);

        StateListDrawable drawable = (StateListDrawable) imgArrow.getDrawable();
        int[] states = new int[1];
        if (isExpanded)
            states[0] = android.R.attr.state_expanded;
        drawable.setState(states);

        listable.updateHeaderText(convertView, (TextView) convertView.findViewById(R.id.txt_listable));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        G group = (G) getGroup(groupPosition);
        C child = (C) group.getChildren().get(childPosition);
        String message = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(
                    getModuleContext(parent.getContext())
            ).inflate(
                    R.layout.item_listable_msg, parent, false);
        }

        TextView textView = ((TextView) convertView.findViewById(R.id.txt_listable));
        group.updateMessageText(convertView, textView, child);
        textView.setText(message);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface Listable<T> {
        List<T> getChildren();

        void updateHeaderText(View container, TextView messageHolder);

        void updateMessageText(View container, TextView messageHolder, T child);
    }
}
