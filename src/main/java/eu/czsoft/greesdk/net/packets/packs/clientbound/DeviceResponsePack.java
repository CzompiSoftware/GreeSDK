package eu.czsoft.greesdk.net.packets.packs.clientbound;

import com.google.gson.annotations.SerializedName;
import eu.czsoft.greesdk.net.packets.packs.ClientboundPack;
import lombok.ToString;

@ToString(callSuper = true)
public class DeviceResponsePack extends ClientboundPack {
    public static String TYPE = "dev";

    @SerializedName("cid")
    public String clientId;
    @SerializedName("bc")
    public String brandId;
    @SerializedName("brand")
    public String brand;
    @SerializedName("catalog")
    protected String catalog;
    @SerializedName("mid")
    public String modelId;
    @SerializedName("model")
    public String model;
    @SerializedName("name")
    public String name;
    @SerializedName("series")
    public String series;
    @SerializedName("ver")
    public String hardwareVersion;
    @SerializedName("vender")
    public String vendor;
    @SerializedName("lock")
    public Integer lock;
    @SerializedName("hid")
    public String firmwareVersion;

    public DeviceResponsePack() {
        type = TYPE;
    }
}
