package eu.czsoft.greesdk.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

import eu.czsoft.greesdk.packets.AppPacket;
import eu.czsoft.greesdk.packets.Packet;
import eu.czsoft.greesdk.Utils;

public class AsyncCommunicator extends AsyncTask<Packet[], Void, Packet[]> {
    private final String LOG_TAG = "AsyncCommunicator";
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
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        } finally {
            closeSocket();
        }

        return responses;
    }

    @Override
    protected void onPostExecute(Packet[] responses) {
        super.onPostExecute(responses);

        if (communicationFinishedListener != null)
            communicationFinishedListener.onFinished();
    }

    private void broadcastPacket(Packet packet) throws IOException {
        String data = Utils.serializePacket(packet, keyChain);

        Log.d(LOG_TAG, "Broadcasting: " + data);

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
            Log.w(LOG_TAG, "Exception: " + e.getMessage());
        }

        for (DatagramPacket p : datagramPackets) {
            String data = new String(p.getData(), 0, p.getLength());
            InetAddress address = p.getAddress();

            Log.d(LOG_TAG, String.format("Received response from %s: %s", address.getHostAddress(), data));

            Packet response = Utils.deserializePacket(data, keyChain);

            // Filter out packets sent by us
            if (response.cid != null && response.cid != AppPacket.CID)
                responses.add(response);
        }

        return responses.toArray(new Packet[0]);
    }

    private boolean createSocket() {
        try {
            socket = new DatagramSocket(new InetSocketAddress(DATAGRAM_PORT));
        } catch (SocketException e) {
            Log.e(LOG_TAG, "Failed to create socket. Error: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void closeSocket() {
        Log.i(LOG_TAG, "Closing socket");

        socket.close();
        socket = null;
    }
}
