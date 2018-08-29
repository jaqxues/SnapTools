package com.ljmu.andre.snaptools.Databases.Tables;

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

@TableName("CompletedTutorials")
public class CompletedTutorialObject implements CBIObject {

    @PrimaryKey
    @TableField("fragment_name")
    public String fragmentName;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {

    }

    public String getFragmentName() {
        return fragmentName;
    }

    public CompletedTutorialObject setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
        return this;
    }
}
