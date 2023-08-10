package eu.czsoft.greesdk.packets;

import com.google.gson.annotations.SerializedName;

public class WifiSettingsPacket extends Packet {
    public static String TYPE = "wlan";

    @SerializedName("psw")
    public String password;

    @SerializedName("ssid")
    public String ssid;

    public WifiSettingsPacket() {
        type = TYPE;
    }
}