package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ServerPacksPacket extends AuthResultPacket {
    @SerializedName("packs")
    private List<ServerPackMetaData> packs;

    public List<ServerPackMetaData> getPacks() {
        return packs;
    }
}
