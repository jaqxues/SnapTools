package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LoginPacket extends AuthResultPacket {
    @SerializedName("device_cap")
    public boolean device_cap;
    @SerializedName("developer")
    public boolean developer;
    public String googleToken;
    private String email;
    private String displayName;
    @SerializedName("token")
    private String token;
    @SerializedName("devices")
    private DevicePacket[] devices;

    public String getEmail() {
        return email;
    }

    public LoginPacket setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LoginPacket setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginPacket setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("error", error)
                .add("auth_status", auth_status)
                .add("device_cap", device_cap)
                .add("error_msg", errorMsg)
                .add("developer", developer)
                .add("googleToken", googleToken)
                .add("email", email)
                .add("banned", banned)
                .add("displayName", displayName)
                .add("token", token)
                .add("auth_description", auth_description)
                .add("devices", devices)
                .add("ban_reason", ban_reason)
                .toString();
    }

    public DevicePacket[] getDevices() {
        return devices;
    }
}
