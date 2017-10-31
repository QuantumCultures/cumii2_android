package sg.lifecare.zwave.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.zwave.ZWaveDevice;
import sg.lifecare.zwave.control.Security;
import sg.lifecare.zwave.report.AlarmReport;
import sg.lifecare.zwave.report.BasicReport;
import sg.lifecare.zwave.report.BinarySwitchReport;
import sg.lifecare.zwave.report.MeterReport;
import sg.lifecare.zwave.report.MultilevelSensorReport;
import sg.lifecare.zwave.report.multilevelsensor.AirTemperature;
import sg.lifecare.zwave.report.multilevelsensor.Humidity;
import sg.lifecare.zwave.report.multilevelsensor.Luminance;
import sg.lifecare.zwave.type.Device;
import timber.log.Timber;

public class ZWaveDeviceListAdapter extends RecyclerView.Adapter<ZWaveDeviceListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Item> mItems = new ArrayList<>();

    private OnOffSwitchListener mOnOffSwitchListener;
    private SecurityListener mSecurityListener;

    public ZWaveDeviceListAdapter(Context context) {
        mContext = context;
        mItems.add(new SecurityItem(null));
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
        } else if (viewType == Item.SECURITY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.zwave_item_security, parent, false);

            return new SecurityViewHolder(view);
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

    public void setOnOffSwitchListener(OnOffSwitchListener listener) {
        mOnOffSwitchListener = listener;
    }

    public void setSecurityListener(SecurityListener listener) {
        mSecurityListener = listener;
    }

    public void addSecurity(sg.lifecare.zwave.control.Security security) {
        Timber.d("addSecurity");

        if (mItems.size() > 0) {
            mItems.remove(0);
            mItems.add(0, new SecurityItem(security));
        }
        notifyDataSetChanged();
    }

    public void addAllDevices(List<ZWaveDevice> devices) {
        // need to keep the first item
        if (mItems.size() > 1) {
            Item item = mItems.get(0);
            mItems.clear();
            mItems.add(item);
        }

        for (ZWaveDevice device : devices) {
            switch (device.getDeviceType()) {
                case Device.SENSOR:
                    mItems.add(new SensorItem(device));
                    break;

                case Device.ON_OFF_POWER_SWITCH:
                    mItems.add(new OnOffSwitchItem(device));
                    break;
            }
        }

        notifyDataSetChanged();
    }

    class Item {
        static final int EMPTY = 0;
        static final int SENSOR = 1;
        static final int ON_OFF_SWITCH = 2;
        static final int SECURITY = 3;

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

    class SensorItem extends Item {

        private ZWaveDevice mZWaveDevice;

        SensorItem(ZWaveDevice device) {
            super(SENSOR);

            mZWaveDevice = device;
        }
    }

    class OnOffSwitchItem extends Item {

        private ZWaveDevice mZWaveDevice;

        OnOffSwitchItem(ZWaveDevice device) {
            super(ON_OFF_SWITCH);

            mZWaveDevice = device;
        }
    }

    class SecurityItem extends Item {

        private Security mSecurity;

        SecurityItem(Security security) {
            super(SECURITY);

            mSecurity = security;
        }
    }

    abstract class ItemViewHolder<T extends Item> extends RecyclerView.ViewHolder {

        ItemViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindView(T item);
    }

    class EmptyViewHolder extends ItemViewHolder<Empty> {

        EmptyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Empty item) {

        }
    }

    class SensorViewHolder extends ItemViewHolder<SensorItem> {

        @BindView(R.id.sensor_frame)
        View mSensorFrame;

        @BindView(R.id.device_name_text)
        TextView mDeviceNameText;

        @BindView(R.id.no_data_label)
        TextView mNoDataLabel;

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
        void bindView(SensorItem item) {
            List<MultilevelSensorReport> multilevelSensorReports = item.mZWaveDevice.getMultilevelSensorReports();
            List<AlarmReport> alarmReports = item.mZWaveDevice.getAlarmReports();
            String name = item.mZWaveDevice.getName();

            if (!TextUtils.isEmpty(name)) {
                mDeviceNameText.setText(name);
            }

            mSensorFrame.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));

            if ((alarmReports != null) && (alarmReports.size() > 0)) {
                for (AlarmReport report : alarmReports) {
                    if (report.getAlarmType() == AlarmReport.BURGLAR_ALARM) {
                        if (report.getAlarmLevel() == AlarmReport.ACTIVATE_ALARM) {
                            mSensorFrame.setBackgroundColor(
                                    ContextCompat.getColor(itemView.getContext(), R.color.zwave_alarm_on));
                        } else if (report.getAlarmLevel() == AlarmReport.DEACTIVATE_ALARM) {
                            mSensorFrame.setBackgroundColor(
                                    ContextCompat.getColor(itemView.getContext(), R.color.zwave_alarm_off));
                        }
                    }
                }
            }

            if ((multilevelSensorReports != null) && (multilevelSensorReports.size() > 0)) {
                mNoDataLabel.setVisibility(View.INVISIBLE);
                mAirTemperatureText.setVisibility(View.VISIBLE);
                mLuminanceText.setVisibility(View.VISIBLE);
                mHumidityText.setVisibility(View.VISIBLE);

                for (MultilevelSensorReport report : multilevelSensorReports) {
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

            } else {
                mNoDataLabel.setVisibility(View.VISIBLE);
                mAirTemperatureText.setVisibility(View.INVISIBLE);
                mLuminanceText.setVisibility(View.INVISIBLE);
                mHumidityText.setVisibility(View.INVISIBLE);
            }

        }
    }

    class OnOffSwitchViewHolder extends ItemViewHolder<OnOffSwitchItem> {

        @BindView(R.id.device_name_text)
        TextView mDeviceNameText;

        @BindView(R.id.on_off_text)
        TextView mOnOffText;

        @BindView(R.id.data_text)
        TextView mDataText;

        OnOffSwitchViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(OnOffSwitchItem item) {


            mOnOffText.setText(itemView.getContext().getString(R.string.zwave_label_no_data));

            BinarySwitchReport binarySwitchReport = item.mZWaveDevice.getBinarySwitchReport();
            BasicReport basicReport = item.mZWaveDevice.getBasicReport();
            List<MeterReport> meterReports = item.mZWaveDevice.getMeterReports();
            String name = item.mZWaveDevice.getName();

            if (!TextUtils.isEmpty(name)) {
                mDeviceNameText.setText(name);
            }

            if ((binarySwitchReport != null) || (basicReport != null)) {
                boolean isOn = false;

                if (basicReport != null) {
                    if (basicReport.getValue() > 0) {
                        isOn = true;
                    }
                }

                if (binarySwitchReport != null) {
                    if (binarySwitchReport.isOn()) {
                        isOn = true;
                    }
                }

                if (isOn) {
                    mOnOffText.setText(
                            itemView.getContext().getString(R.string.zwave_label_on));
                    mOnOffText.setTextColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.zwave_on));
                } else {
                    mOnOffText.setText(
                            itemView.getContext().getString(R.string.zwave_label_off));
                    mOnOffText.setTextColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.zwave_off));
                }

                final boolean tempOn = isOn;

                mOnOffText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnOffSwitchListener != null) {
                            mOnOffSwitchListener.onOnOffClick(item.mZWaveDevice, !tempOn);
                        }
                    }
                });
            }

            if ((meterReports != null) && (meterReports.size() > 0)) {

                String data =  "";

                for (MeterReport meterReport : meterReports) {
                    Timber.d("meter type = %d", meterReport.getMeterType());
                    if (meterReport.getMeterType() == MeterReport.ELECTRIC) {
                        String unit = MeterReport.getScaleUnit(itemView.getContext(),
                                meterReport.getMeterType(), meterReport.getScale());
                        double value = meterReport.getMeterValue();

                        Timber.d("%.1f %s  ", value, unit);

                        String newData = String.format(Locale.getDefault(), " %.2f %s ", value, unit);

                        data = data + newData;
                    }
                }

                mDataText.setText(data);
            } else {
                mDataText.setVisibility(View.GONE);
            }


        }
    }

    class SecurityViewHolder extends ItemViewHolder<SecurityItem> {

        @BindView(R.id.arm_button)
        AppCompatButton mArmButton;

        @BindView(R.id.home_button)
        AppCompatButton mHomeButton;

        @BindView(R.id.disarm_button)
        AppCompatButton mDisarmButton;

        SecurityViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(SecurityItem item) {

            mArmButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_inactive));
            mDisarmButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_inactive));
            mHomeButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_inactive));

            if (item.mSecurity != null) {
                switch (item.mSecurity.getStatus()) {
                    case Security.ARM:
                        mArmButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_active));
                        break;

                    case Security.ARM_PARTIAL:
                        mHomeButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_active));
                        break;

                    case Security.DISARM:
                        mDisarmButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.zwave_active));
                        break;
                }
            }


            if (mSecurityListener != null) {
                mArmButton.setOnClickListener(view -> mSecurityListener.onArmClick());

                mHomeButton.setOnClickListener(view -> mSecurityListener.onHomeClick());

                mDisarmButton.setOnClickListener(view -> mSecurityListener.onDisarmClick());
            }
        }
    }

    public interface OnOffSwitchListener {
        void onOnOffClick(ZWaveDevice device, boolean isOn);
    }

    public interface SecurityListener {
        void onArmClick();
        void onDisarmClick();
        void onHomeClick();
    }
}
