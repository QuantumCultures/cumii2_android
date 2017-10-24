package sg.lifecare.zwave;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ZWaveManager {

    public static final String VERSION = "1.0.0";

    public static ZWaveManager sZwaveManager;

    private List<ZWaveDevice> devices;

    public static ZWaveManager getInstance(Context context) {
        if (sZwaveManager == null) {
            sZwaveManager = new ZWaveManager();
        }

        return sZwaveManager;
    }

    private ZWaveManager() {
        devices = new ArrayList<>();
    }

    public List<ZWaveDevice> getDevices() {
        return devices;
    }

    public boolean isDeviceAdded(int nodeId) {
        if (devices.size() > 0) {
            for (ZWaveDevice device : devices) {
                if (device.getNodeId() == nodeId) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean addNewDevice(ZWaveDevice device) {

        if (isDeviceAdded(device.getNodeId())) {
            return false;
        }

        devices.add(device);
        return true;
    }

    public ZWaveDevice findDeviceByNodeId(int nodeId) {

        for (ZWaveDevice device : devices) {
            if (device.getNodeId() == nodeId) {
                return device;
            }
        }
        return null;
    }

    public void removeAllDevices() {
        devices.clear();
    }
}
