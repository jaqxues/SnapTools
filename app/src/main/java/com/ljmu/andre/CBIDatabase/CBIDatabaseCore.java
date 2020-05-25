package com.ljmu.andre.CBIDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;
import com.ljmu.andre.CBIDatabase.Utils.TableVersionTable;
import com.ljmu.andre.snaptools.Utils.PathProvider;
import timber.log.Timber;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

/**
 * CLASS BASED INSERTION DATABASE
 */
public class CBIDatabaseCore extends SQLiteOpenHelper {
    private Map<Class<? extends CBIObject>, CBITable<? extends CBIObject>> classTableMap = new ConcurrentHashMap<>();
    private SQLiteDatabase writableDatabase;
    private CBITable<TableVersionTable> backedTableVersionsTable;

    public CBIDatabaseCore(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void runCommands(Collection<SQLCommand> sqlCommands) {
        for (SQLCommand sqlCommand : sqlCommands)
            runCommand(sqlCommand);
    }

    public void runCommand(SQLCommand sqlCommand) {
        Timber.d("Running command: \n" + sqlCommand.toString());

        switch (sqlCommand.getCommandAction()) {
            case QUERY:
                if (sqlCommand.getQuery() == null)
                    break;

                getDatabase().execSQL(sqlCommand.getQuery());
                break;

            case RECYCLE:
                writableDatabase.close();
                writableDatabase = null;
                createDbIfNotExisting();
                break;
            default:
                throw new IllegalArgumentException("Unknown command action: " + sqlCommand.getCommandAction());
        }
    }

    public SQLiteDatabase getDatabase() {
        createDbIfNotExisting();
        return writableDatabase;
    }

    private void createDbIfNotExisting() {
        if (writableDatabase == null || !writableDatabase.isOpen())
            writableDatabase = this.getWritableDatabase();
    }

    public <T extends CBIObject> CBITable<T> registerTable(Class<T> cbiObjectClass) {
        return registerTable(cbiObjectClass, false);
    }

    public <T extends CBIObject> CBITable<T> registerTable(Class<T> cbiObjectClass, boolean recursionLock) {
        if (classTableMap.containsKey(cbiObjectClass)) {
            Timber.d("Database already contains Table for: " + cbiObjectClass.getCanonicalName());
            return (CBITable<T>) classTableMap.get(cbiObjectClass);
        }

        CBITable<T> table = new CBITable<>(cbiObjectClass, this);

        if (!recursionLock)
            manageTableVersioning(table);

        table.createTable();

        classTableMap.put(cbiObjectClass, table);
        return table;
    }

    public void unregisterTable(Class<? extends CBIObject> cbiObjectClass) {
        classTableMap.remove(cbiObjectClass);
    }

    private <T extends CBIObject> void manageTableVersioning(CBITable<T> cbiTable) {
        if (backedTableVersionsTable == null) {
            backedTableVersionsTable = registerTable(TableVersionTable.class, true);

            // Block endless TableVersionTable loops =====================================
            if (backedTableVersionsTable == null)
                throw new IllegalStateException("Blocked potentially endless TableVersionTable creation");
        }

        String name = cbiTable.getTableName();
        int version = cbiTable.getTableVersion();

        TableVersionTable tableData = backedTableVersionsTable.getFirst(name);

        if (tableData == null) {
            Timber.d("Table version not found in backed table");

            if (backedTableVersionsTable.insert(new TableVersionTable(name, version)))
                Timber.d("Successfully stored new table version: [Name: %s][Version: %s]", name, version);
            else
                Timber.w("Failed to insert new backed table version info for %s", name);

            return;
        }

        // If the input table has a higher version, invoke the upgrade method ========
        if (version > tableData.tableVersion) {
            Timber.d("Upgrading table %s to newest version %s", name, version);

            try {
                CBIObject cbiObject = cbiTable.tableType.newInstance();
                cbiObject.onTableUpgrade(this, cbiTable, tableData.tableVersion, version);

                if (backedTableVersionsTable.insert(new TableVersionTable(name, version)))
                    Timber.d("Successfully stored new table version: [Name: %s][Version: %s]", name, version);
                else
                    Timber.w("Failed to insert new backed table version info for %s", name);

            } catch (Exception e) {
                Timber.e(e, "Failed to create table instance to invoke upgrade");
            }

            return;
        }

        Timber.d("Table has no new version upgrade");
    }

    public <T extends CBIObject> CBITable<T> getTable(Class<T> cbiObjectClass) {
        return (CBITable<T>) classTableMap.get(cbiObjectClass);
    }

    private static String getDBPath(String name) {
        return PathProvider.getDatabasesPath() + name + ".db";
    }
}
