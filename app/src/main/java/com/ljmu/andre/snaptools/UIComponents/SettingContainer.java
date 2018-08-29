package com.ljmu.andre.snaptools.UIComponents;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Deprecated
public class SettingContainer<T> extends LinearLayout {
    private T key;

    public SettingContainer(Context context, T key) {
        super(context);

        setOrientation(VERTICAL);
        setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        );

        this.key = key;
    }

    public void activatedBy(Object parentKey) {

    }

    public T getKey() {
        return key;
    }
}
