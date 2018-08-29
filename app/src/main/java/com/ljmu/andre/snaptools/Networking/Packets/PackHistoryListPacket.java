package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackHistoryListPacket extends AuthResultPacket {
    @SerializedName("packs")
    private List<PackHistoryObject> packHistories;

    public List<PackHistoryObject> getPackHistories() {
        return packHistories;
    }
}
