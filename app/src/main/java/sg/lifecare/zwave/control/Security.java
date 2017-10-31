package sg.lifecare.zwave.control;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Security {

    public static final String TITLE = "security";

    public static final int DISARM = 0x00;
    public static final int ARM = 0x01;
    public static final int ARM_PARTIAL = 0x02;

    private int status;

    public static String toJson(Security security) {
        String result = new Gson().toJson(
                security, new TypeToken<Security>() {}.getType());

        return result;
    }

    public static Security fromJson(String result) {
        Security security = new Gson().fromJson(
                result, new TypeToken<Security>() {}.getType());

        return security;
    }

    public Security() {

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
