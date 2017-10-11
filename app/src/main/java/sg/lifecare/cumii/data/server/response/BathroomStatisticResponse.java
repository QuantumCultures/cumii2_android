package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class BathroomStatisticResponse extends Response {


    @SerializedName("Data")
    private List<Data> data;

    @Override
    public Data getData() {
        return data.size() > 0 ? data.get(0) : null;
    }

    public class Data {

        private String _id;

        private String analysis_fail;
        private String analysis_fail_reason;
        private int average_bathroom_duration;
        private Date date_of_analysis;

        private int day_min_bathroom_duration;
        private int day_average_bathroom_duration;
        private int day_max_bathroom_duration;

        private int day_min_bathroom_frequency;
        private int day_median_bathroom_frequency;
        private int day_max_bathroom_frequency;

        private int night_min_bathroom_duration;
        private int night_average_bathroom_duration;
        private int night_max_bathroom_duration;

        private int night_min_bathroom_frequency;
        private int night_median_bathroom_frequency;
        private int night_max_bathroom_frequency;

        private int max_bathroom_duration;
        private int min_bathroom_duration;

        public int getDayMedianBathroomFrequency() {
            return day_median_bathroom_frequency;
        }

        public int getDayMinDuration() {
            return day_min_bathroom_duration / 60;
        }

        public int getDayAverageDuration() {
            return day_average_bathroom_duration / 60;
        }

        public int getDayMaxDuration() {
            return day_max_bathroom_duration / 60;
        }

        public int getNightMedianBathroomFrequency() {
            return night_median_bathroom_frequency;
        }

        public int getNightMinDuration() {
            return night_min_bathroom_duration / 60;
        }

        public int getNightAverageDuration() {
            return night_average_bathroom_duration / 60;
        }

        public int getNightMaxDuration() {
            return night_max_bathroom_duration / 60;
        }

    }
}
