package sg.lifecare.zwave.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.service.CumiiMqttService;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.zwave.ZWaveDevice;
import sg.lifecare.zwave.ZWaveUtil;
import sg.lifecare.zwave.control.Control;
import sg.lifecare.zwave.control.Security;
import sg.lifecare.zwave.ui.adapter.ZWaveDeviceListAdapter;
import timber.log.Timber;

public class ZWaveDeviceListFragment extends BaseFragment {

    private static final String MEMBER_POSITION = "position";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.device_list)
    RecyclerView mDeviceListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private AssistsedEntityResponse.Data mMember;
    private ZWaveDeviceListAdapter mDeviceAdapter;
    private ZWaveDeviceListFragmentListener mCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ZWaveDeviceListFragmentListener) {
            mCallback = (ZWaveDeviceListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ZWaveDeviceListFragmentListener");
        }
    }



    public static ZWaveDeviceListFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(MEMBER_POSITION, position);

        ZWaveDeviceListFragment fragment = new ZWaveDeviceListFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMember = getDataManager().getMember(getArguments().getInt(MEMBER_POSITION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zwave_fragment_dashboard, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Timber.d("onStart");

        IntentFilter filter = new IntentFilter();
        filter.addAction(CumiiMqttService.ACTION_ZWAVE_STATUS);
        filter.addAction(CumiiMqttService.ACTION_ZWAVE_SECURITY);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMqttReceiver, filter);

        mDeviceAdapter.setOnOffSwitchListener(mOnOffSwitchListener);
        mDeviceAdapter.setSecurityListener(mSecurityListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        Timber.d("onStop");

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMqttReceiver);

        mDeviceAdapter.setOnOffSwitchListener(null);
        mDeviceAdapter.setSecurityListener(null);
    }

    private void setupView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mDeviceListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceListView.addItemDecoration(dividerItemDecoration);

        mDeviceAdapter = new ZWaveDeviceListAdapter(getContext());
        mDeviceListView.setAdapter(mDeviceAdapter);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ZWaveDeviceListAdapter.OnOffSwitchListener mOnOffSwitchListener = new ZWaveDeviceListAdapter.OnOffSwitchListener() {

        @Override
        public void onOnOffClick(ZWaveDevice device, boolean isOn) {
            Timber.d("onOffClick");

            CumiiMqttService mqttService = mCallback.getMqttService();
            if (mqttService != null) {
                List<AssistsedEntityResponse.Device> devices = mMember.getDevices();
                if ((devices != null) && (devices.size() > 0)) {

                    Control control = new Control();
                    control.setNodeId(device.getNodeId());

                    control.setValue(isOn ? Control.ON : Control.OFF);

                    mqttService.publishZwaveSet(mMember.getId(), devices.get(0).getId(),
                            Control.TITLE, Control.toJson(control));
                }
            }
        }
    };

    private ZWaveDeviceListAdapter.SecurityListener mSecurityListener = new ZWaveDeviceListAdapter.SecurityListener() {

        @Override
        public void onArmClick() {
            CumiiMqttService mqttService = mCallback.getMqttService();
            if (mqttService != null) {
                List<AssistsedEntityResponse.Device> devices = mMember.getDevices();
                if ((devices != null) && (devices.size() > 0)) {

                    Security alarm = new Security();
                    alarm.setStatus(Security.ARM);

                    mqttService.publishZwaveSet(mMember.getId(), devices.get(0).getId(),
                            Security.TITLE, Security.toJson(alarm));
                }
            }
        }

        @Override
        public void onDisarmClick() {
            CumiiMqttService mqttService = mCallback.getMqttService();
            if (mqttService != null) {
                List<AssistsedEntityResponse.Device> devices = mMember.getDevices();
                if ((devices != null) && (devices.size() > 0)) {

                    Security alarm = new Security();
                    alarm.setStatus(Security.DISARM);

                    mqttService.publishZwaveSet(mMember.getId(), devices.get(0).getId(),
                            Security.TITLE, Security.toJson(alarm));
                }
            }
        }

        @Override
        public void onHomeClick() {
            CumiiMqttService mqttService = mCallback.getMqttService();
            if (mqttService != null) {
                List<AssistsedEntityResponse.Device> devices = mMember.getDevices();
                if ((devices != null) && (devices.size() > 0)) {

                    Security alarm = new Security();
                    alarm.setStatus(Security.ARM_PARTIAL);

                    mqttService.publishZwaveSet(mMember.getId(), devices.get(0).getId(),
                            Security.TITLE, Security.toJson(alarm));
                }
            }
        }
    };

    private BroadcastReceiver mMqttReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("MqttReceiver: action=%s", intent.getAction());

            if (CumiiMqttService.ACTION_ZWAVE_STATUS.equals(intent.getAction())) {
                String message = intent.getStringExtra("message");
                Timber.d("    message: %s", message);

                List<ZWaveDevice> devices = ZWaveUtil.jsonToZwaveDevices(message);

                if (devices.size() > 0) {
                    mMessageText.setVisibility(View.INVISIBLE);
                } else {
                    mMessageText.setVisibility(View.VISIBLE);
                }

                // TODO: check the difference
                mDeviceAdapter.addAllDevices(devices);
            } else if (CumiiMqttService.ACTION_ZWAVE_SECURITY.equals(intent.getAction())) {
                String message = intent.getStringExtra("message");

                mMessageText.setVisibility(View.INVISIBLE);

                Security security = Security.fromJson(message);

                mDeviceAdapter.addSecurity(security);
            }
        }
    };

    public interface ZWaveDeviceListFragmentListener {
        CumiiMqttService getMqttService();
    }

}
