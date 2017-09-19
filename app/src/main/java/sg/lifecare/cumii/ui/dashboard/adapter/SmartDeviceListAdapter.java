package sg.lifecare.cumii.ui.dashboard.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.server.response.ConnectedDeviceResponse;
import sg.lifecare.cumii.util.DateUtils;

public class SmartDeviceListAdapter extends RecyclerView.Adapter<SmartDeviceListAdapter.DeviceViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<ConnectedDeviceResponse.Data> mSmartDevices = new ArrayList<>();

    private OnItemClickListener mListener;

    public SmartDeviceListAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_smart_device, parent, false);

        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mSmartDevices.size();
    }

    public void replaceSmartDevices(List<ConnectedDeviceResponse.Data> gateways) {
        mSmartDevices.clear();

        if (gateways != null) {
            mSmartDevices.addAll(gateways);
        }

        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_image)
        ImageView mDeviceImage;

        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.message_text)
        TextView mMessageText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        @BindView(R.id.battery_image)
        ImageView mBatteryImage;

        @BindView(R.id.battery_text)
        TextView mBatteryText;

        @BindView(R.id.battery_frame)
        View mBatteryFrame;

        DeviceViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            ConnectedDeviceResponse.Data smartDevice = mSmartDevices.get(position);

            String image = smartDevice.getImageUrl();

            if (!TextUtils.isEmpty(image)) {
                Glide.with(itemView.getContext())
                        .load(image)
                        .into(mDeviceImage);
            }

            mNameText.setText(smartDevice.getName());

            if ("N".equalsIgnoreCase(smartDevice.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_online));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.online));
            } else if ("F".equalsIgnoreCase(smartDevice.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_offline));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.offline));
            } else {
                mMessageText.setVisibility(View.INVISIBLE);
            }

            if (smartDevice.getLastUpdate() != null) {
                mTimestampText.setText(DateUtils.getDisplayTimestamp(itemView.getContext(),
                        smartDevice.getLastUpdate()));
            }

            setBattery(smartDevice.isOnline(), smartDevice.getValue());

            if (mListener != null) {
                itemView.setOnClickListener(v -> mListener.onItemClick(position));
            }
        }

        private void setBattery(boolean isOnline, int batteryLevel) {

            mBatteryFrame.setVisibility(View.INVISIBLE);

            if (batteryLevel == -1) {

            } else {
                mBatteryFrame.setVisibility(View.VISIBLE);

                mBatteryText.setText(batteryLevel + "%");

                if (isOnline) {
                    if (batteryLevel >= 90) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_on100);
                        mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                                R.color.batt_green));
                    } else if (batteryLevel >= 65) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_on75);
                        mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                                R.color.batt_green));
                    } else if (batteryLevel >= 31) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_on50);
                        mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                                R.color.batt_orange));
                    } else if (batteryLevel >= 10) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_on25);
                        mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                                R.color.batt_red));
                    } else {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_on0);
                        mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                                R.color.batt_red));
                    }
                } else {
                    mBatteryText.setTextColor(ContextCompat.getColor(itemView.getContext(),
                            R.color.batt_grey));

                    if (batteryLevel >= 90) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_off100);
                    } else if (batteryLevel >= 65) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_off75);
                    } else if (batteryLevel >= 31) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_off50);
                    } else if (batteryLevel >= 10) {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_off25);
                    } else {
                        mBatteryImage.setImageResource(R.drawable.ic_battery_off0);
                    }
                }

            }

        }
    }
}
