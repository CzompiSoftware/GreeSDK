package eu.czsoft.greesdk.packets;

public class AppPacket extends Packet {
    public static String CID = "app";
    public static String TYPE = "pack";

    public AppPacket() {
        cid = CID;
        type = TYPE;
        uid = 0;
    }
}
