package eu.czsoft.greesdk.net.packets.serverbound;

import eu.czsoft.greesdk.net.packets.ServerboundPacket;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@ToString(callSuper = true)
public class ApplicationPacket extends ServerboundPacket {
    public static String CLIENT_ID = "greesdk";
    public static String TYPE = "pack";

    public ApplicationPacket() {
        clientId = CLIENT_ID;
        type = TYPE;
        uid = 0;
    }
}
