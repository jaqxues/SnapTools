package com.ljmu.andre.snaptools.ModulePack.Databases;

import android.content.Context;

import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DATABASES_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FiltersDatabase {
    private static final String DB_NAME = "FilterStates.db";
    private static final int VERSION = 1;
    private static CBIDatabaseCore databaseCore;

    public static CBIDatabaseCore init(Context context) {
        if (databaseCore == null) {
            String dbPath = getPref(DATABASES_PATH);
            databaseCore = new CBIDatabaseCore(context, dbPath + DB_NAME, VERSION);
        }

        return databaseCore;
    }

    public static <T extends CBIObject> CBITable<T> getTable(Class<T> cbiClass) {
        CBITable<T> table = databaseCore.getTable(cbiClass);

        if (table == null) {
            Timber.d("Table Didn't Exist");
            table = databaseCore.registerTable(cbiClass);
        } else
            Timber.d("Table existed!");

        return table;
    }
}
