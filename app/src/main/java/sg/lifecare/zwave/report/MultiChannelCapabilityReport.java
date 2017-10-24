package sg.lifecare.zwave.report;

import java.util.ArrayList;
import java.util.List;

public class MultiChannelCapabilityReport {

    private int value;
    private int generic_device;
    private int specific_device;
    private List<Integer> command_classes = new ArrayList<Integer>();

    public boolean isDynamic() {
        return (value & 0x0080) == 0x0080;
    }

    public int getEndPoint() {
        return (value & 0x007F);
    }

    public int getGenericDevice() {
        return generic_device;
    }

    public int getSpecificDevice() {
        return specific_device;
    }

    public List<Integer> getCommandClasses() {
        return command_classes;
    }


    public void setValue(int value) {
        this.value = value;
    }

    public void setGenericDevice(int generic) {
        this.generic_device = generic;
    }

    public void setSpecificDevice(int specific) {
        this.specific_device = specific;
    }

    public void addCommandClass(int commandClass) {
        this.command_classes.add(commandClass);
    }

}
