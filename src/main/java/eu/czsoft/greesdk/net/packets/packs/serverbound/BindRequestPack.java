package eu.czsoft.greesdk.net.packets.packs.serverbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ClientboundPack;
import eu.czsoft.greesdk.net.packets.packs.ServerboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class BindRequestPack extends ServerboundPack {
    public static String TYPE = "bind";

    @SerializedName("uid")
    public int uid;

    public BindRequestPack() {
        type = TYPE;
    }
}


