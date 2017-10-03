package sg.lifecare.jsw.ui.adapter;

import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECTED;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECTING;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_FAIL;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_WRONG_DID;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_CONNECT_WRONG_PWD;
import static com.jsw.sdk.p2p.device.P2PDev.CONN_INFO_SESSION_CLOSED;

import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.P2pCamera;
import timber.log.Timber;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.CameraViewHolder> {

    private List<P2pCamera> mP2pCameras = new ArrayList<>();

    public interface OnCameraItemClickListener {
        void onEventClick(P2pCamera p2pCamera);
        void onVideoClick(P2pCamera p2pCamera);
    }

    private OnCameraItemClickListener mListener;

    public CameraListAdapter(RecyclerView recyclerView, OnCameraItemClickListener listener) {
        mListener = listener;

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
                        CameraViewHolder cameraViewHolder =
                                (CameraViewHolder) recyclerView.findViewHolderForLayoutPosition(i);

                        if (cameraViewHolder.mOverlayFrame.getVisibility() == View.VISIBLE) {
                            cameraViewHolder.mOverlayFrame.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }

    @Override
    public CameraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_camera_view, parent, false);

        Timber.d("onCreateViewHolder");

        return new CameraViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CameraViewHolder holder, int position) {
        Timber.d("onBindViewHolder: position = %d", position);
        holder.bindView(mP2pCameras.get(position));
    }

    @Override
    public int getItemCount() {
        Timber.d("getItemCount: %d", mP2pCameras.size());
        return mP2pCameras.size();
    }

    public void replaceCameras(List<P2pCamera> cameras) {
        Timber.d("replaceCameras: size = %d", cameras.size());

        mP2pCameras = cameras;

        notifyDataSetChanged();
    }

    class CameraViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.status_text)
        TextView mStatusText;

        @BindView(R.id.snapshot_view)
        ImageView mSnapshotView;

        //@BindView(R.id.video_hardware_view)
        //TouchedTextureView mVideoHardwareView;

        //@BindView(R.id.video_view)
        //TouchedView mVideoView;

        @BindView(R.id.overlay_frame)
        View mOverlayFrame;

        @BindView(R.id.event_image)
        View mEventImage;

        @BindView(R.id.video_image)
        View mVideoImage;

        CameraViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            //mVideoView.setVisibility(View.GONE);
            //mVideoHardwareView.setVisibility(View.GONE);
            mOverlayFrame.setVisibility(View.GONE);
        }

        void bindView(final P2pCamera camera) {
            String name = camera.getP2pDev().getCam_name();
            if (TextUtils.isEmpty(name)) {
                mNameText.setText(itemView.getContext().getString(R.string.jsw_label_camera));
            } else {
                mNameText.setText(name);
            }

            int status = camera.getP2pDev().getConnInfo();
            switch (status) {
                case CONN_INFO_CONNECTED:
                    mStatusText.setText(R.string.jsw_label_connected);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_online));
                    break;

                case CONN_INFO_SESSION_CLOSED:
                    mStatusText.setText(R.string.jsw_label_disconnected);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_offline));
                    break;

                case CONN_INFO_CONNECT_WRONG_PWD:
                    mStatusText.setText(R.string.jsw_label_connect_wrong_password);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_offline));
                    break;

                case CONN_INFO_CONNECT_WRONG_DID:
                    mStatusText.setText(R.string.jsw_label_connect_wrong_did);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_offline));
                    break;

                case CONN_INFO_CONNECTING:
                    mStatusText.setText(R.string.jsw_label_connecting);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_normal));
                    break;

                case CONN_INFO_CONNECT_FAIL:
                    mStatusText.setText(R.string.jsw_label_disconnected);
                    mStatusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.jsw_offline));
                    break;
            }

            Bitmap snapshot = camera.getP2pDev().getSnapshot();
            if (snapshot != null) {
                mSnapshotView.setScaleType(ImageView.ScaleType.FIT_XY);
                mSnapshotView.setImageBitmap(snapshot);
            }

            mSnapshotView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOverlayFrame.setVisibility(View.VISIBLE);
                }
            });

            mOverlayFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOverlayFrame.setVisibility(View.GONE);
                }
            });

            mEventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.d("onclick");
                    if (mListener != null) {
                        mListener.onEventClick(camera);
                        mOverlayFrame.setVisibility(View.GONE);
                    }
                }
            });

            mVideoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onVideoClick(camera);
                        mOverlayFrame.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
