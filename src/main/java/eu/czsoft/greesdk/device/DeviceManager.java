package eu.czsoft.greesdk.device;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.czsoft.greesdk.network.AsyncCommunicationFinishedListener;
import eu.czsoft.greesdk.network.AsyncCommunicator;
import eu.czsoft.greesdk.network.DeviceKeyChain;
import eu.czsoft.greesdk.packets.AppPacket;
import eu.czsoft.greesdk.packets.Packet;
import eu.czsoft.greesdk.packets.ScanPacket;
import eu.czsoft.greesdk.packets.WifiSettingsPacket;
import eu.czsoft.greesdk.packs.BindOkPack;
import eu.czsoft.greesdk.packs.BindPack;
import eu.czsoft.greesdk.packs.CommandPack;
import eu.czsoft.greesdk.packs.DatPack;
import eu.czsoft.greesdk.packs.DevicePack;
import eu.czsoft.greesdk.packs.ResultPack;
import eu.czsoft.greesdk.packs.StatusPack;

public class DeviceManager {
    private final String LOG_TAG = "DeviceManager";
    private final int DATAGRAM_PORT = 7000;

    private static DeviceManager instance = null;

    private final HashMap<String, DeviceImpl> devices = new HashMap<>();
    private final DeviceKeyChain keyChain = new DeviceKeyChain();
    private final ArrayList<DeviceManagerEventListener> eventListeners = new ArrayList<>();

    public static DeviceManager getInstance() {
        if (instance == null)
            instance = new DeviceManager();

        return instance;
    }

    protected DeviceManager() {
        Log.i(LOG_TAG, "Created");
    }

    public Device[] getDevices() {
        return devices.values().toArray(new Device[0]);
    }

    public Device getDevice(String deviceId) {
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

    public void setParameter(Device device, String name, int value) {
        HashMap<String, Integer> p = new HashMap<>();
        p.put(name, value);

        setParameters(device, p);
    }

    public void setParameters(Device device, Map<String, Integer> parameters) {
        Log.d(LOG_TAG, String.format("Setting parameters of %s: %s", device.getId(), parameters));

        AppPacket packet = new AppPacket();
        packet.tcid = device.getId();
        packet.i = 0;

        CommandPack pack = new CommandPack();
        pack.keys = parameters.keySet().toArray(new String[0]);
        pack.values = parameters.values().toArray(new Integer[0]);
        pack.mac = packet.tcid;

        packet.pack = pack;

        final AsyncCommunicator comm = new AsyncCommunicator(keyChain);
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
                try {
                    final Packet[] responses = comm.get();

                    for (Packet response : responses) {
                        if (devices.containsKey(response.cid)) {
                            devices.get(response.cid).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }

    public void setWifi(String ssid, String password){

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
                        if (devices.containsKey(response.cid)) {
                            devices.get(response.cid).updateWithResultPack((ResultPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to get response of command. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(new Packet[] { packet });
    }

    public void discoverDevices() {
        Log.i(LOG_TAG, "Device discovery running...");

        final AsyncCommunicator comm = new AsyncCommunicator();
        comm.setCommunicationFinishedListener(new AsyncCommunicationFinishedListener() {
            @Override
            public void onFinished() {
            try {
                Packet[] responses = comm.get();
                Log.i(LOG_TAG, String.format("Got %d response(s)", responses.length));

                bindDevices(responses);
            } catch (Exception e) {

            }
            }
        });

        ScanPacket sp = new ScanPacket();

        comm.execute(new ScanPacket[]{ sp });
    }

    public void updateDevices() {
        if (devices.isEmpty()) {
            Log.i(LOG_TAG, "No devices to update");
            return;
        }

        Log.i(LOG_TAG, String.format("Updating %d device(s)", devices.size()));

        ArrayList<AppPacket> packets = new ArrayList<>();

        ArrayList<String> keys = new ArrayList<>();
        for (DeviceImpl.Parameter p : DeviceImpl.Parameter.values()) {
            keys.add(p.toString());
        }

        for (DeviceImpl device : devices.values()) {
            AppPacket packet = new AppPacket();
            packet.tcid = device.getId();
            packet.i = 0;

            StatusPack pack = new StatusPack();
            pack.keys = keys.toArray(new String[0]);
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
                        if (devices.containsKey(response.cid)) {
                            devices.get(response.cid).updateWithDatPack((DatPack) response.pack);
                        }
                    }

                    sendEvent(DeviceManagerEventListener.Event.DEVICE_STATUS_UPDATED);

                } catch (Exception e) {
                    Log.w(LOG_TAG, "Failed to get device update result. Error: " + e.getMessage());
                }
            }
        });
        comm.execute(packets.toArray(new Packet[0]));
    }

    private void bindDevices(Packet[] scanResponses) throws IOException {
        ArrayList<AppPacket> requests = new ArrayList<>();

        for (int i = 0; i < scanResponses.length; i++) {
            Packet response = scanResponses[i];

            if (!(response.pack instanceof DevicePack))
                continue;

            DevicePack devicePack = (DevicePack) response.pack;

            DeviceImpl device;

            if (!devices.containsKey(response.cid)) {
                device = new DeviceImpl(devicePack.mac, this);
                devices.put(devicePack.mac, device);
            } else {
                device = devices.get(response.cid);
            }
            device.updateWithDevicePack(devicePack);

            if (!keyChain.containsKey(response.cid)) {
                Log.i(LOG_TAG, "Binding device: " + devicePack.name);

                AppPacket request = new AppPacket();
                request.tcid = response.cid;
                request.pack = new BindPack();
                request.pack.mac = request.tcid;
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

            }
            }
        });
        comm.execute(requests.toArray(new Packet[0]));
    }

    private void storeDevices(Packet[] bindResponses) {
        for (Packet response : bindResponses) {
            if (!(response.pack instanceof BindOkPack))
                continue;

            BindOkPack pack = (BindOkPack) response.pack;

            Log.i(LOG_TAG, "Storing key for device: " + pack.mac);
            keyChain.addKey(pack.mac, pack.key);
        }

        sendEvent(DeviceManagerEventListener.Event.DEVICE_LIST_UPDATED);
        updateDevices();
    }

    private void sendEvent(DeviceManagerEventListener.Event event) {
        for (DeviceManagerEventListener listener : eventListeners)
            listener.onEvent(event);
    }
}
