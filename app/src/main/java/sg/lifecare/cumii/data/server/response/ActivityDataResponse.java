package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ActivityDataResponse extends Response {

    @SerializedName("Data")
    private List<ActivityDataResponse.Data> data;

    @Override
    public List<ActivityDataResponse.Data> getData() {
        return data;
    }

    public class Data {
        private String _id;
        private Entity entity;
        private Device device;
        private String zone;
        private List<Media> medias;
        private String extra_data;
        private Date create_date;
        private String event_type_id2;
        private String event_type_id;
        private String event_type_name;
        private String zone_code;
        private String message;
        private String subject;
        private String type;

        public String getId() {
            return _id;
        }

        public Date getCreateDate() {
            return create_date;
        }

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }
    }

    class Entity {
        private String _id;
        private String first_name;
        private String last_name;
        private String name;
    }

    class Device {
        private String _id;
    }

    class Rule {
        private String _id;
        private String name;
    }
}
