package hu.czsoft.greesdk;

import hu.czsoft.greesdk.appliances.ApplianceImpl;
import hu.czsoft.greesdk.appliances.Appliance;
import hu.czsoft.greesdk.appliances.Parameter;
import hu.czsoft.greesdk.net.DeviceKeyChain;
import hu.czsoft.greesdk.net.communication.PacketCommunicator;
import hu.czsoft.greesdk.net.packets.Packet;
import hu.czsoft.greesdk.net.packets.packs.clientbound.BindResponsePack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.DeviceResponsePack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.ResultPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.BindRequestPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.CommandRequestPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.StatusResponsePack;
import hu.czsoft.greesdk.net.packets.serverbound.ApplicationPacket;
import hu.czsoft.greesdk.net.packets.serverbound.ScanAppliancesPacket;
import hu.czsoft.greesdk.net.packets.serverbound.WiFiSettingsPacket;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class DeviceManager {
    private final int DATAGRAM_PORT = 7000;

    private static DeviceManager instance = null;

    private final HashMap<String, ApplianceImpl> devices = new HashMap<>();
    private final DeviceKeyChain keyChain = new DeviceKeyChain();
    private final ArrayList<DeviceManagerEventListener> eventListeners = new ArrayList<>();

    public static DeviceManager getInstance() {
        if (instance == null)
            instance = new DeviceManager();

        return instance;
    }

    protected DeviceManager() {
        LOGGER.info("DeviceManager successfully initialized");
    }

    public List<Appliance> getDevices() {
        try {
            return List.of(devices.values().toArray(new Appliance[0]));
        } catch (Exception ex) {
            return null;
        }
    }

    public Appliance getDevice(String deviceId) {
        if (devices.containsKey(deviceId))
            return devices.get(deviceId);

        return null;
    }

    public void registerEventListener(DeviceManagerEventListener listener) {
        if (!eventListeners.contains(listener))
            eventListeners.add(listener);
    }

    public void unregisterEventListener(DeviceManagerEventListener listener) {
        eventListeners.remove(listener);
    }

    public void setParameter(ApplianceImpl device, String name, int value) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put(name, value);

        setParameters(device, p);
    }

    public void setParameters(ApplianceImpl device, Map<String, Integer> parameters) {
        LOGGER.fine(String.format("Setting parameters of %s: %s", device.getId(), parameters));

        ApplicationPacket packet = new ApplicationPacket();
        packet.targetClientId = device.getId();
        packet.i = 0;

        CommandRequestPack pack = new CommandRequestPack();
        pack.options = parameters.keySet().toArray(new String[0]);
        pack.values = parameters.values().toArray(new Integer[0]);
        pack.mac = packet.targetClientId;

        packet.pack = pack;

        sendPackets(packet);
    }

    public void setWiFi(String ssid, String password){

        WiFiSettingsPacket packet = new WiFiSettingsPacket();
        packet.password = password;
        packet.ssid = ssid;

        sendPackets(packet);
    }

    public void discoverDevices() {
        LOGGER.info("Device discovery running...");

        final PacketCommunicator comm = new PacketCommunicator();
        comm.setCommunicationFinishedListener(() -> {
            try {
                Packet[] responses = comm.get();
                LOGGER.info(String.format("Got %d response(s)", responses.length));

                bindDevices(responses);
            } catch (Exception e) {
                LOGGER.fine("Device discovery failed with exception\n" + e);
            }
        });

        ScanAppliancesPacket sp = new ScanAppliancesPacket();

        comm.execute(new ScanAppliancesPacket[]{ sp });
    }

    public void updateDevices() {
        if (devices.isEmpty()) {
            LOGGER.info("No devices to update");
            return;
        }

        LOGGER.info(String.format("Updating %d device(s)", devices.size()));

        List<ApplicationPacket> packets = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<>();
        for (Parameter p : Parameter.values()) {
            keys.add(p.toString());
        }

        for (ApplianceImpl device : devices.values()) {
            ApplicationPacket packet = new ApplicationPacket();
            packet.targetClientId = device.getId();
            packet.i = 0;

            StatusResponsePack pack = new StatusResponsePack();
            pack.options = keys.toArray(new String[0]);
            pack.mac = device.getId();
            packet.pack = pack;

            packets.add(packet);
        }
        sendPackets(packets.toArray(new Packet[0]));
    }

    private void bindDevices(Packet[] scanResponses) throws IOException {
        ArrayList<ApplicationPacket> requests = new ArrayList<>();

        for (Packet response : scanResponses) {
            if (!(response.pack instanceof DeviceResponsePack devicePack))
                continue;

            ApplianceImpl device;

            if (!devices.containsKey(response.clientId)) {
                device = new ApplianceImpl(devicePack.mac, this);
                devices.put(devicePack.mac, device);
            } else {
                device = devices.get(response.clientId);
            }
            device.updateWithDevicePack(devicePack);

            if (!keyChain.containsKey(response.clientId)) {
                LOGGER.info("Binding device: " + devicePack.name);

                ApplicationPacket request = new ApplicationPacket();
                request.targetClientId = response.clientId;
                request.pack = new BindRequestPack();
                request.pack.mac = request.targetClientId;
                request.i = 1;

                requests.add(request);
            }
        }

        final PacketCommunicator comm = new PacketCommunicator(keyChain);
        comm.setCommunicationFinishedListener(() -> {
            try {
                Packet[] responses = comm.get();
                storeDevices(responses);
            } catch (Exception e) {
                LOGGER.fine("Device discovery failed with exception\n" + e);
            }
        });
        comm.execute(requests.toArray(new Packet[0]));
    }

    private void sendPackets(Packet... packets) {
        final PacketCommunicator comm = new PacketCommunicator(keyChain);
        comm.setCommunicationFinishedListener(() -> {
            try {
                final Packet[] responses = comm.get();

                for (Packet response : responses) {
                    if (devices.containsKey(response.clientId)) {
                        devices.get(response.clientId).updateWithResultPack((ResultPack) response.pack);
                    }
                }

                raiseEvent(DeviceManagerEvent.DEVICE_STATUS_UPDATED);
            } catch (Exception e) {
                LOGGER.severe("Failed to get response of command. Error: " + e.getMessage());
            }
        });
        comm.execute(packets);
    }

    private void storeDevices(Packet[] bindResponses) {
        for (Packet response : bindResponses) {
            if (!(response.pack instanceof BindResponsePack pack))
                continue;

            LOGGER.info("Storing key for device: " + pack.mac);
            keyChain.addKey(pack.mac, pack.deviceKey);
        }

        raiseEvent(DeviceManagerEvent.DEVICE_LIST_UPDATED);
        updateDevices();
    }

    private void raiseEvent(DeviceManagerEvent event) {
        for (DeviceManagerEventListener listener : eventListeners) {
            listener.onEvent(event);
        }
    }
}
