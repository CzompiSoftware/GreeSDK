package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class DatPack extends Pack {
    public static String TYPE = "dat";

    @SerializedName("r")
    public int resultCode;

    @SerializedName("cols")
    public String[] keys;

    @SerializedName("dat")
    public Integer[] values;

    public DatPack() {
        type = TYPE;
    }
}