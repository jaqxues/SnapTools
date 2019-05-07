package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LatestPackPacket extends Packet {
    @SerializedName("latest_pack")
    private ServerPackMetaData latestPack;

    public ServerPackMetaData getLatestPack() {
        return latestPack;
    }

    public LatestPackPacket setLatestPack(ServerPackMetaData latestPack) {
        this.latestPack = latestPack;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("latestPack", latestPack)
                .add("", super.toString())
                .toString();
    }
}
