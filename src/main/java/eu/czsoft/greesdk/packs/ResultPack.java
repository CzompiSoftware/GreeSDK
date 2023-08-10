package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class ResultPack extends Pack {
    public static String TYPE = "res";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("opt")
    public String[] keys;

    @SerializedName("p")
    public Integer[] values;

    public ResultPack() {
        type = TYPE;
    }
}
