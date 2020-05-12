package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;

import java.util.List;

import static android.view.View.GONE;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StatefulListAdapter<T extends StatefulListable> extends ArrayAdapter<T> {
    public StatefulListAdapter(Context context) {
        super(context, R.layout.item_listable_head_stateful);
    }

    public StatefulListAdapter(Context context, T[] objects) {
        super(context, R.layout.item_listable_head_stateful, objects);
    }

    public StatefulListAdapter(Context context, List<T> objects) {
        super(context, R.layout.item_listable_head_stateful, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T object = getItem(position);

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(
                            getModuleContext(parent.getContext())
                    ).inflate(
                            R.layout.item_listable_head_text_stateful, parent, false
                    );
        }

        convertView.findViewById(R.id.img_arrow).setVisibility(GONE);

        object.updateHeaderText(convertView, (TextView) convertView.findViewById(R.id.txt_listable));
        object.updateHeaderStateHolder((TextView) convertView.findViewById(R.id.txt_state));
        return convertView;
    }
}
