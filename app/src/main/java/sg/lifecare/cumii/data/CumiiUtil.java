package sg.lifecare.cumii.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import okhttp3.ResponseBody;
import sg.lifecare.cumii.data.server.response.Response;
import timber.log.Timber;

public class CumiiUtil {

    private static Gson sGson = null;

    // user levels
    public static final int GUEST_LEVEL =  100;
    public static final int USER_LEVEL = 200;
    public static final int STUDENT_LEVEL = 300;
    public static final int CAREGIVER_LEVEL = 400;
    public static final int TEACHER_LEVEL = 400;
    public static final int CLINICIAN_LEVEL = 410;
    public static final int DOCTOR_LEVEL = 412;
    public static final int ENTERPRISE_ADMIN_LEVEL = 440;
    public static final int ENTERPRISE_SYSTEM_ADMIN_LEVEL = 450;
    public static final int SYSTEM_ADMIN_LEVEL = 500;

    // event types
    public static final String SWITCH_EVENT = "SW";
    public static final String PANIC_EVENT = "P";
    public static final String DOOR_EVENT = "D";
    public static final String UTILITY_EVENT = "U";
    public static final String SECURITY_EVENT = "S";
    public static final String MEASUREMENT_EVENT = "V";
    public static final String TEST_EVENT = "T";
    public static final String BATT_EVENT = "BATT";
    public static final String ALERT_EVENT = "A";

    public enum EventType {
        UNKNOWN,
        SWITCH,
        PANIC,
        DOOR,
        UTILITY,
        SECURITY,
        MEASUREMENT,
        TEST,
        BATT,
        ALERT,
    }

    public static EventType getEventType(String type) {
        if (TextUtils.isEmpty(type)) {
            return EventType.UNKNOWN;
        }

        switch (type) {
            case SWITCH_EVENT:
                return EventType.SWITCH;

            case PANIC_EVENT:
                return EventType.PANIC;

            case DOOR_EVENT:
                return EventType.DOOR;

            case UTILITY_EVENT:
                return EventType.UTILITY;

            case SECURITY_EVENT:
                return EventType.SECURITY;

            case MEASUREMENT_EVENT:
                return EventType.MEASUREMENT;

            case BATT_EVENT:
                return EventType.BATT;

            case TEST_EVENT:
                return EventType.TEST;

            case ALERT_EVENT:
                return EventType.ALERT;

            default:
                return EventType.UNKNOWN;
        }
    }

    public static Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Date.class,
                            (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
                                if (jsonElement == null) {
                                    return null;
                                }

                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                return formatter.parseDateTime(jsonElement.getAsString()).toDate();
                            })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();
        }

        return sGson;
    }

    public static Response getResponse(ResponseBody responseBody, Type type) {
        Gson gson = getGson();
        String result = "";

        if (responseBody != null) {
            try {
                result = responseBody.string();
            } catch (IOException e) {
                Timber.e(e.getMessage());
            }
        }

        return gson.fromJson(result, type);
    }
}
