package sg.lifecare.zwave.report;

import java.util.ArrayList;
import java.util.List;

public class MeterReport {

    // rate type
    public static final int IMPORT = 0x01;
    public static final int EXPORT = 0x02;

    // meter type
    public static final int ELECTRIC = 0x01;
    public static final int GAS = 0x02;
    public static final int WATER = 0x03;

    // scale type
    public static final int ELECTRIC_KWH = 0x00;
    public static final int ELECTRIC_KVAH = 0x01;
    public static final int ELECTRIC_W = 0x02;
    public static final int ELECTRIC_PULSE_COUNT = 0x03;
    public static final int ELECTRIC_V = 0x04;
    public static final int ELECTRIC_A = 0x05;
    public static final int ELECTRIC_POWER_FACTOR = 0x06;

    public static final int GAS_CUBIC_METER = 0x00;
    public static final int GAS_CUBIC_FEET = 0x01;
    public static final int PULSE_COUNT = 0x03;

    public static final int WATER_CUBIC_METER = 0x00;
    public static final int WATER_CUBIC_FEET = 0x01;
    public static final int WATER_US_GALLON = 0x02;
    public static final int WATER_PULSE_COUNT = 0x03;

    public static final int NO_PREVIOUS_MATER_VALUE = 0x0000;
    // unknown delta time between meter value and previous meter value
    public static final int UNKNOWN_DELTA_TIME = 0XFFFF;

    private int type;
    private int value;
    private List<Integer> data;

    public MeterReport() {
        data = new ArrayList<Integer>();
    }

    public int getRateType() {
        return (value & 0x0060) >> 5;
    }

    public int getMeterType() {
        return (value & 0x001F);
    }

    public int getPrecision() {
        return (value & 0x00E0) >> 5;
    }

    public int getScale() {
        return (value & 0x0018) | ((value & 0x80) >> 5);
    }

    public int getSize() {
        return (value & 0x0007);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setValue(int value) {
        this.value = value;
    }

    // meter value (according to size)
    // delta time (16 bits)
    // previous meter value (same as meter value)
    public void setData(List<Integer> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
