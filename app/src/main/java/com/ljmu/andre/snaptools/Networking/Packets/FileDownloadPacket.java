package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FileDownloadPacket extends AuthResultPacket {
    @SerializedName("pack_bytes")
    private byte[] pack_bytes;

    public byte[] getPackBytes() {
        return pack_bytes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("pack_bytes", pack_bytes)
                .add("", super.toString())
                .toString();
    }
}
