package hu.czsoft.greesdk.appliances;

import hu.czsoft.greesdk.Utils;
import hu.czsoft.greesdk.DeviceManager;
import hu.czsoft.greesdk.net.packets.packs.clientbound.DeviceResponsePack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.ResultPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.ChangeOptionRequestPack;

import java.util.Map;
import java.util.logging.Logger;

public class ApplianceImpl implements Appliance {
    Logger LOGGER;

    final String deviceId;
    final DeviceManager deviceManager;

    public ApplianceImpl(String deviceId, DeviceManager deviceManager) {
        this.deviceId = deviceId;
        this.deviceManager = deviceManager;
    }

    private String name = "";
    private boolean poweredOn;


    public void updateWithDatPack(ChangeOptionRequestPack pack) {
        updateParameters(Utils.zip(pack.keys, pack.values));
    }

    public void updateWithResultPack(ResultPack pack) {
        updateParameters(Utils.zip(pack.options, pack.values));
    }

    public void updateWithDevicePack(DeviceResponsePack pack) {
        LOGGER.fine("Updating name: " + pack.name);
        name = pack.name;
    }

    @Override
    public String getId() {
        return deviceId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ApplianceType getType() {
        return ApplianceType.UNKNOWN;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public boolean isPoweredOn() {
        return poweredOn;
    }

    @Override
    public void setPoweredOn(boolean newState) {
        setParameter(Parameter.POWER, newState ? 1 : 0);
    }

    @Override
    public int getParameter(String name) {
        return 0;
    }

    @Override
    public void setParameter(String name, int value) {
        deviceManager.setParameter(this, name, value);
    }

    @Override
    public void setWiFiDetails(String ssid, String password) {
        deviceManager.setWiFi(ssid, password);
    }

    void setParameter(Parameter parameter, int value) {
        setParameter(parameter.toString(), value);
    }

    void setParameters(Parameter[] parameters, Integer[] values) {
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = parameters[i].toString();
        }

        deviceManager.setParameters(this, Utils.zip(names, values));
    }


    void updateParameters(Map<String, Integer> parameterMap) {
        LOGGER.fine("Updating parameterMap: " + parameterMap);

        poweredOn = getBooleanParameter(parameterMap, Parameter.POWER, poweredOn);
    }

    static <E> E getEnumParameter(Map<String, Integer> parameterMap, Parameter parameter, E[] values, E def) {
        if (parameterMap.containsKey(parameter.toString())) {
            int ordinal = parameterMap.get(parameter.toString());
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
        }

        return def;
    }

    static int getOrdinalParameter(Map<String, Integer> parameterMap, Parameter parameter, int def) {
        if (parameterMap.containsKey(parameter.toString()))
            return parameterMap.get(parameter.toString());
        return def;
    }

    static boolean getBooleanParameter(Map<String, Integer> parameterMap, Parameter parameter, boolean def) {
        return getOrdinalParameter(parameterMap, parameter, def ? 1 : 0) == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplianceImpl device = (ApplianceImpl) o;

        return deviceId.equals(device.deviceId);
    }

    @Override
    public int hashCode() {
        return deviceId.hashCode();
    }

}
