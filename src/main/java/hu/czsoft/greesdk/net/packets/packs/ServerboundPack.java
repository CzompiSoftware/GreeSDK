package hu.czsoft.greesdk.net.packets.packs;

import lombok.ToString;

/**
 * <b>C</b>lient-><b>S</b>erver<br/>
 * ServerboundPacket means, that it is sent from the <i>Client</i> to the <i>Server</i>, so in this case, from this Sdk to Gree appliance(s).
 */
@ToString(callSuper = true)
public class ServerboundPack extends Pack {

}
