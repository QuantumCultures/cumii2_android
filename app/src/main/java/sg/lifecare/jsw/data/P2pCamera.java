package sg.lifecare.jsw.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jsw.sdk.general.Packet;
import com.jsw.sdk.p2p.device.IAVListener;
import com.jsw.sdk.p2p.device.IRecvIOCtrlListener;
import com.jsw.sdk.p2p.device.P2PDev;
import com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto;
import com.jsw.sdk.p2p.device.extend.Ex_AvEvent_t;
import com.jsw.sdk.p2p.device.extend.Ex_DayTime_t;
import com.jsw.sdk.p2p.device.extend.Ex_IOCTRLListEventResp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sg.lifecare.cumii.util.HexUtil;
import sg.lifecare.jsw.util.JswUtil;
import timber.log.Timber;

public class P2pCamera {

    private P2PDev mP2pDev;
    private final int mPosition;

    private EventData mEventData = new EventData();

    private P2pDevResponseHandler mP2pDevResponseHandler;
    private IoCtrlHandler mIoCtrlHandler;

    private P2pCameraListener mP2pCameraListener;
    private List<P2pCameraEventListener> mP2pCameraEventListener = new ArrayList<>();

    public P2pCamera(P2PDev p2pDev, Activity activity, int position) {
        mP2pDev = p2pDev;
        mPosition = position;

        mP2pDevResponseHandler = new P2pDevResponseHandler(activity, this);
        mIoCtrlHandler = new IoCtrlHandler(activity, this);
    }

    public int getPosition() {
        return mPosition;
    }

    public P2PDev getP2pDev() {
        return mP2pDev;
    }

    public void setListener(P2pCameraListener listener) {
        mP2pCameraListener = listener;
    }

    public void removeListener() {
        mP2pCameraListener = null;
    }

    public void addEventListener(P2pCameraEventListener listener) {
        mP2pCameraEventListener.add(listener);
    }

    public void removeEventListener(P2pCameraEventListener listener) {
        if (mP2pCameraEventListener.size() > 0) {
            for (int i = mP2pCameraEventListener.size() - 1; i >= 0; i--) {
                if (listener == mP2pCameraEventListener.get(i)) {
                    mP2pCameraEventListener.remove(i);
                }
            }
        }
    }


    public void connect(int delay) {
        Timber.d("connect");
        mP2pDev.setEnterLiveView(false);
        mP2pDev.startConn(delay);
    }

    public void pause() {
        mP2pDev.unregAVListener(mIAVListener);
        mP2pDev.unregRecvIOCtrlListener(mIRecvIOCtrlListener);
    }

    public void resume() {
        mP2pDev.regRecvIOCtrlListener(mIRecvIOCtrlListener);
        mP2pDev.regAVListener(mIAVListener);
    }

    public void stop() {
        mP2pDev.unregAVListener(mIAVListener);
        mP2pDev.unregRecvIOCtrlListener(mIRecvIOCtrlListener);
        mP2pDev.stopConn(true);
    }

    public void clearEventData() {
        mEventData = new EventData();
    }

    public EventData getEventData() {
        return mEventData;
    }

    private boolean isP2pDevResponse(int code) {
        return ((code >= 5000) && (code <= 5217));
    }

    private boolean isIoCtrlResponse(int code) {
        return ((code >= 0) && (code <= 111));
    }

    private IRecvIOCtrlListener mIRecvIOCtrlListener = new IRecvIOCtrlListener() {
        @Override
        public void onRecvIOCtrlData(int code, Object p2pDevObject, byte[] data) {
            Timber.d("onRecvIOCtrlData: code = %d", code);

            if (isP2pDevResponse(code)) {
                Message msg = new Message();
                msg.what = code;
                msg.obj = p2pDevObject;

                Bundle bundle = new Bundle();
                bundle.putByteArray("data", data);

                msg.setData(bundle);

                mP2pDevResponseHandler.sendMessage(msg);

            } else if (isIoCtrlResponse(code)) {
                Message msg = new Message();
                msg.what = code;
                msg.obj = p2pDevObject;

                Bundle bundle = new Bundle();
                bundle.putByteArray("data", data);

                msg.setData(bundle);

                mIoCtrlHandler.sendMessage(msg);
            } else {
                Timber.w("onRecvIOCtrlData: unsupported code");
            }
        }
    };

