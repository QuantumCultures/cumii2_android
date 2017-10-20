package sg.lifecare.zwave.report;

public abstract class Report {

    int command_class;
    int command;

    Report(int commandClass, int command) {
        this.command_class = commandClass;
        this.command =  command;
    }
}
