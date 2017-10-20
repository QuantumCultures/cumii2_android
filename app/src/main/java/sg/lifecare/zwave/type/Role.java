package sg.lifecare.zwave.type;

public class Role {

    /** For central gateways **/
    public static final int CENTRAL_STATIC_CONTROLLER = 1;

    /** For sub controllers that are not central gateways **/
    public static final int SUB_STATIC_CONTROLLER = 2;

    /** Remote control device that setup Z-Wave network **/
    public static final int PORTABLE_CONTROLLER = 3;

    /** Devices that can be used for setup but also report information through lifeline **/
    public static final int REPORTING_CONTROLLER = 4;

    /**
     * Low cost devices that do not require setup and only communicates direct range
     * such simple AV remote controls
     */
    public static final int PORTABLE_SLAVE = 5;

    /** Typical controllable device that are always on such as light switches **/
    public static final int ALWAYS_ON_SLAVE = 6;

    /** Devices that require direct control but are battery operated such as lock **/
    public static final int REACHABLE_SLEEPING_SLAVE = 7;

    /** Device that sleeps and report status when triggered such as sensors **/
    public static final int ALWAYS_SLEEPING_SLAVE = 8;
}
