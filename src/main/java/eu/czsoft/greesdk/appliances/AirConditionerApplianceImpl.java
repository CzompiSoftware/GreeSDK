package eu.czsoft.greesdk.appliances;

import eu.czsoft.greesdk.appliances.airconditioner.FanSpeed;
import eu.czsoft.greesdk.appliances.airconditioner.Mode;
import eu.czsoft.greesdk.appliances.airconditioner.TemperatureUnit;
import eu.czsoft.greesdk.appliances.airconditioner.VerticalSwingMode;
import eu.czsoft.greesdk.DeviceManager;

import java.util.Map;
import java.util.logging.Logger;

public class AirConditionerApplianceImpl extends ApplianceImpl implements AirConditionerAppliance {
    private Mode mode = Mode.AUTO;
    private FanSpeed fanSpeed = FanSpeed.AUTO;
    private int temperature = 0;
    private TemperatureUnit temperatureUnit = TemperatureUnit.CELSIUS;
    private boolean lightEnabled;
    private boolean quietModeEnabled;
    private boolean turboModeEnabled;
    private boolean healthModeEnabled;
    private boolean airModeEnabled;
    private boolean xFanModeEnabled;
    private boolean savingModeEnabled;
    private boolean sleepEnabled;
    private boolean sleepModeEnabled;
    private VerticalSwingMode verticalSwingMode = VerticalSwingMode.DEFAULT;

    public AirConditionerApplianceImpl(String deviceId, DeviceManager deviceManager) {
        super(deviceId, deviceManager);

        LOGGER = Logger.getLogger(String.format("AirConditioner(%s)", deviceId));
    }

    @Override
    public ApplianceType getType() {
        return ApplianceType.AC;
    }
    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        setParameter(Parameter.MODE, mode.ordinal());
    }

    @Override
    public FanSpeed getFanSpeed() {
        return fanSpeed;
    }

    @Override
    public void setFanSpeed(FanSpeed fanSpeed) {
        setParameter(Parameter.FAN_SPEED, fanSpeed.ordinal());
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int value, TemperatureUnit unit) {
        setParameters(
                new Parameter[] { Parameter.TEMPERATURE, Parameter.TEMPERATURE_UNIT },
                new Integer[] { value, unit.ordinal() }
        );
    }

    @Override
    public boolean isLightEnabled() {
        return lightEnabled;
    }

    @Override
    public void setLightEnabled(boolean enabled) {
        setParameter(Parameter.LIGHT, enabled ? 1 : 0);
    }

    @Override
    public boolean isQuietModeEnabled() {
        return quietModeEnabled;
    }

    @Override
    public void setQuietModeEnabled(boolean enabled) {
        setParameter(Parameter.QUIET_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isTurboModeEnabled() {
        return turboModeEnabled;
    }

    @Override
    public void setTurboModeEnabled(boolean enabled) {
        setParameter(Parameter.TURBO_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isHealthModeEnabled() {
        return healthModeEnabled;
    }

    @Override
    public void setHealthModeEnabled(boolean enabled) {
        setParameter(Parameter.HEALTH_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isAirModeEnabled() {
        return airModeEnabled;
    }

    @Override
    public void setAirModeEnabled(boolean enabled) {
        setParameter(Parameter.AIR_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isXfanModeEnabled() {
        return xFanModeEnabled;
    }

    @Override
    public void setXfanModeEnabled(boolean enabled) {
        setParameter(Parameter.XFAN_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isSavingModeEnabled() {
        return savingModeEnabled;
    }

    @Override
    public void setSavingModeEnabled(boolean enabled) {
        setParameter(Parameter.SAVING_MODE, enabled ? 1 : 0);
    }

    @Override
    public boolean isSleepEnabled() {
        return sleepEnabled && sleepModeEnabled;
    }

    @Override
    public void setSleepEnabled(boolean enabled) {
        setParameters(new Parameter[] {Parameter.SLEEP, Parameter.SLEEP_MODE}, new Integer[] {enabled ? 1 : 0, enabled ? 1 : 0});
    }

    @Override
    public VerticalSwingMode getVerticalSwingMode() {
        return verticalSwingMode;
    }

    @Override
    public void setVerticalSwingMode(VerticalSwingMode mode) {
        setParameter(Parameter.VERTICAL_SWING, mode.ordinal());
    }

    void updateParameters(Map<String, Integer> parameterMap) {
        super.updateParameters(parameterMap);

        mode = getEnumParameter(parameterMap, Parameter.MODE, Mode.values(), mode);
        fanSpeed = getEnumParameter(parameterMap, Parameter.FAN_SPEED, FanSpeed.values(), fanSpeed);
        temperature = getOrdinalParameter(parameterMap, Parameter.TEMPERATURE, temperature);
        temperatureUnit = getEnumParameter(parameterMap, Parameter.TEMPERATURE_UNIT, TemperatureUnit.values(), temperatureUnit);
        lightEnabled = getBooleanParameter(parameterMap, Parameter.LIGHT, lightEnabled);
        quietModeEnabled = getBooleanParameter(parameterMap, Parameter.QUIET_MODE, quietModeEnabled);
        turboModeEnabled = getBooleanParameter(parameterMap, Parameter.TURBO_MODE, turboModeEnabled);
        healthModeEnabled = getBooleanParameter(parameterMap, Parameter.HEALTH_MODE, healthModeEnabled);
        airModeEnabled = getBooleanParameter(parameterMap, Parameter.AIR_MODE, airModeEnabled);
        xFanModeEnabled = getBooleanParameter(parameterMap, Parameter.XFAN_MODE, xFanModeEnabled);
        savingModeEnabled = getBooleanParameter(parameterMap, Parameter.SAVING_MODE, savingModeEnabled);
        sleepEnabled = getBooleanParameter(parameterMap, Parameter.SLEEP, sleepEnabled);
        sleepModeEnabled = getBooleanParameter(parameterMap, Parameter.SLEEP_MODE, sleepModeEnabled);
        verticalSwingMode = getEnumParameter(parameterMap, Parameter.VERTICAL_SWING, VerticalSwingMode.values(), verticalSwingMode);
    }

    @Override
    public String toString() {
        return "AirConditionerApplianceImpl{" +
                "deviceId='" + deviceId + '\'' +
                ", type='" + getType() + '\'' +
                ", name='" + getName() + '\'' +
                ", poweredOn=" + isPoweredOn() +
                ", mode=" + mode +
                ", fanSpeed=" + fanSpeed +
                ", temperature=" + temperature +
                ", temperatureUnit=" + temperatureUnit +
                ", lightEnabled=" + lightEnabled +
                ", quietModeEnabled=" + quietModeEnabled +
                ", turboModeEnabled=" + turboModeEnabled +
                ", healthModeEnabled=" + healthModeEnabled +
                ", airModeEnabled=" + airModeEnabled +
                ", xFanModeEnabled=" + xFanModeEnabled +
                ", savingModeEnabled=" + savingModeEnabled +
                ", sleepEnabled=" + sleepEnabled +
                ", sleepModeEnabled=" + sleepModeEnabled +
                ", verticalSwingMode=" + verticalSwingMode +
                '}';
    }
}
