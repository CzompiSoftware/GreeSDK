package eu.czsoft.greesdk;

import eu.czsoft.greesdk.appliances.ApplianceImpl;
import eu.czsoft.greesdk.appliances.Appliance;
import eu.czsoft.greesdk.appliances.Parameter;
import eu.czsoft.greesdk.net.DeviceKeyChain;
import eu.czsoft.greesdk.net.communication.AsyncCommunicationFinishedListener;
import eu.czsoft.greesdk.net.communication.AsyncCommunicator;
import eu.czsoft.greesdk.net.packets.Packet;
import eu.czsoft.greesdk.net.packets.packs.clientbound.BindResponsePack;
import eu.czsoft.greesdk.net.packets.packs.clientbound.DeviceResponsePack;
import eu.czsoft.greesdk.net.packets.packs.clientbound.ResultPack;
import eu.czsoft.greesdk.net.packets.packs.serverbound.BindRequestPack;
import eu.czsoft.greesdk.net.packets.packs.serverbound.ChangeOptionRequestPack;
import eu.czsoft.greesdk.net.packets.packs.serverbound.CommandRequestPack;
import eu.czsoft.greesdk.net.packets.packs.serverbound.StatusResponsePack;
import eu.czsoft.greesdk.net.packets.serverbound.ApplicationPacket;
import eu.czsoft.greesdk.net.packets.serverbound.ScanAppliancesPacket;
import eu.czsoft.greesdk.net.packets.serverbound.WifiSettingsPacket;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
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
        LOGGER.info("Created");
    }

    public Appliance[] getDevices() {
        return devices.values().toArray(new Appliance[0]);
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
        LOGGER.debug(String.format("Setting parameters of %s: %s", device.getId(), parameters));

        ApplicationPacket packet = new ApplicationPacket();
        packet.targetClientId = device.getId();
        packet.i = 0;

        CommandRequestPack pack = new CommandRequestPack();
        pack.options = parameters.keySet().toArray(new String[0]);
        pack.values = parameters.values().toArray(new Integer[0]);
        pack.mac = packet.targetClientId;

        packet.pack = pack;

        final AsyncCommunicator comm = new AsyncCommunicator(keyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    final Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (devices.containsKey(response.clientId)) {
                            devices.get(response.clientId).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEvent.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    LOGGER.error("Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }

    public void setWiFi(String ssid, String password){

        WifiSettingsPacket packet = new WifiSettingsPacket();
        packet.password = password;
        packet.ssid = ssid;

        final AsyncCommunicator comm = new AsyncCommunicator(keyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    final Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (devices.containsKey(response.clientId)) {
                            devices.get(response.clientId).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEvent.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    LOGGER.error("Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }

    public void discoverDevices() {
        LOGGER.info("Device discovery running...");

        final AsyncCommunicator comm = new AsyncCommunicator();
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    Packet[] responses = comm.get();
                    LOGGER.info(String.format("Got %d response(s)", responses.length));

                    bindDevices(responses);
                } catch (Exception e) {
                    LOGGER.debug("Device discovery failed with exception\n" + e);
                }
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

        ArrayList<ApplicationPacket> packets = new ArrayList<>();

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

        final AsyncCommunicator comm = new AsyncCommunicator(keyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (devices.containsKey(response.clientId)) {
                            devices.get(response.clientId).updateWithDatPack((ChangeOptionRequestPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEvent.DEVICE_STATUS_UPDATED);

                } catch (Exception e) {
                    LOGGER.warn("Failed to get device update result. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(packets.toArray(new Packet[0]));
    }

    private void bindDevices(Packet[] scanResponses) throws IOException {
        ArrayList<ApplicationPacket> requests = new ArrayList<>();

        for (int i = 0; i < scanResponses.length; i++) {
            Packet response = scanResponses[i];

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

        final AsyncCommunicator comm = new AsyncCommunicator(keyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    Packet[] responses = comm.get();
                    storeDevices(responses);
                } catch (Exception e) {
                    LOGGER.debug("Device discovery failed with exception\n" + e);
                }
            }
        });
        comm.execute(requests.toArray(new Packet[0]));
    }

    private void storeDevices(Packet[] bindResponses) {
        for (Packet response : bindResponses) {
            if (!(response.pack instanceof BindResponsePack pack))
                continue;

            LOGGER.info("Storing key for device: " + pack.mac);
            keyChain.addKey(pack.mac, pack.deviceKey);
        }

        sendEvent(DeviceManagerEvent.DEVICE_LIST_UPDATED);
        updateDevices();
    }

    private void sendEvent(DeviceManagerEvent event) {
        for (DeviceManagerEventListener listener : eventListeners)
            listener.onEvent(event);
    }
}
