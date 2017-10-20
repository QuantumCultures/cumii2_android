package sg.lifecare.jsw.ui;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.jsw.sdk.decoder.HwH264Decoder;
import com.jsw.sdk.general.DisplayInformation;
import com.jsw.sdk.p2p.device.IAVListener;
import com.jsw.sdk.p2p.device.IRecvIOCtrlListener;
import com.jsw.sdk.p2p.device.P2PDev;
import com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto;
import com.jsw.sdk.p2p.device.extend.Ex_IOCTRLAVStream;
import com.jsw.sdk.p2p.device.extend.Ex_IOCTRLGetVideoParameterResp;
import com.jsw.sdk.ui.TouchedTextureView;
import com.jsw.sdk.ui.TouchedView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.CameraData;
import sg.lifecare.jsw.util.JswUtil;
import timber.log.Timber;

public class DashboardActivity1 extends AppCompatActivity implements IRecvIOCtrlListener,
        IAVListener {

    private List<P2PDev> mP2PDevices = new ArrayList<>();
    private CameraData mCameraData;
    private P2PDev mP2PDevice;

    private static final String TEST_DID = "CGXX-000886-DHLRP";
    private static final String TEST_MODEL = "WAPP-ESR";
    private static final String TEST_PASSWORD = "0000";

    //private static final String TEST_DID = "DGXX-000222-FPFLP";
    //private static final String TEST_MODEL = "RVDP-DSI";
    //private static final String TEST_PASSWORD = "12345678";

    private int mScreenWidth;
    private int mScreenHeight;

    @BindView(R.id.video_hardware_view)
    TouchedTextureView mVideoHardwareView;

    @BindView(R.id.video_view)
    TouchedView mVideoView;

    private P2pDevResponseHandler mP2pDevResponseHandler;
    private IoCtrlHandler mIoCtrlHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jsw_activity_dashboard1);

        ButterKnife.bind(this);

        mP2pDevResponseHandler = new P2pDevResponseHandler(this);
        mIoCtrlHandler = new IoCtrlHandler(this);

        HwH264Decoder.setHardwareDecode(true);

        P2PDev.setNetworkAvailable(true);
        P2PDev.initAll();



        mCameraData = CameraData.getInstance(this);
        mCameraData.clear();

    }

    private void initScreenInfo() {
        DisplayMetrics displayMetrics = DisplayInformation.geDisplayMetrics(this);
        boolean isPotrait = DisplayInformation.isPortrait(this);

        mScreenWidth = isPotrait ? displayMetrics.widthPixels : displayMetrics.heightPixels;
        mScreenHeight = isPotrait ? displayMetrics.heightPixels : displayMetrics.widthPixels;
    }

    @OnClick(R.id.add_camera_button)
    public void onAddCameraClick() {

        Timber.d("onAddCameraClick: DID %s", JswUtil.getFormatedDeviceId(TEST_DID.getBytes()));

        mP2PDevice = P2PDev.getP2PDev(TEST_DID);
        mP2PDevice.setDev_id1(TEST_DID);
        mP2PDevice.setView_pwd(TEST_PASSWORD);

        mCameraData.addCamera("Camera 1", mP2PDevice.getDev_id1(), mP2PDevice.getView_pwd());
    }

    @OnClick(R.id.connect_camera_button)
    public void onConnectCameraClick() {
        List<CameraData.Camera> cameras = mCameraData.getCameras();
        if (cameras.size() > 0) {
            Timber.d("start connecting");
            mP2PDevice = P2PDev.getP2PDev(TEST_DID);
            mP2PDevice.set_id(0);
            mP2PDevice.setCam_name(cameras.get(0).getName());
            mP2PDevice.setDev_id1(cameras.get(0).getDid());
            mP2PDevice.setView_pwd(cameras.get(0).getPassword());
            mP2PDevice.setModelOfDevInfo(TEST_MODEL);
            mP2PDevice.assembleUID();
            //mP2PDevice.sendIOCtrl_on_off(false, (byte)0, (byte)0);
            mP2PDevice.regAVListener(this);
            mP2PDevice.regRecvIOCtrlListener(this);
            mP2PDevice.startConn(0);
            //mP2PDevice.setEnterLiveView(true);


        }
    }

    @OnClick(R.id.check_status_button)
    public void onCheckStatusClick() {
        if (mP2PDevice != null) {
            Timber.d("view password: %d", mP2PDevice.isViewPwdOK());
            getVideoParameter();
        }
    }

    @Override
    public void onRecvIOCtrlData(int code, Object p2pDevObject, byte[] data) {
        Timber.d("onRecvIOCtrlData: code = %d", code);

        P2PDev p2PDev = null;

        if (p2pDevObject instanceof P2PDev) {
            p2PDev = (P2PDev) p2pDevObject;
        }

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
        }
    }

    private boolean isP2pDevResponse(int code) {
        return ((code >= 5000) && (code <= 5217));
    }

    private boolean isIoCtrlResponse(int code) {
        return ((code >= 0) && (code <= 111));
    }

    private void displayVideo(P2PDev p2PDev) {
        p2PDev.setEnterLiveView(true);
        p2PDev.regAVListener(this);
        p2PDev.regRecvIOCtrlListener(this);

        Timber.d("use hardware decoder: %b", HwH264Decoder.isUseHardwareDecoer());

        if (HwH264Decoder.isUseHardwareDecoer()) {

            mVideoView.setVisibility(View.GONE);

            mVideoHardwareView.setVisibility(View.VISIBLE);
            mVideoHardwareView.attachCamera(p2PDev, 0);
            mVideoHardwareView.setSurfaceTextureListener(
                    new TouchedTextureView.TouchedSurfaceTextureListener(mVideoHardwareView));

            if (mVideoHardwareView.isAvailable()) {
                Timber.d("view available: width=%d, height=%d", mVideoHardwareView.getWidth(), mVideoHardwareView.getHeight());
                p2PDev.setSurface(new Surface(mVideoHardwareView.getSurfaceTexture()));
            }

        } else {
            mVideoHardwareView.setVisibility(View.GONE);
            mVideoHardwareView.setSurfaceTextureListener(null);

            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.attachCamera(p2PDev, 0);

        }

        Timber.d("displayVideo: checkSensorCam - %b", P2PDev.checkSensorCam(p2PDev.getDev_id1()));

        if (P2PDev.checkSensorCam(p2PDev.getDev_id1())) {
            p2PDev.startAV(P2PDev.AV_TYPE_REALAV, 0, 0L, true, false);
        } else {
            p2PDev.startAV(P2PDev.AV_TYPE_REALAV, Ex_IOCTRLAVStream.QualityBySetting, 0, 0L,
                    Ex_IOCTRLAVStream.AUDIO_NOTIFY_NOT_CONSUME, true);
        }

        Timber.d("displayVideo: isSupportPTZ - %b", p2PDev.getParam().isSupportPTZ());

        if (p2PDev.getParam().isSupportPTZ()) {
            p2PDev.sendIOCtrl_fetchSensorList();
        }

        Timber.d("displayVideo: isSupportSecurityDisable - %b", p2PDev.getParam().isSupportSecurityDisable());
        if (p2PDev.getParam().isSupportSecurityDisable()) {
            p2PDev.sendIOCtrl_fetchArmSetting();
        }

    }

    private void getVideoParameter() {
        byte[] VParaReq = new byte[8];
        Arrays.fill(VParaReq, (byte)0);

        mP2PDevice.sendIOCtrl_outer(AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_VIDEO_PARAMETER_REQ,
                VParaReq, VParaReq.length);
    }

    @Override
    public void updateVFrame(Bitmap bitmap, Object p2pDevObject) {
        Timber.d("updateVFrame");
    }

    @Override
    public void updateAVInfo(int code, Object p2pDevObject, int value1, int value2) {
        Timber.d("updateAVInfo: code - %d (%s)", code, JswUtil.getP2pDevResponseString(code));
        if (isP2pDevResponse(code)) {
            P2PDev p2PDev = null;

            if (p2pDevObject instanceof P2PDev) {
                p2PDev = (P2PDev) p2pDevObject;
            }

            switch (code) {
                case P2PDev.STATUS_INFO_AV_ONLINENUM:
                    Timber.d("online number = %d, connection info = %d", value1, value2);
                    break;

                case P2PDev.STATUS_INFO_AV_RESOLUTION:
                    Timber.d("width = %d, height = %d", value1, value2);
                    break;

                case P2PDev.STATUS_INFO_GET_FRAME:
                    // add frame count
                    Timber.d("decoded output count = %d", value2);
                    break;
            }
        }
    }

    @Override
    public void updateAVInfo2(int code, Object p2pDeviceObject, int errorCode, int value) {
        Timber.d("updateAVInfo2: code - %d (%s)", code, JswUtil.getP2pDevResponseString(code));
    }

    private static class P2pDevResponseHandler extends Handler {

        private final WeakReference<DashboardActivity1> mActivity;

        P2pDevResponseHandler(DashboardActivity1 activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            Timber.d("P2pDevResponseHandler: %s", JswUtil.getP2pDevResponseString(msg.what));

            DashboardActivity1 activity = mActivity.get();

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
                case P2PDev.OM_GET_ONE_PIC_FROM_STREAM:
                    // TODO: refresh
                    break;

                case P2PDev.OM_GET_ONE_PIC_TIMEOUT:
                    break;

                case P2PDev.CONN_INFO_SESSION_CLOSED:
                    Timber.d("disconnect %s with connection info %d", p2PDev.getDev_id1(), p2PDev.getConnInfo());

                    if (P2PDev.CONN_INFO_CONNECT_WRONG_PWD == p2PDev.getConnInfo()) {
                        break;
                    }

                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    // TODO: refresh list
                    break;

                case P2PDev.CONN_INFO_CONNECT_WRONG_PWD:
                    // TODO: stop connection

                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    // TODO: refresh list
                    break;

                case P2PDev.CONN_INFO_CONNECTING:
                case P2PDev.CONN_INFO_CONNECT_FAIL:
                case P2PDev.CONN_INFO_CONNECT_WRONG_DID:
                    if (!p2PDev.getWaitApConnected()) {
                        // TODO: update camera status
                    }

                    // TODO: refresh list
                    break;

                case P2PDev.CONN_INFO_CONNECTED:
                    if (p2PDev.getWaitApConnected()) {
                        p2PDev.setWaitApConnected(false);

                        // TODO: stop reconnect timer
                    }
                    // TODO: update camera status

                    //p2PDev.changeDateTime();
                    //p2PDev.startGetOnePicFromRealStream();

                    //activity.displayVideo(p2PDev);
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

        private final WeakReference<DashboardActivity1> mActivity;

        IoCtrlHandler(DashboardActivity1 activity) {
            mActivity = new WeakReference<DashboardActivity1>(activity);
        }

        public void handleMessage(Message msg) {

            Timber.d("IoCtrlHandler: %s", JswUtil.getIoctlResponseString(msg.what));
            DashboardActivity1 activity = mActivity.get();

            if (activity == null) {
                return;
            }

            P2PDev p2PDev = null;
            int code = msg.what;
            byte[] data = msg.getData().getByteArray("data");

            if (msg.obj instanceof P2PDev) {
                p2PDev = (P2PDev) msg.obj;
            }

            if (p2PDev == null) {
                Timber.w("P2PDev is null");
                return;
            }


            switch (code) {
                case AUTH_AV_IO_Proto.IOCTRL_TYPE_DEVINFO_RESP:
                    // TODO: update camera to database
                    // TODO: refresh list
                    Timber.d("camera_model=%s", p2PDev.getModelOfDevInfo());
                    //activity.displayVideo(p2PDev);
                    //p2PDev.startGetOnePicFromRealStream();
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

                case AUTH_AV_IO_Proto.IOCTRL_TYPE_GET_VIDEO_PARAMETER_RESP:
                    Ex_IOCTRLGetVideoParameterResp resp = new Ex_IOCTRLGetVideoParameterResp();
                    resp.setData(data, 0);

                    Timber.d("video_quality=%d", resp.getQuality());
                    Timber.d("support_hd=%b", p2PDev.getParam().isSupportVideoQuality_HD());

                    break;
            }
        }
    }
}
