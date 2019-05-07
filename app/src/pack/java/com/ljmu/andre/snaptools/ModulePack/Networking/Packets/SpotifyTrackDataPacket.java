package com.ljmu.andre.snaptools.ModulePack.Networking.Packets;

import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SpotifyTrackDataPacket extends Packet {
    @SerializedName("thumbnail_url")
    public String thumbnailUrl;
}
