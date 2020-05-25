package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jaqxues.akrolyb.prefs.Preference;


import java.util.Collection;
import java.util.Map;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.jaqxues.akrolyb.prefs.PrefManagerKt.putPref;
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

    public static <T> void addToCollection(Preference<? extends Collection<T>> preference, T object) {
        addToCollection(preference, object, null);
    }

    public static <T> void addToCollection(Preference<? extends Collection<T>> preference, T object, @Nullable Activity shouldKill) {
        Collection<T> collection = getPref(preference);

        collection.add(object);
        update(preference, collection, shouldKill);
    }

    private static void update(Preference preference, Object newObject, @Nullable Activity shouldKill) {
        if (shouldKill != null)
            putAndKill(preference, newObject, shouldKill);
        else
            putPref(preference, newObject);
    }

    public static <T> void putAndKill(Preference<T> preference, T object, Activity activity) {
        putPref(preference, object);

        if (getPref(KILL_SC_ON_CHANGE))
            PackUtils.killSCService(activity);
    }

    public static <K, V> void removeFromMap(Preference<? extends Map<K, V>> preference, K key) {
        addToMap(preference, key, null);
    }

    public static <K, V> void addToMap(Preference<? extends Map<K, V>> preference, K key, V value) {
        addToMap(preference, key, value, null);
    }

    public static <K, V> void addToMap(Preference<? extends Map<K, V>> preference, K key, V value, @Nullable Activity shouldKill) {
        Map<K, V> map = getPref(preference);

        map.put(key, value);

        update(preference, map, shouldKill);
    }

    public static <K, V> void removeFromMap(Preference<? extends Map<K, V>> preference, K key, @Nullable Activity shouldKill) {
        Map<K, V> map = getPref(preference);
        if (map == null)
            return;

        map.remove(key);
        update(preference, map, shouldKill);
    }

    public static <K, V> boolean mapContainsKey(Preference<? extends Map<K, V>> preference, K key) {
        Map<K, V> map = getPref(preference);
        return map != null && map.containsKey(key);
    }

    public static <K, V> boolean mapContainsValue(Preference<? extends Map<K, V>> preference, K key) {
        Map<K, V> map = getPref(preference);
        return map != null && map.containsValue(key);
    }

    public static <K, V> V getFromMap(Preference<? extends Map<K, V>> preference, K key) {
        Map<K, V> map = getPref(preference);
        if (map == null)
            return null;

        return map.get(key);
    }

    public static <T> void removeFromCollection(Preference<? extends Collection<T>> preference, T object) {
        removeFromCollection(preference, object, null);
    }

    public static <T> void removeFromCollection(Preference<? extends Collection<T>> preference, T object, @Nullable Activity shouldKill) {
        Collection<T> collection = getPref(preference);
        if (collection == null)
            return;

        collection.remove(object);
        update(preference, collection, shouldKill);
    }

    public static <T> boolean collectionContains(Preference<? extends Collection<T>> preference, T object) {
        Collection<T> collection = getPref(preference);
        return collection != null && collection.contains(object);
    }
}
