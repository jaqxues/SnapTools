package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackDataPacket extends AuthResultPacket {
    @SerializedName("development")
    public boolean development;
    @SerializedName("mod_version")
    private String mod_version;
    @SerializedName("sc_version")
    private String sc_version;
    @SerializedName("pack_type")
    private String pack_type;
    @SerializedName("changelog")
    private String changelog;
    @SerializedName("flavour")
    private String flavour;

    private String currentModVersion = "Unknown";
    @SerializedName("pack_name")
    private String packName;

    public boolean isDevelopment() {
        return development;
    }

    public PackDataPacket setDevelopment(boolean development) {
        this.development = development;
        return this;
    }

    public String getModVersion() {
        return mod_version;
    }

    public PackDataPacket setModVersion(String mod_version) {
        this.mod_version = mod_version;
        return this;
    }

    public PackDataPacket setScVersion(String sc_version) {
        this.sc_version = sc_version;
        return this;
    }

    public String getSCVersion() {
        return sc_version;
    }

    public String getPackType() {
        return pack_type;
    }

    public PackDataPacket setPackType(String pack_type) {
        this.pack_type = pack_type;
        return this;
    }

    public String getChangelog() {
        return changelog;
    }

    public String getCurrentModVersion() {
        return currentModVersion;
    }

    public PackDataPacket setCurrentModVersion(String currentModVersion) {
        this.currentModVersion = currentModVersion;
        return this;
    }

    public String getPackName() {
        return packName == null ?
                PackMetaData.getFileNameFromTemplate(
                        getPackType(),
                        getSCVersion(),
                        getFlavour(),
                        getModVersion())
                : packName;
    }

    public PackDataPacket setPackName(String packName) {
        this.packName = packName;
        return this;
    }

    public String getFlavour() {
        return flavour;
    }

    public PackDataPacket setFlavour(String flavour) {
        this.flavour = flavour;
        return this;
    }
}
