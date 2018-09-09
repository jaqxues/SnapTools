package com.ljmu.andre.CBIDatabase;

import android.database.Cursor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.ljmu.andre.CBIDatabase.Adapters.AdapterHandler;
import com.ljmu.andre.CBIDatabase.Annotations.Adapter;
import com.ljmu.andre.CBIDatabase.Annotations.AutoIncrement;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Utils.CBIDescriptor;
import com.ljmu.andre.CBIDatabase.Utils.CBIDescriptor.ObjectType;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unchecked")
class CBITableHelper {
    final ImmutableMap<String, CBIDescriptor> headerDescriptorMap;
    String primaryKey;
    Class<? extends CBIObject> tableType;

    CBITableHelper(Class<? extends CBIObject> tableType) {
        this.tableType = tableType;
        headerDescriptorMap = buildDescriptorMap(tableType);
    }

    private ImmutableMap<String, CBIDescriptor> buildDescriptorMap(
            Class<? extends CBIObject> tableType) {
        ImmutableMap.Builder<String, CBIDescriptor> descriptorBuilder = new Builder<>();

        for (Field field : tableType.getDeclaredFields()) {
            field.setAccessible(true);

            CBIDescriptor descriptor = getDescriptor(field);
            if (descriptor == null)
                continue;

            descriptorBuilder.put(descriptor.getHeaderName(), descriptor);
        }

        for (Method method : tableType.getDeclaredMethods()) {
            method.setAccessible(true);

            CBIDescriptor descriptor = getDescriptor(method);
            if (descriptor == null)
                continue;

            descriptorBuilder.put(descriptor.getHeaderName(), descriptor);
        }

        return descriptorBuilder.build();
    }

    private CBIDescriptor getDescriptor(AccessibleObject accessibleObject) {
        TableField tableField = accessibleObject.getAnnotation(TableField.class);

        if (tableField == null)
            return null;

        CBIDescriptor descriptor =
                new CBIDescriptor()
                        .setHeaderName(tableField.value())
                        .setDefaultSqlValue(tableField.SQL_DEFAULT())
                        .setNotNull(tableField.NOT_NULL())
                        .setSqlType(tableField.SQL_TYPE());

        if (accessibleObject instanceof Field) {
            // ===========================================================================

            Field field = (Field) accessibleObject;
            descriptor.setField(field)
                    .setType(field.getType())
                    .setObjectType(ObjectType.FIELD);

            // ===========================================================================
        } else if (accessibleObject instanceof Method) {
            // ===========================================================================

            Method method = (Method) accessibleObject;
            descriptor.setType(method.getReturnType())
                    .setObjectType(
                            tableField.IS_GETTER() ?
                                    ObjectType.GETTER : ObjectType.SETTER
                    );

            // ===========================================================================
        }

        Adapter adapterAnnotation = accessibleObject.getAnnotation(Adapter.class);
        if (adapterAnnotation != null)
            descriptor.setBuildAdapter(adapterAnnotation.TYPE());

        if (descriptor.getSqlType() == null) {
            AdapterHandler.TypeAdapter typeAdapter = descriptor.getAppropriateAdapter();
            descriptor.setSqlType(typeAdapter.sqlType());
        }

        if (accessibleObject.getAnnotation(AutoIncrement.class) != null)
            descriptor.setAutoIncrement(true);

        if (accessibleObject.getAnnotation(PrimaryKey.class) != null) {
            if (primaryKey != null)
                throw new IllegalStateException("Cannot have multiple primary keys!");

            descriptor.setPrimaryKey(true);
            primaryKey = descriptor.getHeaderName();
        }

        return descriptor;
    }


    <T extends CBIObject> T buildFromCursor(Cursor cursor, QueryBuilder queryBuilder) throws IllegalAccessException, InstantiationException {
        CBIObject cbiObject = tableType.newInstance();

        Set<String> projectionSet = new HashSet<>(
                Arrays.asList(queryBuilder.getArrProjections())
        );

        for (CBIDescriptor descriptor : headerDescriptorMap.values()) {
            if (!projectionSet.isEmpty() && !projectionSet.contains(descriptor.getHeaderName()))
                continue;

            try {
                AdapterHandler.TypeAdapter typeAdapter = descriptor.getAppropriateAdapter();
                Object cursorResult = typeAdapter.fromCursor(cursor, descriptor.getHeaderName());
                if (cursorResult == null)
                    continue;

                descriptor.setDescriptorValue(cbiObject, cursorResult);
            } catch (Throwable e) {
                Timber.w(e.getMessage());
            }
        }

        return (T) cbiObject;
    }
}
