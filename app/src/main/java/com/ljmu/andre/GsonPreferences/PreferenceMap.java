package com.ljmu.andre.GsonPreferences;

import com.ljmu.andre.GsonPreferences.Preferences.Preference;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ===========================================================================
 * Map capable of being de/serialised without losing the generics
 * ===========================================================================
 */
class PreferenceMap extends ConcurrentHashMap<String, Preference> {
    private static final long serialVersionUID = 2162788535918724249L;

    PreferenceMap() {
    }

    PreferenceMap(Map<? extends String, ? extends Preference> m) {
        super(m);
    }
}
