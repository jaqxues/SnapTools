package com.ljmu.andre.snaptools.ModulePack.Utils;

import com.google.common.base.MoreObjects;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Result<K, V> {
    private K key;
    private V value;

    public Result(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public Result setKey(K key) {
        this.key = key;
        return this;
    }

    public V getValue() {
        return value;
    }

    public Result setValue(V value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("key", key == null ? null : key.toString())
                .add("value", value == null ? null : value.toString())
                .toString();
    }

    public static class BadResult extends Result<Boolean, String> {
        public BadResult(String value) {
            super(false, value);
        }
    }
}
