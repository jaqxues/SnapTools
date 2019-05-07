package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AuthResultPacket extends Packet {
    @SerializedName("auth_status")
    public boolean auth_status;

    // Public to make it harder to modify result =================================
    @SerializedName("banned")
    public boolean banned;

    @SerializedName("auth_description")
    public String auth_description;

    @SerializedName("ban_reason")
    public String ban_reason;

    public String getAuthDescription() {
        return auth_description;
    }

    public String getBanReason() {
        return ban_reason;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("auth_status", auth_status)
                .add("auth_description", auth_description)
                .add("banned", banned)
                .add("ban_reason", ban_reason)
                .add("", super.toString())
                .toString();
    }
}
