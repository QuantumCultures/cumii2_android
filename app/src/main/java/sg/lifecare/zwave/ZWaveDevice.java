package sg.lifecare.zwave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.lifecare.zwave.report.AlarmReport;
import sg.lifecare.zwave.report.BasicReport;
import sg.lifecare.zwave.report.BinarySensorReport;
import sg.lifecare.zwave.report.BinarySwitchReport;
import sg.lifecare.zwave.report.MeterReport;
import sg.lifecare.zwave.report.MultiChannelCapabilityReport;
import sg.lifecare.zwave.report.MultiChannelEndPointReport;
import sg.lifecare.zwave.report.MultilevelSensorReport;
import sg.lifecare.zwave.report.MultilevelSwitchReport;

public class ZWaveDevice {

    private int node_id;
    private String name;
    private String zone;
    private int device_type;
    private int role_type;
    private Date last_update;

    private List<Integer> command_classes;

    private BasicReport basic_report;
    private BinarySensorReport binary_sensor_report;
    private BinarySwitchReport binary_switch_report;
    private MultiChannelCapabilityReport multichannel_capability_report;
    private MultiChannelEndPointReport multichannel_endpoint_report;
    private List<AlarmReport> alarm_reports;
    private List<MeterReport> meter_reports;
    private List<MultilevelSensorReport> multilevel_sensor_reports;
    private MultilevelSwitchReport multilevel_switch_report;

    public ZWaveDevice(int node_id) {
        this.node_id = node_id;

        command_classes = new ArrayList<Integer>();

        alarm_reports = new ArrayList<AlarmReport>();
        meter_reports = new ArrayList<MeterReport>();
        multilevel_sensor_reports = new ArrayList<MultilevelSensorReport>();
    }

    public String getName() {
        return name;
    }

    public int getNodeId() {
        return node_id;
    }

    public int getRoleId() {
        return role_type;
    }

    public String getZone() {
        return zone;
    }


    public List<Integer> getCommandClasses() {
        return command_classes;
    }

    public int getDeviceType() {
        return device_type;
    }

    public List<AlarmReport> getAlarmReports() {
        return alarm_reports;
    }

    public BasicReport getBasicReport() {
        return basic_report;
    }

    public BinarySensorReport getBinarySensorReport() {
        return binary_sensor_report;
    }

    public BinarySwitchReport getBinarySwitchReport() {
        return binary_switch_report;
    }

    public List<MeterReport> getMeterReports() {
        return meter_reports;
    }

    public MultiChannelCapabilityReport getMultichannelCapabilityReport() {
        return multichannel_capability_report;
    }

    public MultiChannelEndPointReport getMultichannelEndpointReport() {
        return multichannel_endpoint_report;
    }

    public List<MultilevelSensorReport> getMultilevelSensorReports() {
        return multilevel_sensor_reports;
    }

    public MultilevelSwitchReport getMultilevelSwitchReport() {
        return multilevel_switch_report;
    }

    public void setDeviceType(int type) {
        this.device_type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoleType(int type) {
        this.role_type = type;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setBasicReport(BasicReport report) {
        this.basic_report = report;
    }

    public void setBinarySensorReport(BinarySensorReport report) {
        this.binary_sensor_report = report;
    }

    public void setBinarySwitchReport(BinarySwitchReport report) {
        this.binary_switch_report = report;
    }

    public void setMultiChannelCapabilityReport(MultiChannelCapabilityReport report) {
        this.multichannel_capability_report = report;
    }

    public void setMultiChannelEndpointReport(MultiChannelEndPointReport report) {
        this.multichannel_endpoint_report = report;
    }

    public void setMultilevelSwitchReport(MultilevelSwitchReport report) {
        this.multilevel_switch_report = report;
    }


    public boolean hasCommandClass(int commandClass) {
        for (int cc : command_classes) {
            if (cc == commandClass) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAlarmReport(int alarmType) {
        for (AlarmReport ar : alarm_reports) {
            if (ar.getAlarmType() == alarmType) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMeterReport(int meterType) {
        for (MeterReport mr : meter_reports) {
            if (mr.getMeterType() == meterType) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMultilevelSensorReport(int sensorType) {
        for (MultilevelSensorReport mr : multilevel_sensor_reports) {
            if (mr.getSensorType() == sensorType) {
                return true;
            }
        }

        return false;
    }

    public void addCommandClass(int commandClass) {
        if (!hasCommandClass(commandClass)) {
            command_classes.add(commandClass);
        }
    }

    public void addAlarmReport(AlarmReport alarmReport) {
        if (!hasAlarmReport(alarmReport.getAlarmType())) {
            alarm_reports.add(alarmReport);
        }
    }

    public void addMeterReport(MeterReport report) {
        if (!hasMeterReport(report.getMeterType())) {
            meter_reports.add(report);
        }
    }

    public void addMultilevelSensorReport(MultilevelSensorReport report) {
        if (!hasMultilevelSensorReport(report.getSensorType())) {
            multilevel_sensor_reports.add(report);
        }
    }
}
