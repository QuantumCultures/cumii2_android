package sg.lifecare.zwave.report;

public class MultiChannelEndPointReport {

    private int value1;
    private int value2;

    public int getEndPoints() {
        return (value2 & 0x007F);
    }

    public boolean isIdentical() {
        return (value1 & 0x0040) == 0x0040;
    }

    public boolean isDynamic() {
        return (value1 & 0x0080) == 0x0080;
    }

    public void setValue1(int value) {
        this.value1 = value;
    }

    public void setValue2(int value) {
        this.value2 = value;
    }
}