    private IAVListener mIAVListener = new IAVListener() {
        @Override
        public void updateVFrame(Bitmap bitmap, Object o) {
            Timber.d("updateVFrame");
        }

        @Override
        public void updateAVInfo(int code, Object p2pDevObject, int value1, int value2) {
            Timber.d("updateAVInfo: code - %d (%s)", code, JswUtil.getP2pDevResponseString(code));
            if (isP2pDevResponse(code)) {
                Message msg = new Message();
                msg.what = code;
                msg.obj = p2pDevObject;
                msg.arg1 = value1;
                msg.arg2 = value2;

                Bundle bundle = new Bundle();
                bundle.putByteArray("data", new byte[0]);

                msg.setData(bundle);

                mP2pDevResponseHandler.sendMessage(msg);
            }
        }

        @Override
        public void updateAVInfo2(int code, Object p2pDevObject, int errorCode, int value) {
            Timber.d("updateAVInfo2: code - %d (%s)", code, JswUtil.getP2pDevResponseString(code));

            if (isP2pDevResponse(code)) {
                P2PDev p2PDev = null;

                if (p2pDevObject instanceof P2PDev) {
                    p2PDev = (P2PDev) p2pDevObject;
                }

                switch (code) {
                    case P2PDev.STATUS_INFO_REMOTE_RECORDING:
                        break;
                }

            }
        }
    };

    private static class P2pDevResponseHandler extends Handler {

        private final WeakReference<Activity> mActivity;
        private final WeakReference<P2pCamera> mP2pCamera;

        P2pDevResponseHandler(Activity activity, P2pCamera p2pCamera) {
            mActivity = new WeakReference<>(activity);
            mP2pCamera = new WeakReference<>(p2pCamera);
        }

        public void handleMessage(Message msg) {
            Timber.d("P2pDevResponseHandler: %s", JswUtil.getP2pDevResponseString(msg.what));

            Activity activity = mActivity.get();
            P2pCamera p2pCamera = mP2pCamera.get();

            if (activity == null) {
                return;
            }

            P2PDev p2PDev = null;
            int code = msg.what;

            if (msg.obj instanceof P2PDev) {
                p2PDev = (P2PDev) msg.obj;
            }

            if (p2PDev == null) {
                Timber.w("P2PDev is null");
                return;
            }

            switch (code) {

                case P2PDev.STATUS_INFO_AV_ONLINENUM:
                    Timber.d("online number = %d, connection info = %d", msg.arg1, msg.arg2);
                    break;

                case P2PDev.STATUS_INFO_AV_RESOLUTION:
                    Timber.d("width = %d, height = %d", msg.arg1, msg.arg2);
                    break;

                case P2PDev.STATUS_INFO_GET_FRAME:
                    // add frame count
                    Timber.d("decoded output count = %d", msg.arg2);
                    break;

                case P2PDev.OM_GET_ONE_PIC_FROM_STREAM:
                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onSnapshotUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.OM_GET_ONE_PIC_TIMEOUT:
                    break;

                case P2PDev.CONN_INFO_CONNECTED:
                    if (p2PDev.getWaitApConnected()) {
                        p2PDev.setWaitApConnected(false);

                        // TODO: stop reconnect timer
                    }

                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_SESSION_CLOSED:
                    Timber.d("disconnect %s with connection info %d", p2PDev.getDev_id1(), p2PDev.getConnInfo());

                    if (P2PDev.CONN_INFO_CONNECT_WRONG_PWD == p2PDev.getConnInfo()) {
                        break;
                    }

                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_CONNECT_WRONG_PWD:
                    // TODO: stop connection

                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_CONNECTING:
                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_CONNECT_FAIL:
                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_CONNECT_WRONG_DID:
                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    if (p2pCamera.mP2pCameraListener != null) {
                        p2pCamera.mP2pCameraListener.onConnectionStatusUpdate(p2pCamera);
                    }
                    break;

                case P2PDev.CONN_INFO_CONNECTE_COUNTDOWN:
                    p2PDev.stopConn(true);
                    p2PDev.setWaitApConnected(true);

                    // TODO: add timer
                    break;

            }
        }
    }

    private static class IoCtrlHandler extends Handler {

        private final WeakReference<Activity> mActivity;
        private final WeakReference<P2pCamera> mP2pCamera;

        IoCtrlHandler(Activity activity, P2pCamera p2pCamera) {
            mActivity = new WeakReference<>(activity);
            mP2pCamera = new WeakReference<>(p2pCamera);
        }

