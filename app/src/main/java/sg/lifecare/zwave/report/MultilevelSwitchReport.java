package sg.lifecare.zwave.report;

public class MultilevelSwitchReport {

    public static final int OFF_OR_DISABLED = 0x00;
    public static final int MIN_VALUE = 0x01;
    public static final int MAX_VALUE = 0x63;

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
