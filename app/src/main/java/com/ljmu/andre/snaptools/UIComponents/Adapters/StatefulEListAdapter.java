package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import timber.log.Timber;

import java.util.List;
import java.util.Map;

import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StatefulEListAdapter<G extends StatefulListable, C> extends ExpandableListHelper {
    private List<? extends StatefulListable> listables;
    private Map<?, G> listableMap;

    public StatefulEListAdapter(
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
        G listable = (G) getGroup(groupPosition);

        if (convertView == null) {
            Context moduleContext = getModuleContext(parent.getContext());

            convertView =
                    LayoutInflater.from(
                            moduleContext
                    ).inflate(
                            getLayout(moduleContext, "item_listable_head_text_stateful"), parent, false
                    );
        }

        try {
            ImageView imgArrow = ResourceUtils.getView(convertView, "img_arrow");

            StateListDrawable drawable = (StateListDrawable) imgArrow.getDrawable();
            int[] states = new int[1];
            if (isExpanded)
                states[0] = android.R.attr.state_expanded;
            drawable.setState(states);
        } catch (NullPointerException e) {
            Timber.e(new Exception("No ImgArrow found for: " + convertView, e));
        }

        listable.updateHeaderText(convertView, getView(convertView, "txt_listable"));
        listable.updateHeaderStateHolder(getView(convertView, "txt_state"));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        G group = (G) getGroup(groupPosition);
        C child = (C) group.getChildren().get(childPosition);
        Context moduleContext = getModuleContext(parent.getContext());

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(
                            moduleContext
                    ).inflate(
                            getLayout(moduleContext, "item_listable_msg_stateful"), parent, false
                    );
        }

        TextView messageHolder = getView(convertView, "txt_listable");

        group.updateMessageText(convertView, messageHolder, child);
        group.updateMessageStateHolder(getView(convertView, "img_state"), child);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface StatefulListable<T> {
        List<T> getChildren();

        void updateHeaderText(View container, TextView headerHolder);

        void updateMessageText(View container, TextView messageHolder, T child);

        void updateHeaderStateHolder(TextView stateHolder);

        void updateMessageStateHolder(TextView stateHolder, T child);
    }
}
