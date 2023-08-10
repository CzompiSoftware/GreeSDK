package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class StatusPack extends Pack {
    public static String TYPE = "status";

    @SerializedName("cols")
    public String[] keys;

    public StatusPack() {
        type = TYPE;
    }
}