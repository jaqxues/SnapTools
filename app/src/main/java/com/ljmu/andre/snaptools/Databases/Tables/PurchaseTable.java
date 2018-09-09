package com.ljmu.andre.snaptools.Databases.Tables;


import com.google.common.base.MoreObjects;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName(value = "PurchaseTable", VERSION = 3)
public class PurchaseTable implements CBIObject {
    public static final int TYPE = 0;

    @PrimaryKey
    @TableField(value = "identifier")
    public String identifier;

    @TableField(value = "purchase_token")
    public String purchaseToken;

    @TableField(value = "type")
    public String type;

    @TableField(value = "pending")
    public boolean isPending;

    // Needed for the database ===================================================
    public PurchaseTable() {
    }

    public PurchaseTable(String purchaseToken, String type, String identifier) {
        this.purchaseToken = purchaseToken;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        Timber.d("Table Upgrade");
        List<SQLCommand> sqlCommands = new ArrayList<>();

        if (oldVersion < 2) {
            sqlCommands.add(
                    new SQLCommand(
                            "ALTER TABLE " + table.getTableName()
                                    + " ADD COLUMN pending Integer DEFAULT '0'"
                    )
            );
        }

        linkedDBCore.runCommands(sqlCommands);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("purchaseToken", purchaseToken)
                .add("type", type)
                .add("identifier", identifier)
                .add("pending", isPending)
                .toString();
    }
}
