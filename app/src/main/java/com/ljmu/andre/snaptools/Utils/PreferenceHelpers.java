package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ljmu.andre.GsonPreferences.Preferences.Preference;

import java.util.Collection;
import java.util.Map;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.KILL_SC_ON_CHANGE;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PreferenceHelpers {
    public static boolean togglePreference(@NonNull Preference preference) {
        boolean newValue = !(boolean) getPref(preference);
        putPref(preference, newValue);
        return newValue;
    }

    public static void addToCollection(Preference preference, Object object) {
        addToCollection(preference, object, null);
    }

    public static void addToCollection(Preference preference, Object object, @Nullable Activity shouldKill) {

        Collection<Object> collection = getPref(preference);
        if (collection == null)
            collection = preference.getDefaultVal();


        collection.add(object);

        update(preference, collection, shouldKill);
    }

    private static void update(Preference preference, Object newObject, @Nullable Activity shouldKill) {
        if (shouldKill != null)
            putAndKill(preference, newObject, shouldKill);
        else
            putPref(preference, newObject);
    }

    public static void putAndKill(Preference preference, Object object, Activity activity) {
        putPref(preference, object);

        if (getPref(KILL_SC_ON_CHANGE))
            PackUtils.killSCService(activity);
    }

    public static void removeFromMap(Preference preference, Object key) {
        addToMap(preference, key, null);
    }

    public static void addToMap(Preference preference, Object key, Object value) {
        addToMap(preference, key, value, null);
    }

    public static void addToMap(Preference preference, Object key, Object value, @Nullable Activity shouldKill) {
        Map<Object, Object> map = getPref(preference);

        if (map == null)
            map = preference.getDefaultVal();

        map.put(key, value);

        update(preference, map, shouldKill);
    }

    public static void removeFromMap(Preference preference, Object key, @Nullable Activity shouldKill) {
        Map<Object, Object> map = getPref(preference);
        if (map == null)
            return;

        map.remove(key);
        update(preference, map, shouldKill);
    }

    public static boolean mapContainsKey(Preference preference, Object key) {
        Map<Object, Object> map = getPref(preference);
        return map != null && map.containsKey(key);
    }

    public static boolean mapContainsValue(Preference preference, Object key) {
        Map<Object, Object> map = getPref(preference);
        return map != null && map.containsValue(key);
    }

    public static <T> T getFromMap(Preference preference, Object key) {
        Map<Object, Object> map = getPref(preference);
        if (map == null)
            return null;

        return (T) map.get(key);
    }

    public static void removeFromCollection(Preference preference, Object object) {
        removeFromCollection(preference, object, null);
    }

    public static void removeFromCollection(Preference preference, Object object, @Nullable Activity shouldKill) {
        Collection<Object> collection = getPref(preference);
        if (collection == null)
            return;

        collection.remove(object);
        update(preference, collection, shouldKill);
    }

    public static boolean collectionContains(Preference preference, Object object) {
        Collection<Object> collection = getPref(preference);
        return collection != null && collection.contains(object);
    }
}
