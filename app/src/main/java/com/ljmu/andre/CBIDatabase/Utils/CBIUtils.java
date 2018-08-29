package com.ljmu.andre.CBIDatabase.Utils;

import com.google.common.collect.ImmutableMap;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIObject;

import java.util.Map;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CBIUtils {
    public static Map<String, Object> getTableHeaders(Class<? extends CBIObject> cbiClass) {
        TableName tableNameAnnotation = cbiClass.getAnnotation(TableName.class);
        if (tableNameAnnotation == null)
            throw new IllegalArgumentException("A database table REQUIRES a TableName annotation");

        String name = tableNameAnnotation.value();
        int version = tableNameAnnotation.VERSION();

        if (name.equals("{NUL}"))
            throw new IllegalArgumentException("Table " + cbiClass.getCanonicalName() + " must be given a name");

        return ImmutableMap.<String, Object>builder()
                .put("name", name)
                .put("version", version)
                .build();
    }
}
