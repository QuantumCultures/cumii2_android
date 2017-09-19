package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WaterConsumptionResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data {
        private List<Float> hour_consumption;
        private List<Float> hour_consumption_cost;
        private List<Float> hour_accumulated_consumption;
        private List<Float> hour_accumulated_consumption_cost;
        private List<Float> day_total_consumption;
        private List<Float> day_total_consumption_cost;
        private List<Float> week_total_consumption;
        private List<Float> week_total_consumption_cost;
        private List<Float> month_total_consumption;
        private List<Float> month_total_consumption_cost;
        private String cost_unit;
        private String analysis_fail;
        private String analysis_fail_reason;
    }
}
