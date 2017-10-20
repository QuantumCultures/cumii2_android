package sg.lifecare.zwave.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.zwave.ZWaveDevice;
import sg.lifecare.zwave.report.MultilevelSensorReport;
import sg.lifecare.zwave.report.multilevelsensor.AirTemperature;
import sg.lifecare.zwave.report.multilevelsensor.Humidity;
import sg.lifecare.zwave.report.multilevelsensor.Luminance;
import sg.lifecare.zwave.type.Device;
import timber.log.Timber;

public class ZWaveDeviceListAdapter extends RecyclerView.Adapter<ZWaveDeviceListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Item> mItems = new ArrayList<>();

    public ZWaveDeviceListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Item.SENSOR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.zwave_item_sensor, parent, false);

            return new SensorViewHolder(view);
        } else if (viewType == Item.ON_OFF_SWITCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.zwave_item_on_off_switch, parent, false);

            return new OnOffSwitchViewHolder(view);
        }

        return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.zwave_item_empty, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindView(mItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        Timber.d("getItemViewType: position=%d, type=%d", position, mItems.get(position).mType);
        return mItems.get(position).mType;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addAllDevices(List<ZWaveDevice> devices) {
        mItems.clear();

        for (ZWaveDevice device : devices) {
            switch (device.getDeviceType()) {
                case Device.SENSOR:
                    mItems.add(new Sensor(device));
                    break;

                case Device.ON_OFF_POWER_SWITCH:
                    mItems.add(new OnOffSwitch(device));
                    break;
            }
        }

        notifyDataSetChanged();
    }

    class Item {
        static final int EMPTY = 0;
        static final int SENSOR = 1;
        static final int ON_OFF_SWITCH = 2;

        private int mType;

        Item(int type) {
            mType = type;
        }
    }

    class Empty extends Item {
        Empty() {
            super(EMPTY);
        }
    }

    class Sensor extends Item {

        private ZWaveDevice mZWaveDevice;

        Sensor(ZWaveDevice device) {
            super(SENSOR);

            mZWaveDevice = device;
        }
    }

    class OnOffSwitch extends Item {

        private ZWaveDevice mZWaveDevice;

        OnOffSwitch(ZWaveDevice device) {
            super(ON_OFF_SWITCH);

            mZWaveDevice = device;
        }
    }

    abstract class ItemViewHolder<T extends Item> extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindView(T item);
    }

    class EmptyViewHolder extends ItemViewHolder<Empty> {

        public EmptyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Empty item) {

        }
    }

    class SensorViewHolder extends ItemViewHolder<Sensor> {

        @BindView(R.id.air_temperature_text)
        TextView mAirTemperatureText;

        @BindView(R.id.humidity_text)
        TextView mHumidityText;

        @BindView(R.id.luminance_text)
        TextView mLuminanceText;


        SensorViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Sensor item) {
            List<MultilevelSensorReport> reports = item.mZWaveDevice.getMultilevelReports();
            if (reports.size() > 0) {
                for (MultilevelSensorReport report : reports) {
                    switch (report.getSensorType()) {
                        case MultilevelSensorReport.AIR_TEMPERATURE:
                            if (report.getScaleType() == AirTemperature.FAHRENHEIT) {
                                mAirTemperatureText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_fahrenheit, report.getSensorData()));
                            } else {
                                mAirTemperatureText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_celsius, report.getSensorData()));
                            }
                            break;

                        case MultilevelSensorReport.HUMIDITY:
                            if (report.getScaleType() == Humidity.ABSOLUTE_HUMIDITY) {
                                mHumidityText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_absolute_humidity, report.getSensorData()));
                            } else {
                                mHumidityText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_percentage, report.getSensorData()));
                            }

                            break;

                        case MultilevelSensorReport.LUMINANCE:
                            if (report.getScaleType() == Luminance.LUX) {
                                mLuminanceText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_lux, report.getSensorData()));
                            } else {
                                mLuminanceText.setText(itemView.getContext().getString(
                                        R.string.zwave_format_percentage, report.getSensorData()));
                            }
                            break;
                    }
                }

            }

        }
    }

    class OnOffSwitchViewHolder extends ItemViewHolder<OnOffSwitch> {

        @BindView(R.id.on_off_text)
        TextView mOnOffText;

        OnOffSwitchViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(OnOffSwitch item) {
            mOnOffText.setText("OFF");
        }

    }
}
