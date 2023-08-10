package eu.czsoft.greesdk.net.packets.packs;

import eu.czsoft.greesdk.net.packets.Packet;
import lombok.ToString;

/**
 * <b>S</b>erver-><b>C</b>lient<br/>
 * ClientboundPacket means, that it is sent from the <i>Server</i> to the <i>Client</i>, so in this case, from Gree appliance to this Sdk.
 */
@ToString(callSuper = true)
public class ClientboundPack extends Pack {

}

