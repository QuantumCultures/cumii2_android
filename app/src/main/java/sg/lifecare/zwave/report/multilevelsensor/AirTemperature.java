package sg.lifecare.zwave.report.multilevelsensor;

import sg.lifecare.zwave.report.MultilevelSensorReport;

public class AirTemperature extends MultilevelSensorReport {

    public static final int CELSIUS = 0x00;
    public static final int FAHRENHEIT = 0x01;

    public AirTemperature() {
        super(MultilevelSensorReport.AIR_TEMPERATURE);
    }
}
