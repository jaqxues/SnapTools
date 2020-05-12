package com.ljmu.andre.snaptools.ModulePack.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.graphics.Point;
import androidx.core.util.Pair;

import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensAssetObject;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensObject;
import com.ljmu.andre.snaptools.ModulePack.Utils.InterruptFlag;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.LENS_MERGE_ENABLE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DATABASES_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LensDatabase {
    public static final String DB_NAME = "Lenses.db";
    private static final int VERSION = 1;
    private static CBIDatabaseCore databaseCore;

    public static CBIDatabaseCore init(Context context) {
        if (databaseCore == null) {
            String dbPath = getPref(DATABASES_PATH);
            databaseCore = new CBIDatabaseCore(context, dbPath + DB_NAME, VERSION);
        }

        return databaseCore;
    }

    public static Result<Boolean, Point> mergeLensDatabases(Context context, List<String> sourceFiles,
                                                            InterruptFlag interruptFlag) {
//		CBITable<LensObject> lensTable = LensDatabase.getTable(LensObject.class);

//		long startSize = lensTable.getRowCount();

        Point successDuplicatePoint = new Point(0, 0);
        Result<Boolean, Point> totalMergeResult = new Result<>(true, successDuplicatePoint);

        for (String lensPath : sourceFiles) {
            if (interruptFlag.shouldInterrupt()) {
                return totalMergeResult.setKey(true);
            }

            CBIDatabaseCore mergeSource = LensDatabase.getLensDatabase(context, lensPath);
            Result<Boolean, Pair<Integer, Integer>> mergeResult = mergeLensDatabases(mergeSource, interruptFlag);

            if (!mergeResult.getKey())
                totalMergeResult.setKey(false);

            Pair<Integer, Integer> currentSuccessDuplicatePair = mergeResult.getValue();
            successDuplicatePoint.offset(
                    currentSuccessDuplicatePair.first,
                    currentSuccessDuplicatePair.second
            );

            mergeSource.getDatabase().close();
        }

//		long endSize = lensTable.getRowCount();

        return totalMergeResult;
    }

    public static CBIDatabaseCore getLensDatabase(Context context, String dbLocation) {
        return new CBIDatabaseCore(context, dbLocation, VERSION);
    }

    public static Result<Boolean, Pair<Integer, Integer>> mergeLensDatabases(CBIDatabaseCore source,
                                                                             InterruptFlag interruptFlag) {
        int successfulMerges = 0;
        int duplicatesRemoved = 0;

        try {
            CBITable<LensObject> currentLensTable = LensDatabase.getTable(LensObject.class);
            CBITable<LensObject> newTable = new CBITable<>(LensObject.class, source);
            newTable.createTable();

            boolean wasSuccessful = true;

            Set<String> headerBlacklist = new HashSet<>();
            headerBlacklist.add("isActive");
            headerBlacklist.add("favourited");

            for (LensObject lensObject : newTable.getAll()) {
                if (interruptFlag.shouldInterrupt()) {
                    return new Result<>(false, Pair.create(successfulMerges, duplicatesRemoved));
                }

                if (lensObject.id == null || lensObject.code == null || lensObject.mLensLink == null) {
                    continue;
                }

                long duplicateLensLinks = currentLensTable.getRowCount("mLensLink = ?", new String[]{lensObject.mLensLink});
                if (duplicateLensLinks > 0) {

                    if (duplicateLensLinks > 1) {
                        duplicatesRemoved += duplicateLensLinks - 1;
                        currentLensTable.delete(
                                new QueryBuilder()
                                        .addSelection("mLensLink", lensObject.mLensLink)
                                        .setLimit("" + (duplicateLensLinks - 1))
                        );
                    }

                    continue;
                }

                if (currentLensTable.contains(lensObject.id))
                    continue;

                if (getPref(LENS_MERGE_ENABLE))
                    lensObject.isActive = true;

                if (!currentLensTable.insert(lensObject, headerBlacklist))
                    wasSuccessful = false;
                else
                    successfulMerges++;
            }

            Timber.d("Lens merge result [Success: %s][Purges: %s]", successfulMerges, duplicatesRemoved);

            CBITable<LensAssetObject> currentLensAssetTable = LensDatabase.getTable(LensAssetObject.class);
            CBITable<LensAssetObject> newAssetTable = new CBITable<>(LensAssetObject.class, source);
            newAssetTable.createTable();

            currentLensAssetTable.insertAll(newAssetTable.getAll());
            return new Result<>(wasSuccessful, Pair.create(successfulMerges, duplicatesRemoved));
        } catch (SQLiteDiskIOException e) {
            Timber.w(e, "Disk IO Exception");
        } catch (SQLiteException e) {
            Timber.w(e, "SQLiteException");
        }

        return new Result<>(false, Pair.create(successfulMerges, duplicatesRemoved));
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

    public static boolean insert(CBIObject object) {
        try {
            CBITable table = getTable(object.getClass());
            return table.insert(object);
        } catch (Throwable t) {
            Timber.e(t);
        }

        return false;
    }
}
