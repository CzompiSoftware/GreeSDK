package eu.czsoft.greesdk.packs;

import com.google.gson.annotations.SerializedName;

public class DevicePack extends Pack {
    public static String TYPE = "dev";

    public String cid;
    public String bc;
    public String brand;
    public String catalog;
    public String mid;
    public String model;
    public String name;
    public String series;
    public String ver;
    public Integer lock;

    @SerializedName("vender")
    public String vendor;

    public DevicePack() {
        type = TYPE;
    }
}
