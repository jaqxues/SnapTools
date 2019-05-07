package com.ljmu.andre.snaptools.ModulePack.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.KnownBugObject;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;

import java.util.List;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class KnownBugsPacket extends Packet {
    @SerializedName("bugs")
    public List<KnownBugObject> bugs;

    public void assignChildrensKeys(String scVersion, String packVersion) {
        if (bugs == null) {
            Timber.w("Couldn't assign keys to null bug list");
            return;
        }

        for (KnownBugObject bugObject : bugs)
            bugObject.key = KnownBugObject.createKey(scVersion, packVersion);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("error", error)
                .add("errorCode", getErrorCode())
                .add("bugs", bugs)
                .toString();
    }
}
