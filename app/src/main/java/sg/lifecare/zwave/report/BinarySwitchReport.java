package sg.lifecare.zwave.report;

public class BinarySwitchReport {

    private static final int ON = 0xff;
    private static final int OFF = 0x00;

    private int value;

    BinarySwitchReport() {
    }

    public boolean isOn() {
        return value == ON;
    }

    public void setOn() {
        this.value = ON;
    }

    public void setOff() {
        this.value = OFF;
    }
}
