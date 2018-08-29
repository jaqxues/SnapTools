package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName("ActiveFilters")
public class FilterObject implements CBIObject {
    @PrimaryKey
    @TableField("file_name")
    private String fileName;

    @TableField("is_active")
    private boolean isActive;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {

    }

    public String getFileName() {
        return fileName;
    }

    public FilterObject setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public FilterObject setActive(boolean active) {
        isActive = active;
        return this;
    }

    public FilterObject toggleActive() {
        isActive = !isActive;
        return this;
    }

}
