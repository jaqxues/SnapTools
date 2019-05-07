package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.ModulePack.Utils.FieldMapper;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
@TableName(value = "LensManifests")
public class LensAssetObject implements CBIObject {

    @PrimaryKey
    @TableField("id")
    public String id;
    @TableField("url")
    public String url;
    @TableField("signature")
    public String signature;
    @TableField("type")
    public String type;
    @TableField("load_mode")
    public String loadMode;
    @TableField("scale")
    public int scale;
    @TableField("preload_limit")
    public int preloadLimit;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {

    }

    public void buildFromAsset(FieldMapper assetMapper, Object asset) {

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("url", url)
                .add("signature", signature)
                .add("type", type)
                .add("loadMode", loadMode)
                .add("scale", scale)
                .add("preloadLimit", preloadLimit)
                .toString();
    }
}
