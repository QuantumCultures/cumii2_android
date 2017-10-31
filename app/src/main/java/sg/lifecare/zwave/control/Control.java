package sg.lifecare.zwave.control;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Control {

    private static final String TAG = "Control";

    public static final String TITLE = "control";

    public static final int OFF = 0x00;
    public static final int ON = 0xFF;

    private int node_id;
    private int value;

    public static String toJson(Control control) {
        String result = new Gson().toJson(
                control, new TypeToken<Control>() {}.getType());

        Log.d(TAG, "toJson: " + result);

        return result;
    }

    public static Control fromJson(String result) {
        Control control = new Gson().fromJson(
                result, new TypeToken<Control>() {}.getType());

        return control;
    }

    public Control() {

    }

    public int getNodeId() {
        return node_id;
    }

    public int getValue() {
        return value;
    }

    public void setNodeId(int id) {
        this.node_id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
