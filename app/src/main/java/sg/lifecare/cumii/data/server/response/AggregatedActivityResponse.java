package sg.lifecare.cumii.data.server.response;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AggregatedActivityResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data {

        private float activity_level_in_zone;
        private int count;
        private Zone zone;

        public float getActivityLevelInZone() {
            return activity_level_in_zone;
        }

        private int getCount() {
            return count;
        }

        public Zone getZone() {
            return zone;
        }
    }

    public class Zone {

        private String _id;
        private String name;
        private String type;
        private String value;
        private String value1;
        private String value2;
        private String value3;
        private String value4;

        public int getColor() {
            return Color.parseColor(value3);
        }

        public String getName() {
            return name;
        }
    }
}
