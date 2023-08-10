package eu.czsoft.greesdk.net.packets;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.Pack;
import lombok.ToString;

/**
 * <b>C</b>lient-><b>S</b>erver<br/>
 * ServerboundPacket means, that it is sent from the <i>Client</i> to the <i>Server</i>, so in this case, from this Sdk to Gree appliance(s).
 */
@ToString(callSuper = true)
public class ServerboundPacket extends Packet {
}
