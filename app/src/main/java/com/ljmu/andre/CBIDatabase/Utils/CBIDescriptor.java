package com.ljmu.andre.CBIDatabase.Utils;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.CBIDatabase.Adapters.AdapterHandler;
import com.ljmu.andre.CBIDatabase.Adapters.AdapterHandler.TypeAdapter;
import timber.log.Timber;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CBIDescriptor {
    private ObjectType objectType;

    private String headerName;
    private Class type;
    private Field field;
    private Method method;
    private String sqlType;
    private String sqlDefaultValue;
    private boolean notNull;
    private boolean autoIncrement;
    private boolean primaryKey;
    private Class<? extends TypeAdapter> buildAdapter;

    public ObjectType getObjectType() {
        return objectType;
    }

    public CBIDescriptor setObjectType(ObjectType objectType) {
        this.objectType = objectType;
        return this;
    }

    public String getHeaderName() {
        return headerName;
    }

    public CBIDescriptor setHeaderName(String headerName) {
        this.headerName = headerName;
        return this;
    }

    public Object getDescriptorValue(Object instance) {
        try {
            switch (objectType) {
                case GETTER:
                    method.setAccessible(true);
                    return method.invoke(instance);
                case FIELD:
                    field.setAccessible(true);
                    return field.get(instance);
                default:
                    throw new IllegalArgumentException("Unknown object type: " + objectType);
            }
        } catch (InvocationTargetException e) {
            Timber.e(e);
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            Timber.e(e);
            throw new IllegalStateException("Method/Field not the correct Access Level", e);
        }
    }

    public void setDescriptorValue(Object instance, Object value) {
        try {
            switch (objectType) {
                case SETTER:
                    method.setAccessible(true);
                    method.invoke(instance, value);
                    break;
                case FIELD:
                    field.setAccessible(true);
                    field.set(instance, value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown object type: " + objectType);
            }
        } catch (InvocationTargetException e) {
            Timber.e(e);
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            Timber.e(e);
            throw new IllegalStateException("Method/Field not the correct Access Level", e);
        }
    }

    @Deprecated
    public Field getField() {
        return field;
    }

    public CBIDescriptor setField(Field field) {
        this.field = field;
        return this;
    }

    @Deprecated
    public Method getMethod() {
        return method;
    }

    public CBIDescriptor setMethod(Method method) {
        this.method = method;
        return this;
    }

    public String getSqlType() {
        return sqlType;
    }

    public CBIDescriptor setSqlType(String sqlType) {
        if (sqlType.equals("{NUL}"))
            sqlType = null;

        this.sqlType = sqlType;
        return this;
    }

    public String getDefaultSqlValue() {
        return sqlDefaultValue;
    }

    public CBIDescriptor setDefaultSqlValue(String sqlDefaultValue) {
        if (sqlDefaultValue.equals("{NUL}"))
            sqlDefaultValue = null;

        this.sqlDefaultValue = sqlDefaultValue;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public CBIDescriptor setNotNull(boolean notNull) {
        this.notNull = notNull;
        return this;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public CBIDescriptor setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public CBIDescriptor setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public Class getBuildAdapter() {
        return buildAdapter;
    }

    public CBIDescriptor setBuildAdapter(Class<? extends TypeAdapter> buildAdapter) {
        this.buildAdapter = buildAdapter;
        return this;
    }

    public <T extends TypeAdapter> T getAppropriateAdapter() {
        return (T) AdapterHandler.getAdapter(getType());
    }

    public Class getType() {
        return type;
    }

    public CBIDescriptor setType(Class type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("headerName", headerName)
                .add("type", type)
                .add("field", field)
                .add("sqlType", sqlType)
                .add("sqlDefaultValue", sqlDefaultValue)
                .add("autoIncrement", autoIncrement)
                .add("primaryKey", primaryKey)
                .add("buildAdapter", buildAdapter)
                .toString();
    }

    public enum ObjectType {
        GETTER, SETTER, FIELD
    }
}

