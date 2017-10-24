package sg.lifecare.zwave.report;

public class DoorLockConfigurationReport {

    // operation type
    public static final int CONSTANT_OPERATION = 0x01;
    public static final int TIMED_OPERATION = 0x02;

    public static final int TIMEOUT_NOT_SUPPORTED = 0xFE;

    private int operation_type;
    private int handles_state;
    private int timeout_minutes;
    private int timeout_seconds;

    public int getOperationType() {
        return operation_type;
    }

    public boolean isOutdoorHandles1Enabled() {
        return (handles_state & 0x0010) == 0x0010;
    }

    public boolean isOutdoorHandle2Enabled() {
        return (handles_state & 0x0020) == 0x0020;
    }

    public boolean isOutdoorHandle3Enabled() {
        return (handles_state & 0x0040) == 0x0040;
    }

    public boolean isOutdoorHandle4Enabled() {
        return (handles_state & 0x0080) == 0x0080;
    }

    public boolean isIndoorHandles1Enabled() {
        return (handles_state & 0x0001) == 0x0001;
    }

    public boolean isIndoorHandle2Enabled() {
        return (handles_state & 0x0002) == 0x0002;
    }

    public boolean isIndoorHandle3Enabled() {
        return (handles_state & 0x0004) == 0x0004;
    }

    public boolean isIndoorHandle4Enabled() {
        return (handles_state & 0x0008) == 0x0008;
    }

    public void setOperationType(int type) {
        this.operation_type = type;
    }

    public void setHandlesState(int state) {
        this.handles_state = state;
    }

    public void setTimeoutMinutes(int minutes) {
        this.timeout_minutes = minutes;
    }

    public void setTimeoutSeconds(int seconds) {
        this.timeout_seconds = seconds;
    }
}
