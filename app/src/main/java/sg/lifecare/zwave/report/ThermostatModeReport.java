package sg.lifecare.zwave.report;

public class ThermostatModeReport {

    // mode
    public static final int OFF = 0x00;
    public static final int HEAT = 0x01;
    public static final int COOL = 0x02;
    public static final int AUTO = 0x03;
    public static final int AUXILIARY_OR_EMERGENCY_HEAT = 0x04;
    public static final int RESUME = 0x05;
    public static final int FAN_ONLY = 0x06;
    public static final int FUMACE = 0x07;
    public static final int DRY_AIR = 0x08;
    public static final int MOIST_AIR = 0x09;
    public static final int AUTO_CHANGEOVER= 0x0A;
    public static final int ENERGY_SAVE_STATE = 0x0B;
    public static final int ENERGY_SAVE_COOL = 0x0C;
    public static final int AWAY = 0x0D;

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
