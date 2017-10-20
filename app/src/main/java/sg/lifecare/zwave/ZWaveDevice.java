package sg.lifecare.zwave;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sg.lifecare.zwave.report.BinarySwitchReport;
import sg.lifecare.zwave.report.MeterReport;
import sg.lifecare.zwave.report.MultilevelSensorReport;

public class ZWaveDevice {

    private int node_id;
    private String name;
    private String zone;
    private int device_type;
    private int role_type;
    private Date last_update;

    private List<Integer> command_classes;

    private BinarySwitchReport binary_switch_report;
    private MeterReport meter_report;
    private List<MultilevelSensorReport> multilevel_reports;

    public ZWaveDevice(int node_id) {
        this.node_id = node_id;

        command_classes = new ArrayList<Integer>();
        multilevel_reports = new ArrayList<MultilevelSensorReport>();
    }

    public List<Integer> getCommandClasses() {
        return command_classes;
    }

    public int getDeviceType() {
        return device_type;
    }

    public List<MultilevelSensorReport> getMultilevelReports() {
        return multilevel_reports;
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

    public boolean hasCommandClass(int commandClass) {
        for (int cc : command_classes) {
            if (cc == commandClass) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMultilevelReport(int sensorType) {
        for (MultilevelSensorReport mr : multilevel_reports) {
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

    public void addMultilevelReport(MultilevelSensorReport report) {
        if (!hasMultilevelReport(report.getSensorType())) {
            multilevel_reports.add(report);
        }
    }

    public void setBinarySwitchReport(BinarySwitchReport report) {
        this.binary_switch_report = report;
    }

    public void setMeterReport(MeterReport report) {
        this.meter_report = report;
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

}