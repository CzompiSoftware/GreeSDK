package eu.czsoft.greesdk.appliances;

public enum Parameter {
    POWER("Pow"),
    MODE("Mod"),
    TEMPERATURE("SetTem"),
    TEMPERATURE_UNIT("TemUn"),
    FAN_SPEED("WdSpd"),
    AIR_MODE("Air"),
    XFAN_MODE("Blo"),
    HEALTH_MODE("Health"),
    SLEEP("SwhSlp"),
    SLEEP_MODE("SlpMod"),
    QUIET_MODE("Quiet"),
    TURBO_MODE("Tur"),
    SAVING_MODE("SvSt"),
    LIGHT("Lig"),
    HORIZONTAL_SWING("SwingLfRig"),
    VERTICAL_SWING("SwUpDn"),
    STHT_MODE("StHt"),
    HEAT_COOL_TYPE("HeatCoolType"),
    TEM_REC_MODE("TemRec");

    private final String param;

    Parameter(final String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return param;
    }
}
