package sg.lifecare.zwave.report.multilevelsensor;

import sg.lifecare.zwave.report.MultilevelSensorReport;

public class Luminance extends MultilevelSensorReport {

    public static final int PERCENTAGE_VALUE = 0x00;
    public static final int LUX = 0x01;

    public Luminance() {
        super(MultilevelSensorReport.LUMINANCE);
    }
}
