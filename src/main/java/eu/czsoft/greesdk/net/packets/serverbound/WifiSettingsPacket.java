package eu.czsoft.greesdk.net.packets.serverbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.ServerboundPacket;
import lombok.ToString;

@ToString(callSuper = true)
public class WifiSettingsPacket extends ServerboundPacket {
    public static String TYPE = "wlan";

    @SerializedName("psw")
    public String password;

    @SerializedName("ssid")
    public String ssid;

    public WifiSettingsPacket() {
        type = TYPE;
    }
}
