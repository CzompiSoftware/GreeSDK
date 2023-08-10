package eu.czsoft.greesdk.net.packets.packs.serverbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ServerboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class StatusResponsePack extends ServerboundPack {
    public static String TYPE = "status";

    @SerializedName("cols")
    public String[] options;

    public StatusResponsePack() {
        type = TYPE;
    }
}
