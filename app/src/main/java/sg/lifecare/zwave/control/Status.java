package sg.lifecare.zwave.control;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Status {

    private static final String TAG = "Status";

    public static final String TITLE = "status";

    private List<Integer> node_ids = new ArrayList<Integer>();

    public static String toJson(Status status) {
        String result = new Gson().toJson(
                status, new TypeToken<Status>() {}.getType());

        Log.d(TAG, "toJson: " + result);

        return result;
    }

    public static Status fromJson(String result) {
        Status status = new Gson().fromJson(
                result, new TypeToken<Status>() {}.getType());

        return status;
    }

    public Status() {

    }

    public List<Integer> getNodeIds() {
        return node_ids == null ? new ArrayList<Integer>() : node_ids;
    }

    public void addNodeId(int id) {
        if (node_ids.size() > 0) {
            for (int nodeId : node_ids) {
                if (id == nodeId) {
                    break;
                }
            }
        }

        node_ids.add(id);

    }
}
