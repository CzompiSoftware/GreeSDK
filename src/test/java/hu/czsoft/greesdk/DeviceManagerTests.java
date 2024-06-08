package hu.czsoft.greesdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeviceManagerTests {

    @Test
    public void discoverDevices() {
        DeviceManager dm = new DeviceManager();
        dm.registerEventListener(event -> {
            var devices = dm.getDevices();
            Assertions.assertNotEquals(null, devices);

        });
        dm.discoverDevices();
    }

    @Test
    public void getDevice() {
        DeviceManager dm = new DeviceManager();
        dm.registerEventListener(event -> {
            var devices = dm.getDevices();
            var device = dm.getDevice(devices.get(0).getId());
            Assertions.assertNotEquals(null, device);
        });
        dm.discoverDevices();
    }
}
