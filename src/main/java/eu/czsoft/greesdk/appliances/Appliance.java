package eu.czsoft.greesdk.appliances;

public interface Appliance {
    String getId();

    String getName();

    void setName(String name);

    boolean isPoweredOn();
    void setPoweredOn(boolean value);

    int getParameter(String name);

    void setParameter(String name, int value);

    void setWifiDetails(String ssid, String password);
}
