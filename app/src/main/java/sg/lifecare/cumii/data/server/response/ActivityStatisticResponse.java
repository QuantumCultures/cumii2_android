package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ActivityStatisticResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public Data getData() {
        return data.size() > 0 ?  data.get(0) : null;
    }

    public class Data {

        private String _id;

        private String analysis_fail;
        private String analysis_fail_reason;
        private int average_activity_duration;
        private Date date_of_analysis;
        private int day_average_activity_duration;
        private int day_average_inactivity_duration;
        private int day_max_activity_duration;
        private int day_max_inactivity_duration;
        private int day_min_activity_duration;
        private int day_min_inactivity_duration;
        private int max_activity_duration;
        private int max_inaction_duration;
        private int min_activity_duration;
        private int min_inactivity_duration;
        private int night_average_activity_duration;
        private int night_average_inactivity_duration;
        private int night_max_activity_duration;
        private int night_max_inactivity_duration;
        private int night_min_activity_duration;
        private int night_min_inactivity_duration;

        public int average_activity_level;
        public int average_inactivity_level;
        public int average_day_activity_level;
        public int average_day_inactivity_level;
        public int average_night_activity_level;
        public int average_night_inactivity_level;

        public int getDayAverageActivityLevel() {
            return average_day_activity_level;
        }

        public int getDayAverageInactivityDuration() {
            return day_average_inactivity_duration / 60;
        }

        public int getDayMaxInactivityDuration() {
            return day_max_inactivity_duration / 60;
        }

        public int getNightAverageActivityLevel() {
            return average_night_activity_level;
        }

        public int getNightAverageInactivityDuration() {
            return night_average_inactivity_duration / 60;
        }

        public int getNightMaxInactivityDuration() {
            return night_max_inactivity_duration / 60;
        }

    }
}
