package com.ljmu.andre.snaptools.ModulePack.UIComponents;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CenteredArrayAdapter<T> extends ArrayAdapter<T> {
    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, @NonNull List<T> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
    }

    @RequiresFramework(76)
    public CenteredArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return v;
    }
}
