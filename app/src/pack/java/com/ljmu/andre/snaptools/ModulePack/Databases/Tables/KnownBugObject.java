package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName("KnownBugs")
public class KnownBugObject implements CBIObject {
    @SerializedName("category")
    @PrimaryKey
    @TableField("category")
    public String category;

    @TableField("key")
    public String key;

    @TableField("bugs")
    @SerializedName("bugs")
    public List<String> bugs;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("category", category)
                .add("key", key)
                .add("bugs", bugs)
                .toString();
    }

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {

    }

    public static String createKey(String scVersion, String packVersion) {
        return "S" + scVersion + "-P" + packVersion;
    }
}
