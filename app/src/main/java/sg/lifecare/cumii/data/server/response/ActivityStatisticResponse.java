package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ActivityStatisticResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data {

        private String _id;

        private String analysis_fail;
        private String analysis_fail_reason;
        private int average_activity_duration;
        private Date date_of_analysis;
        private int day_average_activity_duration;
        private int day_averate_inactivity_duration;
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


    }
}
