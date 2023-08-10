package eu.czsoft.greesdk.net.packets.packs.serverbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ServerboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class CommandRequestPack extends ServerboundPack {
    public static String TYPE = "cmd";

    @SerializedName("opt")
    public String[] options;

    @SerializedName("p")
    public Integer[] values;

    public CommandRequestPack() {
        type = TYPE;
    }
}

