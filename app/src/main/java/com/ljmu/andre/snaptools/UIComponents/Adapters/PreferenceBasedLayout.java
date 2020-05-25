package com.ljmu.andre.snaptools.UIComponents.Adapters;

import android.app.Activity;

import com.jaqxues.akrolyb.prefs.Preference;
import com.ljmu.andre.snaptools.UIComponents.SettingBasedLayout;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Deprecated
public class PreferenceBasedLayout<T> extends SettingBasedLayout<T> {
    private Preference<T> preference;

    public PreferenceBasedLayout(Activity activity, Preference<T> preference) {
        super(activity, null);
        this.preference = preference;
    }

    @Override
    public T getSettingKey() {
        return getPref(preference);
    }
}
