package hu.czsoft.greesdk.net.packets.packs.clientbound;

import com.google.gson.annotations.SerializedName;
import hu.czsoft.greesdk.net.packets.packs.ClientboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class BindResponsePack extends ClientboundPack {
    public static String TYPE = "bindok";

    @SerializedName("key")
    public String deviceKey;

    @SerializedName("r")
    public int resultCode;

    public BindResponsePack() {
        type = TYPE;
    }
}

