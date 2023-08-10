package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class BindOkPack extends Pack {
    public static String TYPE = "bindok";

    public String key;

    @SerializedName("r")
    public int resultCode;

    public BindOkPack() {
        type = TYPE;
    }
}
