package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class Packet {
    @SerializedName("error")
    public boolean error;

    @SerializedName("error_msg")
    String errorMsg;

    @SerializedName("error_code")
    private int errorCode;

    public Packet setError(boolean error) {
        this.error = error;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

    public Packet setErrorMessage(String error_msg) {
        this.errorMsg = error_msg;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("error", error)
                .add("errorCode", errorCode)
                .add("errorMsg", errorMsg)
                .toString();
    }
}
