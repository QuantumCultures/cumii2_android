package sg.lifecare.zwave;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.zwave.report.MultilevelSensorReport;

public class ZWaveUtil {

    private static final String TAG = "ZWaveUtil";

    public static String zwaveDevicesToJson(List<ZWaveDevice> devices) {

        if ((devices != null) && (devices.size() > 0)) {
            String result = new Gson().toJson(devices, new TypeToken<List<ZWaveDevice>>() {}.getType());
            Log.d(TAG, "zwaveDevicesToJson: " + result);

            return result;
        }

        return "";
    }

    public static List<ZWaveDevice> jsonToZwaveDevices(String str) {

        if (!TextUtils.isEmpty(str)) {
            List<ZWaveDevice> devices = new Gson().fromJson(str, new TypeToken<List<ZWaveDevice>>() {}.getType());
            Log.d(TAG, "jsonToZwaveDevices: "+ devices.size());

            // need to remap?
            /*if (devices.size() > 0) {
                for (ZWaveDevice device : devices) {
                    List<MultilevelSensorReport> reports =
                    if (device.)
                }
            }*/

            return devices;
        }

        return new ArrayList<>();
    }
}
