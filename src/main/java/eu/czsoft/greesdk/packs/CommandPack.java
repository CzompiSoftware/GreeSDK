package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class CommandPack extends Pack {
    public static String TYPE = "cmd";

    @SerializedName("opt")
    public String[] keys;

    @SerializedName("p")
    public Integer[] values;

    public CommandPack() {
        type = TYPE;
    }
}