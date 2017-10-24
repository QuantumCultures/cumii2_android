package sg.lifecare.zwave.report;

public class DoorLockOperationReport {

    // door lock mode
    public static final int UNSECURED = 0x00;
    public static final int UNSECURED_WITH_TIMEOUT = 0x01;
    public static final int UNSECURED_FOR_INSIDE_DOOR_HANDLES = 0x10;
    public static final int UNSECURED_FOR_INSIDE_DOOR_HANDLES_WITH_TIMEOUT = 0x11;
    public static final int UNSECURED_FOR_OUTSIDE_DOOR_HANDLES = 0x20;
    public static final int UNSECURED_FOR_OUTSIDE_DOOR_HANDLES_WITH_TIMEOUT = 0x21;
    public static final int SECURED = 0xFF;

    public static final int TIMEOUT_NOT_SUPPORTED = 0xFE;

    private int lock_mode;
    private int handles_mode;
    private int condition;
    private int timeout_minutes;
    private int timeout_seconds;

    public int getLockMode() {
        return lock_mode;
    }

    public boolean isOutdoorHandles1Active() {
        return (handles_mode & 0x0010) == 0x0010;
    }

    public boolean isOutdoorHandle2Active() {
        return (handles_mode & 0x0020) == 0x0020;
    }

    public boolean isOutdoorHandle3Active() {
        return (handles_mode & 0x0040) == 0x0040;
    }

    public boolean isOutdoorHandle4Active() {
        return (handles_mode & 0x0080) == 0x0080;
    }

    public boolean isIndoorHandles1Active() {
        return (handles_mode & 0x0001) == 0x0001;
    }

    public boolean isIndoorHandle2Active() {
        return (handles_mode & 0x0002) == 0x0002;
    }

    public boolean isIndoorHandle3Active() {
        return (handles_mode & 0x0004) == 0x0004;
    }

    public boolean isIndoorHandle4Active() {
        return (handles_mode & 0x0008) == 0x0008;
    }

    public boolean isDoorClose() {
        return (condition & 0x0001) == 0x0001;
    }

    public boolean isBoltUnlocked() {
        return (condition & 0x0002) == 0x0002;
    }

    public boolean isLatchClosed() {
        return (condition & 0x0004) == 0x0004;
    }

    public int getTimeoutMinutes() {
        return this.timeout_minutes;
    }

    public int getTimeoutSeconds() {
        return this.timeout_seconds;
    }

    public void setLockMode(int mode) {
        this.lock_mode = mode;
    }

    public void setHandlesMode(int mode) {
        this.handles_mode = mode;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public void setTimeoutMinutes(int minutes) {
        this.timeout_minutes = minutes;
    }

    public void setTimeoutSeconds(int seconds) {
        this.timeout_seconds = seconds;
    }
}
