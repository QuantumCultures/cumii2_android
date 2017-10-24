package sg.lifecare.zwave.report;

public class BatteryLevelReport {

    public static final int BATTERY_LOW_WARNING = 0xFF;
    public static final int BATTERY_MIN = 0x00;
    public static final int BATTERY_MAX = 0x64;

    private int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
