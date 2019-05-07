package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.app.Activity;

import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.UIComponents.SettingBasedLayout;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Deprecated
public class PreferenceBasedLayout<T> extends SettingBasedLayout<T> {
    private Preference preference;

    public PreferenceBasedLayout(Activity activity, Preference preference) {
        super(activity, null);
        this.preference = preference;
    }

    @Override
    public T getSettingKey() {
        return getPref(preference);
    }
}
