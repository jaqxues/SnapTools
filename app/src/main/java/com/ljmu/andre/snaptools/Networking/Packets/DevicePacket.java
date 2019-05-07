package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DevicePacket extends Packet {
    @SerializedName("device_name")
    public String device_name;

    @SerializedName("device_id")
    public String device_id;

    @SerializedName("banned")
    public boolean banned;

    @SerializedName("ban_reason")
    private String ban_reason;
}
