package eu.czsoft.greesdk.appliances;

import eu.czsoft.greesdk.appliances.airconditioner.FanSpeed;
import eu.czsoft.greesdk.appliances.airconditioner.Mode;
import eu.czsoft.greesdk.appliances.airconditioner.TemperatureUnit;
import eu.czsoft.greesdk.appliances.airconditioner.VerticalSwingMode;

public interface AirConditionerAppliance extends Appliance {

    Mode getMode();

    void setMode(Mode mode);

    FanSpeed getFanSpeed();

    void setFanSpeed(FanSpeed fanSpeed);

    int getTemperature();

    void setTemperature(int value, TemperatureUnit unit);

    boolean isLightEnabled();

    void setLightEnabled(boolean enabled);

    boolean isQuietModeEnabled();

    void setQuietModeEnabled(boolean enabled);

    boolean isTurboModeEnabled();

    void setTurboModeEnabled(boolean enabled);

    boolean isHealthModeEnabled();

    void setHealthModeEnabled(boolean enabled);

    boolean isAirModeEnabled();

    void setAirModeEnabled(boolean enabled);

    boolean isXfanModeEnabled();

    void setXfanModeEnabled(boolean enabled);

    boolean isSavingModeEnabled();

    void setSavingModeEnabled(boolean enabled);

    boolean isSleepEnabled();

    void setSleepEnabled(boolean enabled);

    VerticalSwingMode getVerticalSwingMode();

    void setVerticalSwingMode(VerticalSwingMode mode);

}
