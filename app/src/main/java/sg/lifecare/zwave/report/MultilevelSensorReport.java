package sg.lifecare.zwave.report;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MultilevelSensorReport {

    public static final int AIR_TEMPERATURE = 0x01;
    public static final int GENERAL_PURPOSE_VALUE = 0x02;
    public static final int LUMINANCE = 0x03;
    public static final int POWER = 0x04;
    public static final int HUMIDITY= 0x05;
    public static final int VELOCITY = 0x06;
    public static final int DIRECTION = 0x07;
    public static final int ATMOSPHERIC_PRESSURE = 0x08;
    public static final int BAROMETRIC_PRESSURE = 0x09;
    public static final int SOLAR_RADIATION = 0x0A;
    public static final int DEW_POINT = 0x0B;
    public static final int RAIN_RATE = 0x0C;
    public static final int TIDE_LEVEL = 0x0D;
    public static final int WEIGHT = 0x0E;
    public static final int VOLTAGE = 0x0F;
    public static final int CURRENT = 0x10;
    public static final int CO2_LEVEL = 0x11;
    public static final int AIR_FLOW = 0x12;
    public static final int TANK_CAPACITY = 0x13;
    public static final int DISTANCE = 0x14;
    public static final int ANGLE_POSITION = 0x15;
    public static final int ROTATION = 0x16;
    public static final int WATER_TEMPERATURE = 0x17;
    public static final int SOIL_TEMPERATURE = 0x18;
    public static final int SEISMIC_INTENSITY = 0x19;
    public static final int SEISMIC_MAGNITUDE = 0x1A;
    public static final int ULTRAVIOLET = 0x1B;
    public static final int ELECTRICAL_RESISTIVITY = 0x1C;
    public static final int ELECTRICAL_CONDUCTIVITY = 0x1D;
    public static final int LOUDNESS = 0x1E;
    public static final int MOISTURE = 0x1F;
    public static final int FREQUENCY = 0x20;
    public static final int TIME = 0x21;
    public static final int TARGET_TEMPERATURE = 0x22;
    public static final int MULTIDEVICE = 0xFF;

    private int sensor_type;
    private int value; // Precision (3 Bit) + Scale (2 bit) + Size (3 bit)
    private List<Integer> data;

    protected MultilevelSensorReport(int type) {
        this.sensor_type = type;
        this.data = new ArrayList<Integer>();
    }

    public int getSensorType() {
        return sensor_type;
    }

    public int getScaleType() {
        return ((value & 0x18) >> 3);
    }

    public int getPrecision() {
        return ((value & 0xE0) >> 5);
    }

    public int getSize() {
        return (value & 0x07);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setData(List<Integer> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public double getSensorData() {
        int precision = getPrecision();
        int scaleType = getScaleType();
        int size = getSize();
        double value = 0d;

        Timber.d("getSensorData: precision=%d, scaleType=%d, size=%d", precision, scaleType, size);

        if (data.size() == size) {
            if (size == 1) {
                value = (this.data.get(0) & 0x000000ff);
            } else if (size == 2) {
                value = ((this.data.get(0) & 0x000000ff) << 8) + (this.data.get(1) & 0x000000ff);
            } else if (size == 4) {
                value = ((this.data.get(3) & 0x000000ff) << 24)
                        + ((this.data.get(2) & 0x000000ff) << 16)
                        + ((this.data.get(1) & 0x000000ff) << 8)
                        + (this.data.get(0) & 0x000000ff);
            }

            Timber.d("getSensorData: precisionPowe=%f, value=%f", Math.pow(10, precision), value);

            value = value / Math.pow(10, precision);

        } else {
            Timber.d("getSensorData: data size not match");
        }

        return value;
    }
}
