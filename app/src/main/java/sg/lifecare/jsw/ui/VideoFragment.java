package sg.lifecare.jsw.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.jsw.sdk.decoder.HwH264Decoder;
import com.jsw.sdk.p2p.device.P2PDev;
import com.jsw.sdk.p2p.device.extend.Ex_IOCTRLAVStream;
import com.jsw.sdk.ui.TouchedTextureView;
import com.jsw.sdk.ui.TouchedView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.P2pCamera;
import timber.log.Timber;

public class VideoFragment extends Fragment {

    private static final String PARAM_CAMERA_INDEX = "camera_index";

    @BindView(R.id.video_hardware_view)
    TouchedTextureView mVideoHardwareView;

    @BindView(R.id.video_view)
    TouchedView mVideoView;

    private P2pCamera mP2pCamera;

    public static VideoFragment newInstance(int index) {
        Bundle data = new Bundle();
        data.putInt(PARAM_CAMERA_INDEX, index);

        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(data);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int index = getArguments().getInt(PARAM_CAMERA_INDEX);

        mP2pCamera = ((DashboardActivity) getActivity()).getP2pCamera(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jsw_fragment_video, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.d("onResume");

        mP2pCamera.getP2pDev().setEnterLiveView(true);

        if (P2PDev.checkSensorCam(mP2pCamera.getP2pDev().getDev_id1())) {
            mP2pCamera.getP2pDev().startAV(P2PDev.AV_TYPE_REALAV, 0, 0L, true, false);
        } else {
            mP2pCamera.getP2pDev().startAV(P2PDev.AV_TYPE_REALAV, Ex_IOCTRLAVStream.QualityBySetting, 0, 0L,
                    Ex_IOCTRLAVStream.AUDIO_NOTIFY_NOT_CONSUME, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.d("onPause");

        mP2pCamera.getP2pDev().setEnterLiveView(false);
    }

    private void setupView() {
        if (HwH264Decoder.isUseHardwareDecoer()) {
            mVideoView.setVisibility(View.GONE);

            mVideoHardwareView.setVisibility(View.VISIBLE);
            mVideoHardwareView.attachCamera(mP2pCamera.getP2pDev(), mP2pCamera.getPosition());
            mVideoHardwareView.setSurfaceTextureListener(
                    new TouchedTextureView.TouchedSurfaceTextureListener(mVideoHardwareView));

            if (mVideoHardwareView.isAvailable()) {
                mP2pCamera.getP2pDev().setSurface(new Surface(mVideoHardwareView.getSurfaceTexture()));
            }

        } else {
            mVideoHardwareView.setVisibility(View.GONE);
            mVideoHardwareView.setSurfaceTextureListener(null);

            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.attachCamera(mP2pCamera.getP2pDev(), 0);
        }
    }
}
