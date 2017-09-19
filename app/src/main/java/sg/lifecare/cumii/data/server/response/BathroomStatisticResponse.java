package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class BathroomStatisticResponse extends Response {


    @SerializedName("Data")
    private List<Data> data;

    @Override
    public Object getData() {
        return data;
    }

    public class Data {

        private String _id;

        private String analysis_fail;
        private String analysis_fail_reason;
        private int average_bathroom_duration;
        private Date date_of_analysis;
        private int day_average_bathroom_duration;
        private int day_average_bathroom_frequency;
        private int day_max_bathroom_duration;
        private int day_max_bathroom_frequency;
        private int day_min_bathroom_duration;
        private int day_min_bathroom_frequency;
        private int night_average_bathroom_duration;
        private int night_average_bathroom_frequency;
        private int night_max_bathroom_duration;
        private int night_max_bathroom_frequency;
        private int night_min_bathroom_duration;
        private int night_min_bathroom_frequency;
        private int max_bathroom_duration;
        private int min_bathroom_duration;

    }
}
