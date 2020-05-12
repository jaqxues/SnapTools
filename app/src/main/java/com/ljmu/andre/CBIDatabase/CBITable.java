package com.ljmu.andre.CBIDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ljmu.andre.CBIDatabase.Adapters.AdapterHandler.TypeAdapter;
import com.ljmu.andre.CBIDatabase.Utils.CBIDescriptor;
import com.ljmu.andre.CBIDatabase.Utils.CBIUtils;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unchecked")
public class CBITable<T extends CBIObject> extends CBITableHelper {
    private CBIDatabaseCore linkedDatabase;
    private String tableName;
    private int tableVersion;

    public CBITable(
            Class<? extends CBIObject> tableType,
            CBIDatabaseCore linkedDatabase) {
        super(tableType);

        this.linkedDatabase = linkedDatabase;

        Map<String, Object> headerMap = CBIUtils.getTableHeaders(tableType);
        tableName = (String) headerMap.get("name");
        tableVersion = (int) headerMap.get("version");
    }

    public void createTable() {
        SQLiteDatabase database = linkedDatabase.getDatabase();

        database.execSQL(buildTableCreateQuery());
    }

    @NonNull
    private String buildTableCreateQuery() {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        Iterator<CBIDescriptor> descriptorIterator = headerDescriptorMap.values().iterator();

        while (descriptorIterator.hasNext()) {
            CBIDescriptor descriptor = descriptorIterator.next();

            builder.append(descriptor.getHeaderName()).append(" ");

            builder.append(descriptor.getSqlType());

            if (descriptor.getDefaultSqlValue() != null)
                builder.append(" DEFAULT ").append(descriptor.getDefaultSqlValue());

            if (descriptor.isPrimaryKey())
                builder.append(" PRIMARY KEY");

            if (descriptor.isAutoIncrement())
                builder.append(" AUTO_INCREMENT");

            if (descriptorIterator.hasNext())
                builder.append(",");
        }

        builder.append(")");

        Timber.d("BuildQuery: " + builder.toString());

        return builder.toString();
    }

    public void dropTable() {
        linkedDatabase.unregisterTable(tableType);
        linkedDatabase.runCommand(new SQLCommand("DROP TABLE IF EXISTS " + getTableName()));
    }

    public String getTableName() {
        return tableName;
    }

    public int getTableVersion() {
        return tableVersion;
    }

    public long getRowCount() {
        return getRowCount(null, null);
    }

    public long getRowCount(String selection, String[] selectionArgs) {
        return DatabaseUtils.queryNumEntries(linkedDatabase.getDatabase(), tableName, selection, selectionArgs);
    }

    public boolean contains(String primaryKeyValue) {
        return contains(primaryKey, primaryKeyValue);
    }

    @RequiresFramework(78)
    public boolean contains(String columnName, String columnValue) {
        return getRowCount(columnName + " = ?", new String[]{columnValue}) > 0;
    }

    public boolean columnExists(String columnName) {
        Cursor emptyCursor = null;

        Timber.d("Checking if column '%s' exists in table '%s'", columnName, tableName);

        try {
            emptyCursor = linkedDatabase.getDatabase().rawQuery(
                    "SELECT * FROM " + tableName + " LIMIT 0",
                    null
            );

            int columnIndex = emptyCursor.getColumnIndex(columnName);
            Timber.d("Column Index: " + columnIndex);

            return columnIndex != -1;
        } catch (Exception e) {
            Timber.e(e, "Problem checking if column exists!");
        } finally {
            if (emptyCursor != null)
                emptyCursor.close();
        }

        return false;
    }

    public boolean deleteAll() {
        return deleteAll(null, null);
    }

    public boolean deleteAll(String whereClause, String[] whereArgs) {
        SQLiteDatabase database = linkedDatabase.getDatabase();

        int deletedRows = database.delete(tableName, null, null);

        return deletedRows > 0;
    }

