package com.ljmu.andre.CBIDatabase.Utils;

import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName(value = "TableVersionTable")
public class TableVersionTable implements CBIObject {
    @PrimaryKey
    @TableField(value = "TableName")
    public String tableName;

    @TableField(value = "TableVersion", SQL_DEFAULT = "'1'")
    public int tableVersion;

    public TableVersionTable() {
    }

    public TableVersionTable(String tableName, int tableVersion) {
        this.tableName = tableName;
        this.tableVersion = tableVersion;
    }

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        Timber.d("Table Upgrade");
    }
}
