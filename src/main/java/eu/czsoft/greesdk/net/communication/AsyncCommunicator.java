package eu.czsoft.greesdk.net.communication;

import eu.czsoft.greesdk.Utils;
import eu.czsoft.greesdk.net.DeviceKeyChain;
import eu.czsoft.greesdk.net.packets.Packet;
import eu.czsoft.greesdk.net.packets.serverbound.ApplicationPacket;
import eu.czsoft.greesdk.task.ConcurrentTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class AsyncCommunicator extends ConcurrentTask<Packet[], Void, Packet[]> {
    private final Logger LOGGER = LogManager.getLogger("AsyncCommunicator");
    private final int DATAGRAM_PORT = 7000;
    private final int TIMEOUT_MS = 500;

    private AsyncCommunicationFinishedListener communicationFinishedListener;
    private DatagramSocket socket;
    private final DeviceKeyChain keyChain;

    public void setCommunicationFinishedListener(AsyncCommunicationFinishedListener listener) {
        communicationFinishedListener = listener;
    }

    public AsyncCommunicator(DeviceKeyChain deviceKeyChain) {
        keyChain = deviceKeyChain;
    }

    public AsyncCommunicator() {
        keyChain = null;
    }

    @Override
    protected Packet[] doInBackground(Packet[]... args) {
        Packet[] requests = args[0];
        Packet[] responses = new Packet[0];

        if (requests == null || requests.length == 0)
            return responses;

        if (!createSocket())
            return responses;

        try {
            for (Packet request : requests)
                broadcastPacket(request);
            responses = receivePackets(TIMEOUT_MS);
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage());
        } finally {
            closeSocket();
        }

        return responses;
    }

    @Override
    protected void onPostExecute() {
        super.onPostExecute();

        if (communicationFinishedListener != null)
            communicationFinishedListener.onFinished();
    }

    private void broadcastPacket(Packet packet) throws IOException {
        String data = Utils.serializePacket(packet, keyChain);

        LOGGER.debug("Broadcasting: " + packet);

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
            LOGGER.warn("Exception: " + e.getMessage());
        }

        for (DatagramPacket p : datagramPackets) {
            String data = new String(p.getData(), 0, p.getLength());
            InetAddress address = p.getAddress();

            LOGGER.debug(String.format("Received response from %s: %s", address.getHostAddress(), data));

            Packet response = Utils.deserializePacket(data, keyChain);
            LOGGER.debug(response);
            // Filter out packets sent by us
            if (response.clientId != null && response.clientId != ApplicationPacket.CLIENT_ID)
                responses.add(response);
        }

        return responses.toArray(new Packet[0]);
    }

    private boolean createSocket() {
        try {
            socket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
        } catch (SocketException e) {
            LOGGER.error("Failed to create socket. Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void closeSocket() {
        LOGGER.info("Closing socket");

        socket.close();
        socket = null;
    }
}
