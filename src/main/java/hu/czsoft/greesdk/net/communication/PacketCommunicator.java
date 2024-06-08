package hu.czsoft.greesdk.net.communication;

import hu.czsoft.greesdk.Utils;
import hu.czsoft.greesdk.net.DeviceKeyChain;
import hu.czsoft.greesdk.net.packets.Packet;
import hu.czsoft.greesdk.net.packets.serverbound.ApplicationPacket;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log
public class PacketCommunicator {
    private final int DATAGRAM_PORT = 7000;
    private final int TIMEOUT_MS = 500;

    private DatagramSocket socket;
    private final DeviceKeyChain keyChain;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private List<Callable<Packet[]>> callableTasks = new ArrayList<>();
    private List<Future<Packet[]>> futures = new ArrayList<>();
    private PacketTaskFinishedListener function;


    public PacketCommunicator(DeviceKeyChain deviceKeyChain) {
        keyChain = deviceKeyChain;
    }

    public PacketCommunicator() {
        keyChain = null;
    }

    protected Packet[] doInBackground(Packet... requests) {
        Packet[] responses = new Packet[0];

        if (requests == null || requests.length == 0)
            return responses;

        if (!createSocket())
            return responses;

        try {
            for (Packet request : requests) {
                broadcastPacket(request);
            }
            responses = receivePackets(TIMEOUT_MS);
        } catch (Exception e) {
            LOGGER.severe("Error: " + e.getMessage());
        } finally {
            closeSocket();
        }

        return responses;
    }


    private void broadcastPacket(Packet packet) throws IOException {
        String data = Utils.serializePacket(packet, keyChain);

        LOGGER.info("Broadcasting: " + packet);

        DatagramPacket datagramPacket = new DatagramPacket(
                data.getBytes(), data.length(),
                InetAddress.getByName("255.255.255.255"), DATAGRAM_PORT);

        socket.send(datagramPacket);
    }

    private Packet[] receivePackets(int timeout) throws IOException {
        socket.setSoTimeout(timeout);

        ArrayList<Packet> responses = new ArrayList<>();
        ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();

        try {
            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, 65536);

                socket.receive(datagramPacket);

                datagramPackets.add(datagramPacket);
            }
        } catch (Exception e) {
            LOGGER.warning("Exception: " + e.getMessage());
        }

        for (DatagramPacket p : datagramPackets) {
            String data = new String(p.getData(), 0, p.getLength());
            InetAddress address = p.getAddress();

            LOGGER.info(String.format("Received response from %s: %s", address.getHostAddress(), data));

            Packet response = Utils.deserializePacket(data, keyChain);
            LOGGER.info(response.toString());
            // Filter out packets sent by us
            if (response.clientId != null && !response.clientId.equals(ApplicationPacket.CLIENT_ID)) {
                responses.add(response);
            }
        }

        return responses.toArray(new Packet[0]);
    }

    private boolean createSocket() {
        try {
            socket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
        } catch (SocketException e) {
            LOGGER.severe("Failed to create socket. Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void closeSocket() {
        LOGGER.info("Closing socket");

        socket.close();
        socket = null;
    }

    public Packet[] get() throws InterruptedException, ExecutionException {
        return futures.get(0).get();
    }

    public void setCommunicationFinishedListener(PacketTaskFinishedListener function) {
        this.function = function;
    }

    public void execute(Packet... packets) {
        callableTasks.clear();
        callableTasks.add(() -> doInBackground(packets));
        try {
            futures = service.invokeAll(callableTasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (var future : futures) {
            if (future.isDone()) {
                function.method();
            }
        }
    }

}

