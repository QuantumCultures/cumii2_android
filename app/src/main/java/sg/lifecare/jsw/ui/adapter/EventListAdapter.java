package sg.lifecare.jsw.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.EventData;
import timber.log.Timber;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private EventData mEventData;


    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jsw_item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if ((mEventData != null) && (mEventData.getEvents() != null)) {
            return mEventData.getEvents().size();
        }

        return 0;
    }

    public void replaceEventData(EventData eventData) {
        mEventData = eventData;

        if (mEventData.getEvents() != null) {
            Timber.d("replaceEventData: %d", mEventData.getEvents().size());
        }

        notifyDataSetChanged();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_text)
        TextView mNameText;

        private final DateTimeFormatter mDateTimeFormatter;

        EventViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mDateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd hh:mm:ss");
        }

        void bindView(int position) {

            EventData.Event event = mEventData.getEvents().get(position);

            String timestamp = mDateTimeFormatter.print(event.getTimestamp());

            mNameText.setText(timestamp);

        }
    }
}
