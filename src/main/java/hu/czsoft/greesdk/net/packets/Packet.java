package hu.czsoft.greesdk.net.packets;

import com.google.gson.annotations.SerializedName;
import hu.czsoft.greesdk.net.packets.packs.Pack;
import lombok.ToString;

/**
 * In this documentation, the Sdk is defined as Client and Gree appliances are the Server.
 * This interface ties both types of packages together.
 */
@ToString(exclude = {"encryptedPack"})
public class Packet {

    @SerializedName("t")
    public String type;

    @SerializedName("tcid")
    public String targetClientId;
    @SerializedName("i")
    public Integer i;
    @SerializedName("uid")
    public Integer uid;
    @SerializedName("cid")
    public String clientId;

    @SerializedName("pack")
    public String encryptedPack;

    public transient Pack pack;
}
