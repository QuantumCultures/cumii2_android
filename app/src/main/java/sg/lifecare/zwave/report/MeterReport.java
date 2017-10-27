package sg.lifecare.zwave.report;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.cumii.R;

public class MeterReport {

    public static final String TAG = "MeterReport";

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

    public static String getScaleUnit(Context context, int meterType, int scale) {
        Log.d(TAG, "getScaleUnit: meter_type=" + meterType + " scale=" + scale);
        if (meterType == ELECTRIC) {
            switch (scale) {
                case ELECTRIC_KWH:
                    return context.getString(R.string.zwave_unit_kwh);

                case ELECTRIC_KVAH:
                    return context.getString(R.string.zwave_unit_kVAh);

                case ELECTRIC_W:
                    return context.getString(R.string.zwave_unit_W);

                case ELECTRIC_PULSE_COUNT:
                    return context.getString(R.string.zwave_unit_pulse_count);

                case ELECTRIC_V:
                    return context.getString(R.string.zwave_unit_V);

                case ELECTRIC_A:
                    return context.getString(R.string.zwave_unit_A);

                case ELECTRIC_POWER_FACTOR:
                    return context.getString(R.string.zwave_unit_power_factor);
            }
        }


        return "";
    }

    public MeterReport() {
        data = new ArrayList<Integer>();
    }

    public int getRateType() {
        return (type & 0x0060) >> 5;
    }

    public int getMeterType() {
        return (type & 0x001F);
    }

    public int getPrecision() {
        return (value & 0x00E0) >> 5;
    }

    public int getScale() {
        return ((value & 0x0018) >> 3) | ((type & 0x0080) >> 5);
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

    public double getMeterValue() {
        int precision = getPrecision();
        int size = getSize();
        double value = 0d;

        Log.d(TAG, String.format("getMeterValue: precision=%d, size=%d", precision, size));

        if (data.size() == size) {
            if (size == 1) {
                value = (this.data.get(0) & 0x000000ff);
            } else if (size == 2) {
                value = ((this.data.get(1) & 0x000000ff) << 8) + (this.data.get(0) & 0x000000ff);
            } else if (size == 4) {
                value = ((this.data.get(0) & 0x000000ff) << 24)
                        + ((this.data.get(1) & 0x000000ff) << 16)
                        + ((this.data.get(2) & 0x000000ff) << 8)
                        + (this.data.get(3) & 0x000000ff);
            }

            Log.d(TAG, String.format("getMeterValue: precisionPowe=%f, value=%f", Math.pow(10, precision), value));

            value = value / Math.pow(10, precision);

        } else {
            Log.d(TAG, "getSensorData: data size not match");
        }

        return value;
    }
}
