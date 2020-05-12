package com.ljmu.andre.snaptools.ModulePack.UIComponents;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

import android.content.Context;
import android.content.res.Resources.Theme;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.AttributeSet;

/**
 * Created by santalu on 17/08/2017
 * A spinner that supports item click events.
 */

public class ReSpinner extends AppCompatSpinner {
    private OnItemClickListener onItemClickListener;

    public ReSpinner(Context context) {
        super(context);
    }

    public ReSpinner(Context context, int mode) {
        super(context, mode);
    }

    public ReSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    /**
     * If animate false spinner doesn't trigger click event
     * it will just set the selection
     */
    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (animate) {
            performItemClickCustom();
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        performItemClickCustom();
    }

    private void performItemClickCustom() {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, getSelectedView(), getSelectedItemPosition(), getSelectedItemId());
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        onItemClickListener = l;
    }
}