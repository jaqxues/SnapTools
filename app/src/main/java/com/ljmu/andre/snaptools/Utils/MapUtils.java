package com.ljmu.andre.snaptools.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MapUtils {
    public static <K, V> Map<K, V> convertList(Collection<V> collection, KeyBinder<K, V> binder) {
        return convertList(new HashMap<>(), collection, binder);
    }

    public static <K, V> Map<K, V> convertList(Map<K, V> map, Collection<V> collection, KeyBinder<K, V> binder) {
        if (collection == null)
            return map;

        for (V v : collection)
            map.put(binder.getKey(v), v);

        return map;
    }

    @RequiresFramework(73)
    public static <K, V> Map<K, V> convertArray(Map<K, V> map, V[] array, KeyBinder<K, V> binder) {
        if (array == null)
            return map;

        for (V v : array) {
            map.put(binder.getKey(v), v);
        }

        return map;
    }

    public interface KeyBinder<K, V> {
        K getKey(V mapEntry);
    }
}
