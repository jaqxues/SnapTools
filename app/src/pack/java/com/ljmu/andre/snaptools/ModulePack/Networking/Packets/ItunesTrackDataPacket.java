package com.ljmu.andre.snaptools.ModulePack.Networking.Packets;

import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ItunesTrackDataPacket extends Packet {
    private static final int ARTWORK_RES = 256;

    @SerializedName("results")
    private List<ItunesTrackArtData> results;

    @Nullable
    public String getArtworkUrl() {
        if (results == null || results.isEmpty())
            return null;

        ItunesTrackArtData trackArtData = results.get(0);

        if (trackArtData == null)
            return null;

        String unprocessedUrl = trackArtData.artworkUrl100;


        return unprocessedUrl.replace("100x100", ARTWORK_RES + "x" + ARTWORK_RES);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("results", results)
                .toString();
    }

    public static class ItunesTrackArtData {
        @SerializedName("artworkUrl100")
        private String artworkUrl100;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("artworkUrl100", artworkUrl100)
                    .toString();
        }
    }
}
