package sg.lifecare.zwave.report;

public class BinarySensorReport {

    public static final int GENERAL_PURPOSE = 0x01;
    public static final int SMOKE = 0x02;
    public static final int CO = 0x03;
    public static final int CO2 = 0x04;
    public static final int HEAT = 0x05;
    public static final int WATER = 0x06;
    public static final int FREEZE = 0x07;
    public static final int TAMPER = 0x08;
    public static final int AUX = 0x09;
    public static final int DOOR_OR_WINDOW = 0x0A;
    public static final int TILT = 0x0B;
    public static final int MOTION = 0x0C;
    public static final int GLASS_BREAK = 0x0D;

    private int sensor_value;
    private int sensor_type;

    public int getSensorValue() {
        return sensor_value;
    }

    public int getSensorType() {
        return sensor_type;
    }

    public void setSensorValue(int value) {
        this.sensor_value = value;
    }

    public void setSensorType(int type) {
        this.sensor_type = type;
    }
}
