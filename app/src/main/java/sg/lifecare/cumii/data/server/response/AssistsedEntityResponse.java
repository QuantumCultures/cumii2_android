package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class AssistsedEntityResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data extends EntityData {
        private boolean approved;
        private Date date_established;
        private List<Device> devices;
        private boolean disabled;
        private int gps_int_value;
        private List<Module> modules;
        private String status;
        private String type;

        public List<Device> getDevices() {
            return devices;
        }

        public int getGpsBatteryLevel() {
            return gps_int_value;
        }

    }

    public class Device {
        private String _id;
        private String name;
        private String type;

        public String getId() {
            return _id;
        }
    }

    class Module {
        private String _id;
        private String code;
        private String name;
    }
}