    public boolean delete(@NonNull T cbiObject) {
        if (!cbiObject.getClass().equals(tableType))
            throw new IllegalArgumentException("CBIObject Type Mismatch. Expecting: " + tableType.getSimpleName());

        SQLiteDatabase database = linkedDatabase.getDatabase();

        for (CBIDescriptor descriptor : headerDescriptorMap.values()) {
            if (descriptor.isPrimaryKey()) {
                String primaryValue = null;
                Object fieldValue = descriptor.getDescriptorValue(cbiObject);

                if (fieldValue == null)
                    throw new IllegalStateException("Field Primary Key not set");

                TypeAdapter<Object> adapter = descriptor.getAppropriateAdapter();
                String sqlValue = adapter.toSQL(fieldValue);

                if (descriptor.isPrimaryKey())
                    primaryValue = sqlValue;

                Timber.d("[Table: %s][Primary: %s][Value: %s]", tableName, primaryKey, primaryValue);

                int deletedRows = database.delete(
                        tableName,
                        primaryKey + " = ?",
                        new String[]{primaryValue});

                return deletedRows > 0;
            }
        }

        return false;
    }

    public boolean delete(QueryBuilder queryBuilder) {
        SQLiteDatabase database = linkedDatabase.getDatabase();

        int deletedRows = database.delete(
                tableName,
                queryBuilder.getWhereStatement(),
                queryBuilder.getSelections().toArray(new String[0]));

        return deletedRows > 0;
    }

    public boolean insertAll(@NonNull Collection<T> cbiObjectList) {
        return insertAll(cbiObjectList, Collections.emptySet());
    }

    public boolean insertAll(@NonNull Collection<T> cbiObjectList,
                             Set<String> blacklist) {
        boolean hasFailed = false;
        for (T cbiObject : cbiObjectList) {
            if (!insert(cbiObject, blacklist))
                hasFailed = true;
        }

        return !hasFailed;
    }

    public boolean insert(@NonNull T cbiObject, @NonNull Set<String> headerBlacklist) throws IllegalArgumentException {
        if (!cbiObject.getClass().equals(tableType))
            throw new IllegalArgumentException("CBIObject Type Mismatch. Expecting: " + tableType.getSimpleName());

        ContentValues content = new ContentValues();

        for (CBIDescriptor descriptor : headerDescriptorMap.values()) {
            if (headerBlacklist.contains(descriptor.getHeaderName()))
                continue;

            Object fieldValue = descriptor.getDescriptorValue(cbiObject);

            if (fieldValue == null) {
                if (descriptor.isNotNull()) {
                    Timber.w("Tried to insert null value into NonNull [Descriptor: %s]", descriptor.getHeaderName());
                    return false;
                }

                continue;
            }

            TypeAdapter<Object> adapter = descriptor.getAppropriateAdapter();
            String sqlValue = adapter.toSQL(fieldValue);
            content.put(descriptor.getHeaderName(), sqlValue);
        }

        SQLiteDatabase database = linkedDatabase.getDatabase();

        long newRow = database.insertWithOnConflict(tableName, null, content, SQLiteDatabase.CONFLICT_REPLACE);

        return newRow != -1;
    }

    public boolean insert(@NonNull T cbiObject) throws IllegalArgumentException {
        return insert(cbiObject, Collections.emptySet());
    }

    public boolean update(@NonNull T cbiObject) {
        if (!cbiObject.getClass().equals(tableType))
            throw new IllegalArgumentException("CBIObject Type Mismatch. Expecting: " + tableType.getSimpleName());

        if (super.primaryKey == null)
            throw new IllegalArgumentException("Cannot use this method without a Primary Key");


        ContentValues content = new ContentValues();
        String primaryValue = null;

        for (CBIDescriptor descriptor : headerDescriptorMap.values()) {
            Object fieldValue = descriptor.getDescriptorValue(cbiObject);

            if (fieldValue == null) {
                if (descriptor.isNotNull()) {
                    Timber.w("Tried to insert null value into NonNull [Descriptor: %s]", descriptor.getHeaderName());
                    return false;
                }

                continue;
            }

            TypeAdapter<Object> adapter = descriptor.getAppropriateAdapter();
            String sqlValue = adapter.toSQL(fieldValue);

            if (descriptor.isPrimaryKey())
                primaryValue = sqlValue;

            content.put(descriptor.getHeaderName(), sqlValue);
        }

        SQLiteDatabase database = linkedDatabase.getDatabase();
        int affectedRows = database.update(
                tableName,
                content,
                primaryKey,
                new String[]{primaryValue}
        );

        return affectedRows != 0;
    }

