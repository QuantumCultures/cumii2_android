package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class WakeupMedianResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public Data getData() {
        if ((data != null) && (data.size() > 0)) {
            return data.get(0);
        }
        return null;
    }

    public class Data {

        private String _id;
        private Date date_of_analysis;
        private String timezone_of_analysis;
        private Date median_wakeup_time;
        private String entity;
        private String analysis_fail;
        private String analysis_fail_reason;

        public Date getMedianWakeupTime() {
            return median_wakeup_time;
        }
    }
}
