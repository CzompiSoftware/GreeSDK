package eu.czsoft.greesdk.net.packets.packs.serverbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ServerboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class ChangeOptionRequestPack extends ServerboundPack {
    public static String TYPE = "dat";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("cols")
    public String[] keys;

    @SerializedName("dat")
    public Integer[] values;

    public ChangeOptionRequestPack() {
        type = TYPE;
    }
}
