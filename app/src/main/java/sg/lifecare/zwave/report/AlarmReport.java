package sg.lifecare.zwave.report;

import java.util.ArrayList;
import java.util.List;

public class AlarmReport {

    public static final int DEACTIVATE_ALARM = 0x00;
    public static final int ACTIVATE_ALARM = 0xFF;

    // ZWave alarm type/event/event parameters
    public static final int SMOKE_ALARM = 0x01;
    public static final int SMOKE_DETECTED = 0x01;
    public static final int SMOKE_DETECTED_UNKNOWN_LOCATION = 0x02;
    public static final int SMOKE_UNKNOWN_EVENT = 0xFE;

    public static final int CO_ALARM = 0x02;
    public static final int CO_DETECTED = 0x01;
    public static final int CO_DETECTED_UNKNOWN_LOCATION = 0x02;
    public static final int CO_UNKNOWN_EVENT = 0xFE;

    public static final int CO2_ALARM = 0x03;
    public static final int CO2_DETECTED = 0x01;
    public static final int CO2_DETECTED_UNKNOWN_LOCATION = 0x02;
    public static final int CO2_UNKNOWN_EVENT = 0xFE;

    public static final int HEAT_ALARM = 0x04;
    public static final int OVERHEAT_DETECTED = 0x01;
    public static final int OVERHEAD_DETECTED_UNKNOWN_LOCATION = 0x02;
    public static final int RAPID_TEMPERATURE_RISE = 0x03;
    public static final int RAPID_TEMPERATURE_RISE_UNKNOWN_LOCATION = 0x04;
    public static final int UNDERHEAT_DETECTED = 0x05;
    public static final int UNDERHEAT_DETECTED_UNKNOWN_LOCATION = 0x06;
    public static final int HEAT_UNKNOWN_EVENT = 0xFE;

    public static final int WATER_ALARM = 0x05;
    public static final int WATER_LEAK_DETECTED = 0x01;
    public static final int WATER_LEAK_DETECTED_UNKNOWN_LOCATION = 0x02;
    public static final int WATER_LEVEL_DROPPED = 0x03;
    public static final int WATER_LEVEL_DROPPED_UNKNOWN_LOCATION = 0x04;
    public static final int WATER_UNKNOWN_EVENT = 0xFE;

    public static final int ACCESS_CONTROL_ALARM = 0x06;
    public static final int MANUAL_LOCK_OPERATION = 0x01;
    public static final int MANUAL_UNLOCK_OPERATION = 0x02;
    public static final int RF_LOCK_OPERATION = 0x03;
    public static final int RF_UNLOCK_OPERATION = 0x04;
    public static final int KEYPAD_LOCK_OPERATION = 0x05;
    public static final int KEYPAD_UNLOCK_OPERATION = 0x06;
    public static final int KEYPAD_UNKNOWN_EVENT = 0xFE;

    public static final int BURGLAR_ALARM = 0x07;
    public static final int INTRUSION = 0x01;
    public static final int INTRUSION_UNKNOWN_LOCATION = 0x02;
    public static final int TAMPERING_PRODUCT_COVERING_REMOVED = 0x03;
    public static final int TAMPERING_INVALID_CODE = 0x04;
    public static final int GLASS_BROKERAGE = 0x05;
    public static final int GLASS_BROKERAGE_UNKNOWN_LOCATION = 0x06;
    public static final int BURGLAR_UNKNOWN_EVENT = 0xFE;

    public static final int POWER_MANAGEMENT_ALARM = 0x08;
    public static final int POWER_HAS_BEEN_APPLIED = 0x01;
    public static final int AC_MAINS_DISCONNECTED = 0x02;
    public static final int AC_MAINS_RECONNECTED = 0x03;
    public static final int SURGE_DETECTION = 0x04;
    public static final int VOLTAGE_DROP_DRIFT = 0x05;
    public static final int POWER_MANAGEMENT_UNKNOWN_EVENT = 0xFE;

    public static final int SYSTEM_ALARM = 0x09;
    public static final int SYSTEM_HARDWARE_FAILURE = 0x01;
    public static final int SYSTEM_SOFTWARE_FAILURE = 0x02;
    public static final int SYSTEM_UNKNOWN_EVENT = 0xFE;

    public static final int EMERGENCY_ALARM = 0x0A;
    public static final int CONTACT_POLICE = 0x01;
    public static final int CONTACT_FIRE_SERVICE = 0x02;
    public static final int CONTACT_MEDICAL_SERVICE = 0x03;
    public static final int EMERGENCY_UNKNOWN_EVENT = 0xFE;

    public static final int ALARM_CLOCK = 0x0B;
    public static final int WAKE_UP = 0x01;

    private int alarm_type;
    private int alarm_level;
    private int net_source_node_id;
    private int zwave_alarm_status;
    private int zwave_alarm_type;
    private int zwave_alarm_event;
    private List<Integer> event_parameters = new ArrayList<Integer>();

    public int getAlarmType() {
        return this.alarm_type;
    }

    public int getAlarmLevel() {
        return this.alarm_level;
    }

    public int setNetSourceNodeId() {
        return this.net_source_node_id;
    }

    public int getZwaveAlarmStatus() {
        return this.zwave_alarm_status;
    }

    public int getZwaveAlarmType() {
        return this.zwave_alarm_type;
    }

    public int getZwaveAlarmEvent() {
        return this.zwave_alarm_event;
    }

    public List<Integer> getEventParameter() {
        return this.event_parameters;
    }


    public void setAlarmType(int type) {
        this.alarm_type = type;
    }

    public void setAlarmLevel(int level) {
        this.alarm_level = level;
    }

    public void setNetSourceNodeId(int id) {
        this.net_source_node_id = id;
    }

    public void setZwaveAlarmStatus(int status) {
        this.zwave_alarm_status = status;
    }

    public void setZwaveAlarmType(int type) {
        this.zwave_alarm_type = type;
    }

    public void setZwaveAlarmEvent(int event) {
        this.zwave_alarm_event = event;
    }

    public void addEventParameter(int parameter) {
        this.event_parameters.add(parameter);
    }
}
