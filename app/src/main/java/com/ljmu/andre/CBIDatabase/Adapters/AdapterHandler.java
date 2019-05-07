package com.ljmu.andre.CBIDatabase.Adapters;

import android.database.Cursor;

import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.BlobAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.BooleanAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.DoubleAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.IntegerAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.ListAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.LongAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.MapAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.ShortAdapter;
import com.ljmu.andre.CBIDatabase.Adapters.DefaultAdapters.StringAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AdapterHandler {
    private static final HashMap<Class, TypeAdapter> adapterMap = new HashMap<>();

    static {
        ShortAdapter shortAdapter = new ShortAdapter();
        adapterMap.put(Short.class, shortAdapter);
        adapterMap.put(short.class, shortAdapter);

        IntegerAdapter integerAdapter = new IntegerAdapter();
        adapterMap.put(Integer.class, integerAdapter);
        adapterMap.put(int.class, integerAdapter);

        LongAdapter longAdapter = new LongAdapter();
        adapterMap.put(Long.class, longAdapter);
        adapterMap.put(long.class, longAdapter);

        DoubleAdapter doubleAdapter = new DoubleAdapter();
        adapterMap.put(Double.class, doubleAdapter);
        adapterMap.put(double.class, doubleAdapter);

        BooleanAdapter booleanAdapter = new BooleanAdapter();
        adapterMap.put(Boolean.class, booleanAdapter);
        adapterMap.put(boolean.class, booleanAdapter);

        adapterMap.put(String.class, new StringAdapter());
        adapterMap.put(byte[].class, new BlobAdapter());

        adapterMap.put(List.class, new ListAdapter());
        adapterMap.put(Map.class, new MapAdapter());
    }

    public static <T> TypeAdapter<T> getAdapter(Class clazz) {
        synchronized (adapterMap) {
            return adapterMap.get(clazz);
        }
    }

    public static void registerAdapter(Class clazz, TypeAdapter adapter) {
        synchronized (adapterMap) {
            adapterMap.put(clazz, adapter);
        }
    }

    public static void removeAdapter(Class clazz) {
        synchronized (adapterMap) {
            adapterMap.remove(clazz);
        }
    }

    public interface TypeAdapter<T> {
        T fromCursor(Cursor cursor, String column);

        String toSQL(T object);

        String sqlType();
    }
}
