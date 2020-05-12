package com.ljmu.andre.snaptools.Networking.Packets;

import androidx.annotation.NonNull;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName("PackHistory")
public class PackHistoryObject implements Comparable<PackHistoryObject> {
    @SerializedName("sc_version")
    public String scVersion;

    @SerializedName("mod_version")
    public String packVersion;

    @SerializedName("pack_type")
    public String packType;

    @SerializedName("development")
    public boolean development;

    @SerializedName("flavour")
    public String flavour;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("scVersion", scVersion)
                .add("packVersion", packVersion)
                .add("packType", packType)
                .add("development", development)
                .add("", super.toString())
                .toString();
    }

    public String getName() {
        return PackMetaData.getFileNameFromTemplate(
                packType,
                scVersion,
                flavour,
                packVersion
        );
    }

    @Override
    public int compareTo(@NonNull PackHistoryObject o) {
        return packVersion == null || o.packVersion == null ? 0 : MiscUtils.versionCompare(o.packVersion, packVersion);
    }
}
