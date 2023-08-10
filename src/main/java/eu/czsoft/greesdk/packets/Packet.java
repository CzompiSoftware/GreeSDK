package eu.czsoft.greesdk.packets;

import eu.czsoft.greesdk.packs.Pack;

import com.google.gson.annotations.SerializedName;

public class Packet {
    @SerializedName("t")
    public String type;

    public String tcid;
    public Integer i;
    public Integer uid;
    public String cid;

    @SerializedName("pack")
    public String encryptedPack;

    public transient Pack pack;
}
