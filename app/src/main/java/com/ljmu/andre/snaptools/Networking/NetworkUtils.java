package com.ljmu.andre.snaptools.Networking;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ljmu.andre.snaptools.Exceptions.KeyNotFoundException;
import com.ljmu.andre.snaptools.Exceptions.NoJSONObjectException;
import com.ljmu.andre.snaptools.Networking.Packets.Packet;
import com.ljmu.andre.snaptools.Utils.Assert;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * This file was created by Jacques (jaqxues) in the Project SnapTools.<br>
 * Date: 12.09.2018 - Time 19:51.
 */

public class NetworkUtils {

    /**
     * This method extracts a child JSONObject from a parent JSON. It is used for things like PackHistory or
     * KnownBugs. In such things, the packs version, flavour and other factors play a role. However, I do not want
     * to make a JSON File for every specific pack. This is in fact a replacement for the PHP server,
     * which, based on the Parameters it received, build a JSON that could be used for that specific request.
     *
     * @param json The JSON source
     * @param keys The keys used to get the child object to extract. Multiple keys are allowed, from parent to child
     * @return The extracted part of the Json
     * @throws KeyNotFoundException  If one Key in <code>keys</code> is not found.
     * @throws NoJSONObjectException If the specified JsonElement from the key is not a JSONObject
     */
    public static String extractFromJson(String json, String... keys) throws KeyNotFoundException, NoJSONObjectException {
        JsonObject object;
        try {
            JsonElement element = new JsonParser().parse(json);
            object = element.getAsJsonObject();
        } catch (Exception e) {
            Timber.e(e, "Could not parse JSONObject \"%s\"", json);
            throw e;
        }
        for (String key : keys) {
            if (!object.has(key)) {
                KeyNotFoundException e = new KeyNotFoundException("Key \"" + key + "\" not found");
                Timber.e(e, "Key \"%s\" not found in JSON. Current JSONObject: \"%s\"", key, object);
                throw e;
            }
            if (!object.get(key).isJsonObject()) {
                NoJSONObjectException e = new NoJSONObjectException("Target is not a JSONObject");
                Timber.e(e, "This method exclusively handles JSONObject children.");
                throw e;
            }
            object = object.getAsJsonObject(key);
        }

        return object.toString();
    }

    /**
     * This method uses the Gson library in order to de-serialize the specified Packet.
     *
     * @param json        The JSONObject to parse the Packet
     * @param packetClass The type of the Packet that should be returned
     * @param <T>         Type of Packet
     * @return The packet built with Gson from the <code>json</code> input.
     */
    public static <T extends Packet> T parsePacket(String json, Class<T> packetClass) {
        Assert.notNull("Null PacketClass", packetClass);
        T packet = new Gson().fromJson(json, packetClass);

        if (packet == null) {
            Timber.w("No Response Packet");
            return null;
        }

        Timber.d("Response Packet: %s", packet.toString());
        return packet;
    }

    /**
     * Combines {@link this#extractFromJson(String, String...)} and {@link this#parsePacket(String, Class)}.
     *
     * @param json        The source String from which {@link this#extractFromJson(String, String...)} is called
     * @param packetClass The Class of the Packet which should be parsed.
     * @param keys        The keys that should be used to get to the correct JSONObject in the JSON File.
     * @param <T>         The type of the Packet that should be returned
     * @return The parsed packet from the extracted JSON
     * @throws KeyNotFoundException Throws an exception if the given <code>keys</code> have not been found
     */
    @NotNull
    public static <T extends Packet> T extractPack(String json, Class<T> packetClass, String... keys) throws KeyNotFoundException {
        try {
            return parsePacket(extractFromJson(json, keys), packetClass);
        } catch (NoJSONObjectException e) {
            Timber.e(e, "A key did not point to a JSONObject");
            return null;
        }
    }
}
