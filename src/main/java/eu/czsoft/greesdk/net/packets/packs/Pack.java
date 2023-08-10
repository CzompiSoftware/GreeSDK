package eu.czsoft.greesdk.net.packets.packs;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

@ToString
public class Pack {

    @SerializedName("t")
    public String type;

    @SerializedName("mac")
    public String mac;
}