    public boolean updateAll(String columnName, Object newValue) {
        return updateAll(columnName, newValue, null, null);
    }

    public boolean updateAll(String columnName, Object newValue, String whereClause, String[] whereArgs) {
        CBIDescriptor descriptor = headerDescriptorMap.get(columnName);

        if (descriptor == null)
            throw new IllegalArgumentException("Unknown descriptor: " + columnName);

        if (descriptor.isNotNull() && newValue == null)
            return false;

        TypeAdapter<Object> adapter = descriptor.getAppropriateAdapter();

        ContentValues content = new ContentValues();
        String sqlValue = adapter.toSQL(newValue);
        content.put(descriptor.getHeaderName(), sqlValue);

        SQLiteDatabase database = linkedDatabase.getDatabase();
        int affectedRows = database.update(
                tableName,
                content,
                whereClause,
                whereArgs
        );

        Timber.d("Updated %s rows", affectedRows);

        return affectedRows != 0;
    }

    public T getFirst(String primaryKeyValue) {
        if (super.primaryKey == null)
            throw new IllegalArgumentException("Cannot use this method without a Primary Key");

        return getFirst(primaryKey, primaryKeyValue);
    }

    public T getFirst(String columnHeader, String columnValue) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .setTable(tableName)
                .addSelection(columnHeader, columnValue);

        return getFirst(queryBuilder);
    }

    public T getFirst(@NonNull QueryBuilder queryBuilder) {
        SQLiteDatabase database = linkedDatabase.getDatabase();
        queryBuilder.setTable(getTableName());

        Cursor cursor = queryBuilder.query(database);

        if (!cursor.moveToFirst()) {
            cursor.close();
            Timber.d("Error moving cursor to first row");
            return null;
        }

        try {

            CBIObject cbiObject = buildFromCursor(cursor, queryBuilder);
            cursor.close();

            return (T) cbiObject;
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    @NonNull
    public Collection<T> getAll() {
        return getAll(new QueryBuilder(), null);
    }

    @NonNull
    public Collection<T> getAll(@NonNull QueryBuilder queryBuilder, @Nullable Callable<T> buildCallable) {
        Timber.d("Getting all results from table %s", tableName);
        queryBuilder.setTable(tableName);

        SQLiteDatabase database = linkedDatabase.getDatabase();

        Cursor cursor = queryBuilder.query(database);
        Collection<CBIObject> cbiObjects = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            cursor.close();
            Timber.d("Error moving cursor to first row");
            return (Collection<T>) cbiObjects;
        }

        while (!cursor.isAfterLast()) {
            try {
                T cbiObject = buildFromCursor(cursor, queryBuilder);

                if (cbiObject != null) {
                    cbiObjects.add(cbiObject);

                    if (buildCallable != null) {
                        buildCallable.call(cbiObject);
                    }
                }

                cursor.moveToNext();
            } catch (Exception e) {
                Timber.e(e);
                cursor.moveToNext();
            }
        }

        cursor.close();

        Timber.d("Found %s results", cbiObjects.size());
        return (Collection<T>) cbiObjects;
    }

    @NonNull
    public Collection<T> getAll(@Nullable Callable<T> buildCallable) {
        return getAll(new QueryBuilder(), buildCallable);
    }

    @NonNull
    public Collection<T> getAll(@NonNull QueryBuilder queryBuilder) {
        return getAll(queryBuilder, null);
    }
}
