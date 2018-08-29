package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TrialPacket extends AuthResultPacket {
    @SerializedName("trial_mode")
    public int trialMode;

    @SerializedName("trial_active_time")
    private long trialActiveTime;

    public Long getActiveTimestamp() {
        return trialActiveTime * 1000L;
    }
}
