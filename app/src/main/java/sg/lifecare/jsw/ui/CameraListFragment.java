package sg.lifecare.jsw.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jsw.sdk.p2p.device.P2PDev;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.P2pCamera;
import sg.lifecare.jsw.ui.adapter.CameraListAdapter;
import timber.log.Timber;

public class CameraListFragment extends Fragment{

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.list)
    RecyclerView mListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private CameraListAdapter mAdapter;
    private List<P2pCamera> mP2pCameras;

    static CameraListFragment newInstance() {
        return new CameraListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mP2pCameras = ((DashboardActivity) getActivity()).getP2pCameras();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jsw_fragment_camera_list, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    private void setupView() {
        //mSwipeView.setEnabled(false);
        mMessageText.setVisibility(View.INVISIBLE);

        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CameraListAdapter(mListView, mCameraItemClickListener);
        mListView.setAdapter(mAdapter);

        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mP2pCameras.size() > 0) {
                    for (P2pCamera p2pCamera : mP2pCameras) {
                        if (p2pCamera.getP2pDev().isConnected()) {
                            p2pCamera.getP2pDev().getSnapshot();
                        }
                    }
                }

                mSwipeView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");


        mAdapter.replaceCameras(mP2pCameras);
    }

    @Override
    public void onStart() {
        super.onStart();

        Timber.d("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.d("onResume");


        for (int i = 0; i < mP2pCameras.size(); i++) {
            mP2pCameras.get(i).addConnectionListener(mP2pConnectionListener);
            mP2pCameras.get(i).addSnapshotListener(mP2pSnapshotListener);
        }

        mListView.postDelayed(() -> {
            ((DashboardActivity) getActivity()).resumeAllCameras();
            ((DashboardActivity) getActivity()).startAllCameras();
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.d("onPause");

        for (int i = 0; i < mP2pCameras.size(); i++) {
            mP2pCameras.get(i).removeAllListeners();
        }

        ((DashboardActivity) getActivity()).pauseAllCameras();
    }

    @Override
    public void onStop() {
        super.onStop();

        Timber.d("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((DashboardActivity) getActivity()).stopAllCameras();
    }

    private P2pCamera.P2pConnectionListener mP2pConnectionListener = new P2pCamera.P2pConnectionListener() {
        @Override
        public void onConnectionStatusUpdate(P2pCamera p2pCamera) {
            mAdapter.notifyItemChanged(p2pCamera.getPosition());
        }
    };

    private P2pCamera.P2pSnapshotListener mP2pSnapshotListener = new P2pCamera.P2pSnapshotListener() {
        @Override
        public void onSnapshotUpdate(P2pCamera p2pCamera) {
            mAdapter.notifyItemChanged(p2pCamera.getPosition());
        }
    };

    private CameraListAdapter.OnCameraItemClickListener mCameraItemClickListener =
            new CameraListAdapter.OnCameraItemClickListener() {

                @Override
                public void onEventClick(P2pCamera p2pCamera) {
                    if (p2pCamera.getP2pDev().getConnInfo() == P2PDev.CONN_INFO_CONNECTED) {
                        ((DashboardActivity) getActivity()).showEventListFragment(p2pCamera.getPosition());
                    }
                }

                @Override
                public void onVideoClick(P2pCamera p2pCamera) {
                    if (p2pCamera.getP2pDev().getConnInfo() == P2PDev.CONN_INFO_CONNECTED) {
                        ((DashboardActivity) getActivity()).showVideoFragment(p2pCamera.getPosition());
                    }
                }
            };
}
