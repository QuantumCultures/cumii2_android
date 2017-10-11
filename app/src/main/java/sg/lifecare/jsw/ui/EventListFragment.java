package sg.lifecare.jsw.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jsw.sdk.p2p.device.P2PDev;
import com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto;
import com.jsw.sdk.p2p.device.extend.Ex_IOCTRLListEventReq;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.P2pCamera;
import sg.lifecare.jsw.ui.adapter.EventListAdapter;
import sg.lifecare.jsw.ui.dialog.DownloadProgressDialog;
import timber.log.Timber;

public class EventListFragment extends Fragment {

    private static final String PARAM_CAMERA_INDEX = "camera_index";

    @BindView(R.id.list)
    RecyclerView mListView;

    private P2pCamera mP2pCamera;

    private EventListAdapter mAdapter;
    private AlertDialog mDownloadProgressDialog;

    public static EventListFragment newInstance(int index) {

        Bundle data = new Bundle();
        data.putInt(PARAM_CAMERA_INDEX, index);

        EventListFragment fragment = new EventListFragment();
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
        View view = inflater.inflate(R.layout.jsw_fragment_event_list, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.d("onResume");

        mP2pCamera.addEventListener(mP2pEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.d("onPause");

        mP2pCamera.removeEventListener();
    }

    private void setupView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mListView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new EventListAdapter();
        mListView.setAdapter(mAdapter);

        mDownloadProgressDialog = new DownloadProgressDialog(getContext()).create();
        mDownloadProgressDialog.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getEvents();

    }

    private void getEvents() {

        mP2pCamera.clearEventData();

        if ((mP2pCamera.getP2pDev().getConnInfo() >= P2PDev.CONN_INFO_CONNECTING) &&
                (mP2pCamera.getP2pDev().getConnInfo() < P2PDev.CONN_INFO_CONNECTED)) {
            //TODO: not connected message
        } else {

            DateTime start = new DateTime(2017, 9, 29, 0, 1);
            DateTime end = new DateTime(2017, 9, 29, 11, 59);


            byte[] data = Ex_IOCTRLListEventReq.parseConent(mP2pCamera.getPosition(),
                    start.getMillis(), end.getMillis(),
                    AUTH_AV_IO_Proto.IOCTRL_EVENT_ALL, (byte)0);
            mP2pCamera.getP2pDev().sendIOCtrl_outer(AUTH_AV_IO_Proto.IOCTRL_TYPE_LISTEVENT_REQ,
                    data, Ex_IOCTRLListEventReq.LEN_HEAD);
        }
    }

    private void showDownloadProgressDialog() {

    }

    private P2pCamera.P2pEventListener mP2pEventListener = new P2pCamera.P2pEventListener() {

        @Override
        public void onEventDataUpdate(P2pCamera p2pCamera) {
            Timber.d("onEventDataUpdate");
            mAdapter.replaceEventData(p2pCamera.getEventData());
        }
    };
}
