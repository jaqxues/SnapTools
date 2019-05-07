package com.ljmu.andre.snaptools.Databases.Tables;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
@TableName(value = "ServerPacks", VERSION = 7)
public class ServerPackObject implements CBIObject {
    @TableField(value = "Name")
    @PrimaryKey
    public String name;

    @TableField(value = "Type")
    public String type;

    @TableField(value = "ScVersion")
    public String scVersion;

    @TableField(value = "PackVersion")
    public String packVersion;

    @TableField(value = "Development")
    public boolean development;

    @TableField(value = "Latest")
    public boolean latest;

    @TableField(value = "MinApkCode")
    public int minApkCode;

    @TableField(value = "MinApkName")
    public String minApkName;

    @TableField("IsBeta")
    public boolean isBeta;

    @TableField(value = "Flavour", SQL_DEFAULT = "prod")
    public String flavour;

    @TableField(value = "Description")
    public String description;

    @TableField(value = "IsPurchased")
    public Boolean isPurchased;


    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        Timber.d("Table Upgrade");
        List<SQLCommand> sqlCommands = new ArrayList<>();

        if (oldVersion < 5) {
            table.dropTable();
        }

        if (oldVersion < 6) {
            if (!table.columnExists("Description")) {
                sqlCommands.add(
                        new SQLCommand(
                                "ALTER TABLE " + table.getTableName()
                                        + " ADD COLUMN Description String DEFAULT null"
                        )
                );
            }
        }

        if (oldVersion < 7) {
            if (!table.columnExists("IsPurchased")) {
                sqlCommands.add(
                        new SQLCommand(
                                "ALTER TABLE " + table.getTableName()
                                        + " ADD COLUMN IsPurchased String DEFAULT null"
                        )
                );
            }
        }

        linkedDBCore.runCommands(sqlCommands);
    }

    public PackMetaData bindMetaData(ServerPackMetaData packMetaData) {
        packMetaData.setName(name);
        packMetaData.setType(type);
        packMetaData.setScVersion(scVersion);
        packMetaData.setPackVersion(packVersion);
        packMetaData.setDevelopment(development);
        packMetaData.setLatest(latest);
        packMetaData.setMinApkCode(minApkCode);
        packMetaData.setMinApkName(minApkName);
        packMetaData.setFlavour(flavour);
        packMetaData.setDescription(description);
        packMetaData.setPurchased(isPurchased);

        return packMetaData;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("type", type)
                .add("scVersion", scVersion)
                .add("packVersion", packVersion)
                .add("development", development)
                .add("flavour", flavour)
                .add("description", description)
                .add("isPurchased", isPurchased)
                .toString();
    }

    public static ServerPackObject fromPackMetaData(PackMetaData packMetaData) {
        ServerPackObject table = new ServerPackObject();
        table.name = packMetaData.getName();
        table.type = packMetaData.getType();
        table.scVersion = packMetaData.getScVersion();
        table.packVersion = packMetaData.getPackVersion();
        table.development = packMetaData.isDeveloper();
        table.latest = packMetaData.isLatest();
        table.flavour = packMetaData.getFlavour();

        if (packMetaData instanceof ServerPackMetaData) {
            ServerPackMetaData serverPackMetaData = (ServerPackMetaData) packMetaData;
            table.minApkCode = serverPackMetaData.getMinApkCode();
            table.minApkName = serverPackMetaData.getMinApkName();
            table.description = serverPackMetaData.getDescription();
            table.isPurchased = serverPackMetaData.isPurchased();
        }

        return table;
    }
}