        public void handleMessage(Message msg) {

            Timber.d("IoCtrlHandler: %s", JswUtil.getIoctlResponseString(msg.what));
            Activity activity = mActivity.get();
            P2pCamera p2pCamera = mP2pCamera.get();

            if (activity == null) {
                return;
            }

            P2PDev p2pDev = null;
            int code = msg.what;
            byte[] data = msg.getData().getByteArray("data");

            if (msg.obj instanceof P2PDev) {
                p2pDev = (P2PDev) msg.obj;
            }

            if (p2pDev == null) {
                Timber.w("P2PDev is null");
                return;
            }


            switch (code) {
                case AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_ON_OFF_VALUE_RESP:
                    Timber.d("%s: email_alert=%b, event_notify=%b, manual_record_status=%b, auto_delete_record=%b",
                            p2pDev.getDev_id1(), p2pDev.isEmailAlert(), p2pDev.isEventNotify(),
                            p2pDev.isManualRecordSupported(), p2pDev.isAutoDelRec());
                    break;

                case AUTH_AV_IO_Proto.IOCTRL_TYPE_DEVINFO_RESP:
                    Timber.d("%s: model_of_device_info=%s, model=%d",
                            p2pDev.getDev_id1(), p2pDev.getModelOfDevInfo(), p2pDev.getParam().getModel());
                    // TODO: update camera to database
                    // TODO: refresh list
                    //activity.displayVideo(p2PDev);

                    if (p2pDev.isConnected()) {
                        Timber.d("test here");
                        p2pDev.changeDateTime();
                        p2pDev.startGetOnePicFromRealStream();
                    }
                    break;

                case AUTH_AV_IO_Proto.IOCTRL_TYPE_SETPASSWORD_RESP:
                    if (data == null) {
                        break;
                    }

                    if (data[0] == 0) {
                        // TODO: set password to database
                    } else {
                        // TODO: show alert fail to show password
                    }
                    break;

                case AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTEVENT_RESP:
                    parseEventList(p2pCamera.mEventData, data);

                    if (p2pCamera.mP2pCameraEventListener != null) {
                        for (P2pCameraEventListener listener : p2pCamera.mP2pCameraEventListener) {
                            listener.onEventDataUpdate(p2pCamera);
                        }
                    }
                    break;
            }
        }

        private void parseEventList(EventData eventData, byte[] data) {
            if ((data == null) || (data.length < Ex_IOCTRLListEventResp.LEN_HEAD)) {
                Timber.w("parseEventList: invalid date");
                return;
            }

            Timber.d("parseEventList:]\n%s", HexUtil.getHexStringFromByteArray(data));



            int cameraIndex = Packet.byteArrayToInt_Little(data, 0);
            byte eventEndFlag = data[9];

            int count = data[10] & 0xff;
            boolean isUtcTime = (data[11]&0xff) == 1;

            Timber.d("parseEventList: camera_index=%d, count=%d, isUtcTime=%b, endFlag=%d",
                    cameraIndex, count, isUtcTime, eventEndFlag);

            eventData.setCameraIndex(cameraIndex);
            eventData.setEventEndFlag(eventEndFlag);
            eventData.setUtcTime(isUtcTime);

            // Ex_IOCTRLListEventResp.LEN_HEAD = 12
            // Ex_AvEvent_t.LEN_HEAD = 12
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    byte[] tempData = new byte[8];
                    System.arraycopy(data, i * Ex_AvEvent_t.LEN_HEAD + Ex_IOCTRLListEventResp.LEN_HEAD, tempData, 0, 8);
                    Ex_DayTime_t time = new Ex_DayTime_t(tempData);

                    time.month -= 1;
                    byte event = data[i * Ex_AvEvent_t.LEN_HEAD + Ex_IOCTRLListEventResp.LEN_HEAD + 8];
                    byte status = data[i * Ex_AvEvent_t.LEN_HEAD + Ex_IOCTRLListEventResp.LEN_HEAD + 9];

                    Timber.d("parseEventList: event=%d, status=%d, time=%d-%d-%d %d:%02d",
                            event, status, time.day, time.month + 1, time.year, time.hour, time.minute);

                    eventData.addEvent(time.year, time.month, time.day, time.hour, time.minute, event, status);
                }
            }
        }
    }


    public interface P2pVideoListener {
        void updateVFrame(Bitmap bitmap, Object p2pDevObject);
        void updateAVInfo(int code, Object p2pDevObject, int value1, int value2);
        void updateAVInfo2(int code, Object p2pDeviceObject, int errorCode, int value);
    }

    public interface P2pCameraListener {
        void onConnectionStatusUpdate(P2pCamera p2pCamera);
        void onSnapshotUpdate(P2pCamera p2pCamera);
    }

    public interface P2pCameraEventListener {
        void onEventDataUpdate(P2pCamera p2pCamera);
    }
}
