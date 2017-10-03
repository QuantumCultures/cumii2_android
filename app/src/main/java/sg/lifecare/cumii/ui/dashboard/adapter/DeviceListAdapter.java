package sg.lifecare.cumii.ui.dashboard.adapter;

import android.content.Context;
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
import timber.log.Timber;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<Item> mItems = new ArrayList<>();

    private OnItemClickListener mListener;
    private Context mContext;

    public DeviceListAdapter(Context context, OnItemClickListener listener) {
        mListener = listener;
        mContext = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == Item.GATEWAY) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_gateway, parent, false);

            return new GatewayViewHolder(itemView);
        } else if (viewType == Item.DEVICE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_smart_device, parent, false);

            return new DeviceViewHolder(itemView);
        } else if (viewType == Item.HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_device_title, parent, false);

            return new HeaderViewHolder(itemView);
        } else if (viewType == Item.MESSAGE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_device_message, parent, false);

            return new MessageViewHolder(itemView);
        }
        
        return null;
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

    public void replaceDevices(List<ConnectedDeviceResponse.Data> gateways,
            List<ConnectedDeviceResponse.Data> devices) {
        mItems.clear();

        mItems.add(new Header(mContext.getString(R.string.label_gateway)));
        if (gateways.size() > 0) {
            for (ConnectedDeviceResponse.Data gateway : gateways) {
                mItems.add(new Gateway(gateway));
            }
        } else {
            mItems.add(new Message(mContext.getString(R.string.error_no_gateways)));
        }

        mItems.add(new Header(mContext.getString(R.string.label_smart_device)));
        if (devices.size() > 0) {
            for (ConnectedDeviceResponse.Data device : devices) {
                mItems.add(new Device(device));
            }
        } else {
            mItems.add(new Message(mContext.getString(R.string.error_no_smart_devices)));
        }

        notifyDataSetChanged();
    }

    abstract class ItemViewHolder<T extends Item> extends RecyclerView.ViewHolder {

        ItemViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindView(T item);

    }

    class MessageViewHolder extends ItemViewHolder<Message> {

        @BindView(R.id.message_text)
        TextView mMessageText;

        MessageViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Message message) {
            mMessageText.setText(message.mMessage);
        }
    }

    class HeaderViewHolder extends ItemViewHolder<Header> {

        @BindView(R.id.title_text)
        TextView mTitleText;

        HeaderViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Header header) {
            mTitleText.setText(header.mTitle);
        }
    }

    class DeviceViewHolder extends ItemViewHolder<Device> {
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

        @Override
        void bindView(Device device) {
            String image = device.mDevice.getImageUrl();

            if (!TextUtils.isEmpty(image)) {
                Glide.with(itemView.getContext())
                        .load(image)
                        .into(mDeviceImage);
            }

            mNameText.setText(device.mDevice.getName());

            if ("N".equalsIgnoreCase(device.mDevice.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_online));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.online));
            } else if ("F".equalsIgnoreCase(device.mDevice.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_offline));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.offline));
            } else {
                mMessageText.setVisibility(View.INVISIBLE);
            }

            if (device.mDevice.getLastUpdate() != null) {
                mTimestampText.setText(DateUtils.getDisplayTimestamp(itemView.getContext(),
                        device.mDevice.getLastUpdate()));
            }

            setBattery(device.mDevice.isOnline(), device.mDevice.getValue());

            if (mListener != null) {
                //itemView.setOnClickListener(v -> mListener.onItemClick(position));
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

    class GatewayViewHolder extends ItemViewHolder<Gateway> {

        @BindView(R.id.device_image)
        ImageView mDeviceImage;

        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.message_text)
        TextView mMessageText;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        GatewayViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void bindView(Gateway gateway) {

            String image = gateway.mGateway.getImageUrl();

            if (!TextUtils.isEmpty(image)) {
                Glide.with(itemView.getContext())
                        .load(image)
                        .into(mDeviceImage);
            }

            mNameText.setText(gateway.mGateway.getName());

            if ("N".equalsIgnoreCase(gateway.mGateway.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_online));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.online));
            } else if ("F".equalsIgnoreCase(gateway.mGateway.getStatus())) {
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText(itemView.getContext().getString(R.string.label_offline));
                mMessageText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.offline));
            } else {
                mMessageText.setVisibility(View.INVISIBLE);
            }

            if (gateway.mGateway.getLastUpdate() != null) {
                mTimestampText.setText(DateUtils.getDisplayTimestamp(itemView.getContext(),
                        gateway.mGateway.getLastUpdate()));
            }

            if (mListener != null) {
                //itemView.setOnClickListener(v -> mListener.onItemClick(position));
            }
        }
    }


    class Item {
        static final int HEADER = 1;
        static final int MESSAGE = 2;
        static final int GATEWAY = 3;
        static final int DEVICE = 4;

        private int mType;

        Item(int type) {
            mType = type;
        }
    }

    class Gateway extends Item {

        ConnectedDeviceResponse.Data mGateway;

        Gateway(ConnectedDeviceResponse.Data gateway) {
            super(GATEWAY);
            mGateway = gateway;
        }
    }

    class Device extends Item {
        private ConnectedDeviceResponse.Data mDevice;

        Device(ConnectedDeviceResponse.Data device) {
            super(DEVICE);
            mDevice = device;
        }
    }

    class Header extends Item {
        private String mTitle;

        Header(String title) {
            super(HEADER);
            mTitle = title;
        }
    }

    class Message extends Item {
        private String mMessage;

        Message(String message) {
            super(MESSAGE);

            mMessage = message;
        }
    }

}
