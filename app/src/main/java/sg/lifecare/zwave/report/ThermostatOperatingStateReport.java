package sg.lifecare.zwave.report;

public class ThermostatOperatingStateReport {

    // operating state
    public static final int IDLE = 0x00;
    public static final int HEATING = 0x01;
    public static final int COOLING = 0x02;
    public static final int FAN_ONLY = 0x03;
    public static final int PENDING_HEAT = 0x04;
    public static final int PENDING_COOL = 0x05;
    public static final int VENT_OR_ECONOMIZER = 0x06;
    public static final int AUX_HEATING = 0x07;
    public static final int SECOND_STAGE_HEATING = 0x08;
    public static final int SECOND_STAGE_COOLING = 0x09;
    public static final int SECOND_STAGE_AUX_HEAT = 0x0A;
    public static final int THIRD_STAGE_AUX_HEAT = 0x0B;

    private int state;

    public int getState() {
        return (state & 0x000F);
    }

    public void setState(int state) {
        this.state = state;
    }
}
