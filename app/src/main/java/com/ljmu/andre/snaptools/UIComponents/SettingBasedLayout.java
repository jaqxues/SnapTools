package com.ljmu.andre.snaptools.UIComponents;

import android.app.Activity;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Deprecated
public class SettingBasedLayout<T> extends LinearLayout {
    protected Activity activity;
    private T settingKey;
    private LinearLayout headerLayout;
    private Map<T, SettingContainer<T>> settingContainerMap = new HashMap<>();

    public SettingBasedLayout(Activity activity, @Nullable T initialSettingKey) {
        super(activity);
        this.activity = activity;

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        setLayoutParams(params);

        headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(VERTICAL);
        headerLayout.setLayoutParams(params);
        addView(headerLayout);

        settingKey = initialSettingKey;
    }

    public void setHeader(View header) {
        Timber.d("Setting header: " + header);

        headerLayout.removeAllViews();
        headerLayout.addView(header);
    }

    public void addSettingContainer(SettingContainer<T> settingContainer) {
        getSettingContainerMap().put(settingContainer.getKey(), settingContainer);
    }

    protected Map<T, SettingContainer<T>> getSettingContainerMap() {
        return settingContainerMap;
    }

    public T getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(T settingKey) {
        this.settingKey = settingKey;
        update();
    }

    private void clearChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child == headerLayout)
                continue;

            removeView(child);
        }
    }

    public void update() {
        clearChildren();
        //removeAllViews();
        //addView(headerLayout);

        T settingKey = getSettingKey();
        SettingContainer activeSettingContainer = getSettingContainerMap().get(settingKey);

        if (activeSettingContainer != null) {
            addView(activeSettingContainer);
            activeSettingContainer.activatedBy(settingKey);
        }
    }
}
