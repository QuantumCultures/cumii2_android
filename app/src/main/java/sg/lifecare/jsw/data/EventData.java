package sg.lifecare.jsw.data;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class EventData {

    private int mCameraIndex;
    private boolean mIsUtcTime;
    private byte mEventEndFlag;

    private List<Event> mEvents = new ArrayList<>();

    public EventData() {
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    public void setCameraIndex(int cameraIndex) {
        mCameraIndex = cameraIndex;
    }

    public void setUtcTime(boolean isUtcTime) {
        mIsUtcTime = isUtcTime;
    }

    public void setEventEndFlag(byte eventEndFlag) {
        mEventEndFlag = eventEndFlag;
    }

    public void addEvent(int year, int month, int day, int hour, int minute, byte type, byte status) {
        Event event = new Event();
        event.mDate = new DateTime(year, month, day, hour, minute);
        event.mType = type;
        event.mStatus = status;

        mEvents.add(event);
    }

    public class Event {
        private DateTime mDate;
        private byte mType;
        private byte mStatus;

        public DateTime getTimestamp() {
            return mDate;
        }

        public byte getType() {
            return mType;
        }

        public byte getStatus() {
            return mStatus;
        }
    }

}
