package eu.czsoft.greesdk.net.packets.packs.clientbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ClientboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class ResultPack extends ClientboundPack {
    public static String TYPE = "res";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("opt")
    public String[] options;

    @SerializedName("p")
    public Integer[] values;

    public ResultPack() {
        type = TYPE;
    }
}
