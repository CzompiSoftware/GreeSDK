package eu.czsoft.greesdk;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import eu.czsoft.greesdk.deserializers.PackDeserializer;
import eu.czsoft.greesdk.network.DeviceKeyChain;
import eu.czsoft.greesdk.packets.Packet;
import eu.czsoft.greesdk.packs.DatPack;
import eu.czsoft.greesdk.packs.Pack;

public class Utils {

    public static class Unzipped {
        public final String[] keys;
        public final Integer[] values;

        public Unzipped(String[] keys, Integer[] values) {
            this.keys = keys;
            this.values = values;
        }
    }

    public static Map<String, Integer> zip(String[] keys, Integer[] values) throws IllegalArgumentException {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Length of keys and values must match");

        Map<String, Integer> zipped = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            zipped.put(keys[i], values[i]);
        }

        return zipped;
    }

    public static Unzipped unzip(Map<String, Integer> map) {
        return new Unzipped(map.keySet().toArray(new String[0]), map.values().toArray(new Integer[0]));
    }

    public static Map<String, Integer> getValues(DatPack pack) {
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

    private static String getKey(DeviceKeyChain keyChain, Packet packet) {
        String key = Crypto.GENERIC_KEY;

        Log.i("getKey", String.format("packet.cid: %s, packet.tcid: %s", packet.cid, packet.tcid));

        if (keyChain != null) {
            if (keyChain.containsKey(packet.cid)) {
                key = keyChain.getKey(packet.cid);
            } else if (keyChain.containsKey(packet.tcid)) {
                key = keyChain.getKey(packet.tcid);
            }
        }

        return key;
    }
}