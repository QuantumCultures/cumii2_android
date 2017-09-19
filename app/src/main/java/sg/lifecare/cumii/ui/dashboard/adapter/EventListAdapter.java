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
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.ActivityDataResponse;
import sg.lifecare.cumii.util.DateUtils;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onLastItemVisible();
    }

    private ArrayList<ActivityDataResponse.Data> mEvents = new ArrayList<>();

    private EventListAdapter.OnItemClickListener mListener;

    public EventListAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_event, parent, false);


        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bindView(position);

        if ((mEvents.size() - 1) == position) {
            if (mListener != null) {
                mListener.onLastItemVisible();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void replaceEvents(List<ActivityDataResponse.Data> events) {
        mEvents.clear();

        if (events != null) {
            mEvents.addAll(events);
        }

        notifyDataSetChanged();
    }

    public void addEvents(List<ActivityDataResponse.Data> events) {
        mEvents.addAll(events);

        notifyDataSetChanged();
    }

    public ActivityDataResponse.Data getLastEvent() {
        if (mEvents.size() > 0) {
            return mEvents.get(mEvents.size() - 1);
        }

        return null;
    }


    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_text)
        TextView mMessaeText;

        @BindView(R.id.event_image)
        ImageView mEventImage;

        @BindView(R.id.timestamp_text)
        TextView mTimestampText;

        EventViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            ActivityDataResponse.Data event = mEvents.get(position);

            mMessaeText.setText(event.getMessage());
            mTimestampText.setText(DateUtils.getDisplayTimestamp(itemView.getContext(), event.getCreateDate()));

            String type = event.getType();
            if (TextUtils.isEmpty(type)) {
                mEventImage.setImageResource(R.drawable.ic_s);
                mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));
            } else {
                CumiiUtil.EventType eventType = CumiiUtil.getEventType(type);

                switch (eventType) {
                    case SWITCH:
                        mEventImage.setImageResource(R.drawable.ic_sw);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));
                        break;

                    case PANIC:
                        mEventImage.setImageResource(R.drawable.ic_p);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                        break;

                    case DOOR:
                    case UTILITY:
                    case SECURITY:
                    case BATT:
                        mEventImage.setImageResource(R.drawable.ic_s);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));
                        break;

                    case MEASUREMENT:
                        mEventImage.setImageResource(R.drawable.ic_v);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));
                        break;

                    case ALERT:
                        mEventImage.setImageResource(R.drawable.ic_a);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));

                    default:
                        mEventImage.setImageResource(R.drawable.ic_s);
                        mMessaeText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_87));
                        mTimestampText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black_54));
                        break;
                }
            }

            if (mListener != null) {
                itemView.setOnClickListener(v -> mListener.onItemClick(position));
            }
        }
    }
}
