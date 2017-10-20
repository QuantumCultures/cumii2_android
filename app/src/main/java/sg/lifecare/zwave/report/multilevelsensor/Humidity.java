package sg.lifecare.zwave.report.multilevelsensor;

import sg.lifecare.zwave.report.MultilevelSensorReport;

public class Humidity extends MultilevelSensorReport {

    public static final int PERCENTAGE_VALUE = 0x00;
    public static final int ABSOLUTE_HUMIDITY = 0x01;

    public Humidity() {
        super(MultilevelSensorReport.HUMIDITY);
    }
}
