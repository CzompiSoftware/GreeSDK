package eu.czsoft.greesdk.net;

import java.util.HashMap;

public class DeviceKeyChain {
    private final HashMap<String, String> keys = new HashMap<>();

    public void addKey(String deviceId, String key) {
        keys.put(deviceId.toLowerCase(), key);
    }

    public String getKey(String deviceId) {
        if (deviceId == null)
            return null;

        String id = deviceId.toLowerCase();

        if (!keys.containsKey(id))
            return null;

        return keys.get(id);
    }

    public boolean containsKey(String deviceId) {
        if (deviceId == null)
            return false;

        return keys.containsKey(deviceId.toLowerCase());
    }
}
