package hu.czsoft.greesdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.czsoft.greesdk.net.DeviceKeyChain;
import hu.czsoft.greesdk.net.packets.Packet;
import hu.czsoft.greesdk.net.packets.packs.Pack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.ChangeOptionRequestPack;
import hu.czsoft.greesdk.serialization.PackDeserializer;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

@Log
public class Utils {

    public static Map<String, Integer> zip(String[] keys, Integer[] values) throws IllegalArgumentException {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Length of keys and values must match");

        Map<String, Integer> zipped = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            zipped.put(keys[i], values[i]);
        }

        return zipped;
    }

    protected static Unzipped unzip(Map<String, Integer> map) {
        return new Unzipped(map.keySet().toArray(new String[0]), map.values().toArray(new Integer[0]));
    }

    protected static Map<String, Integer> getValues(ChangeOptionRequestPack pack) {
        return zip(pack.keys, pack.values);
    }

    public static String serializePacket(Packet packet, DeviceKeyChain deviceKeyChain) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        if (packet.pack != null) {
            String key = getKey(deviceKeyChain, packet);
            String plainPack = gson.toJson(packet.pack);
            packet.encryptedPack = Crypto.encrypt(plainPack, key);
        }

        return gson.toJson(packet);
    }

    public static Packet deserializePacket(String jsonString, DeviceKeyChain deviceKeyChain) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Pack.class, new PackDeserializer());

        Gson gson = gsonBuilder.create();

        Packet packet = gson.fromJson(jsonString, Packet.class);

        if (packet.encryptedPack != null) {
            String key = getKey(deviceKeyChain, packet);
            String plainPack = Crypto.decrypt(packet.encryptedPack, key);
            packet.pack = gson.fromJson(plainPack, Pack.class);
        }

        return packet;
    }

    protected static String getKey(DeviceKeyChain keyChain, Packet packet) {
        String key = Crypto.GENERIC_KEY;

        LOGGER.info(String.format("packet.cid: %s, packet.tcid: %s", packet.clientId, packet.targetClientId));

        if (keyChain != null) {
            if (keyChain.containsKey(packet.clientId)) {
                key = keyChain.getKey(packet.clientId);
            } else if (keyChain.containsKey(packet.targetClientId)) {
                key = keyChain.getKey(packet.targetClientId);
            }
        }

        return key;
    }
}