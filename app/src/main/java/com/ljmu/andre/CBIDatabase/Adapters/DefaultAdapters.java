package com.ljmu.andre.CBIDatabase.Adapters;

import android.database.Cursor;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DefaultAdapters {
    private static final Gson gson = new Gson();

    static class ShortAdapter implements AdapterHandler.TypeAdapter<Short> {
        @Override
        public Short fromCursor(Cursor cursor, String column) {
            return cursor.getShort(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(Short value) {
            return Short.toString(value);
        }

        @Override
        public String sqlType() {
            return "INTEGER";
        }
    }

    static class IntegerAdapter implements AdapterHandler.TypeAdapter<Integer> {
        @Override
        public Integer fromCursor(Cursor cursor, String column) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(Integer value) {
            return Integer.toString(value);
        }

        @Override
        public String sqlType() {
            return "INTEGER";
        }
    }

    static class LongAdapter implements AdapterHandler.TypeAdapter<Long> {
        @Override
        public Long fromCursor(Cursor cursor, String column) {
            return cursor.getLong(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(Long value) {
            return Long.toString(value);
        }

        @Override
        public String sqlType() {
            return "INTEGER";
        }
    }

    static class BlobAdapter implements AdapterHandler.TypeAdapter<byte[]> {
        @Override
        public byte[] fromCursor(Cursor cursor, String column) {
            return cursor.getBlob(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(byte[] value) {
            return Arrays.toString(value);
        }

        @Override
        public String sqlType() {
            return "BLOB";
        }
    }

    static class BooleanAdapter implements AdapterHandler.TypeAdapter<Boolean> {
        @Override
        public Boolean fromCursor(Cursor cursor, String column) {
            return cursor.getShort(cursor.getColumnIndexOrThrow(column)) == 1;
        }

        @Override
        public String toSQL(Boolean object) {
            return object ? "1" : "0";
        }

        @Override
        public String sqlType() {
            return "INTEGER";
        }
    }

    static class DoubleAdapter implements AdapterHandler.TypeAdapter<Double> {

        @Override
        public Double fromCursor(Cursor cursor, String column) {
            return cursor.getDouble(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(Double object) {
            return Double.toString(object);
        }

        @Override
        public String sqlType() {
            return "DOUBLE";
        }
    }

    static class StringAdapter implements AdapterHandler.TypeAdapter<String> {
        @Override
        public String fromCursor(Cursor cursor, String column) {
            return cursor.getString(cursor.getColumnIndexOrThrow(column));
        }

        @Override
        public String toSQL(String object) {
            return object;
        }

        @Override
        public String sqlType() {
            return "TEXT";
        }
    }

    static class ListAdapter implements AdapterHandler.TypeAdapter<List> {
        @Override
        public List fromCursor(Cursor cursor, String column) {
            String sql = cursor.getString(cursor.getColumnIndexOrThrow(column));

            return gson.fromJson(sql, List.class);
        }

        @Override
        public String toSQL(List object) {
            return gson.toJson(object);
        }

        @Override
        public String sqlType() {
            return "TEXT";
        }
    }

    static class MapAdapter implements AdapterHandler.TypeAdapter<Map> {
        @Override
        public Map fromCursor(Cursor cursor, String column) {
            String sql = cursor.getString(cursor.getColumnIndexOrThrow(column));

            return gson.fromJson(sql, Map.class);
        }

        @Override
        public String toSQL(Map object) {
            return gson.toJson(object);
        }

        @Override
        public String sqlType() {
            return "TEXT";
        }
    }
}
