package sg.lifecare.cumii.ui.dashboard;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.ConnectedDeviceResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.cumii.ui.dashboard.adapter.DeviceListAdapter;
import sg.lifecare.jsw.data.CameraData;
import timber.log.Timber;

public class DeviceListFragment extends BaseFragment {

    private static final String KEY_MEMBER_POSITION = "position";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.device_list)
    RecyclerView mDeviceListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private AssistsedEntityResponse.Data mMember;
    private DeviceListAdapter mDeviceAdapter;

    public static DeviceListFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMember = getDataManager().getMember(getArguments().getInt(KEY_MEMBER_POSITION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        ButterKnife.bind(this, view);

        setupView();

        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGateways(mMember.getId());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getGateways(mMember.getId());
    }

    private void setupView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mDeviceListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceListView.addItemDecoration(dividerItemDecoration);

        mDeviceAdapter = new DeviceListAdapter(getContext(), new DeviceListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {

            }
        });
        mDeviceListView.setAdapter(mDeviceAdapter);
    }

    private void getGateways(String id) {

        mSwipeView.setRefreshing(true);
        mMessageText.setVisibility(View.INVISIBLE);

        Observable<ConnectedDeviceResponse> observable = getDataManager().getCumiiService().getConnectedGateways(id);
        getCompositeDisposible().add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectedDeviceResponse -> {
                    mSwipeView.setRefreshing(false);

                    onGetGatewaysResult(connectedDeviceResponse.getData());

                }, throwable -> {
                    Timber.e(throwable, throwable.getMessage());

                    mSwipeView.setRefreshing(false);

                    if (throwable instanceof SocketTimeoutException) {
                        getBaseActivity().showNetworkError();
                    } else if (throwable instanceof HttpException){
                        if (((HttpException) throwable).code() == 401) {
                            getBaseActivity().goToLoginActivity();
                            return;
                        }

                        Type type = new TypeToken<AssistsedEntityResponse>() {}.getType();
                        Response response = CumiiUtil.getResponse(
                                ((HttpException) throwable).response().errorBody(), type);

                        if ((response != null) && !TextUtils.isEmpty(response.getErrorDesc())) {
                            getBaseActivity().showServerError(response.getErrorDesc());
                        } else {
                            getBaseActivity().showServerError(getString(R.string.error_login_internet));
                        }
                    }
                }));
    }

    private void onGetGatewaysResult(List<ConnectedDeviceResponse.Data> gateways) {

        getSmartDevices(mMember.getId(), gateways);
    }

    private void getSmartDevices(String id, final List<ConnectedDeviceResponse.Data> gateways) {

        mSwipeView.setRefreshing(true);
        mMessageText.setVisibility(View.INVISIBLE);

        Observable<ConnectedDeviceResponse> observable = getDataManager().getCumiiService().getConnectedSmartDevices(id);
        getCompositeDisposible().add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectedDeviceResponse -> {
                    mSwipeView.setRefreshing(false);

                    onGetSmartDevicesResult(gateways, connectedDeviceResponse.getData());

                }, throwable -> {
                    Timber.e(throwable, throwable.getMessage());

                    mSwipeView.setRefreshing(false);

                    if (throwable instanceof SocketTimeoutException) {
                        getBaseActivity().showNetworkError();
                    } else if (throwable instanceof HttpException){
                        if (((HttpException) throwable).code() == 401) {
                            getBaseActivity().goToLoginActivity();
                            return;
                        }

                        Type type = new TypeToken<AssistsedEntityResponse>() {}.getType();
                        Response response = CumiiUtil.getResponse(
                                ((HttpException) throwable).response().errorBody(), type);

                        if ((response != null) && !TextUtils.isEmpty(response.getErrorDesc())) {
                            getBaseActivity().showServerError(response.getErrorDesc());
                        } else {
                            getBaseActivity().showServerError(getString(R.string.error_login_internet));
                        }
                    }
                }));
    }

    private void onGetSmartDevicesResult(List<ConnectedDeviceResponse.Data> gateways,
            List<ConnectedDeviceResponse.Data> devices) {

        // save camera devices
        CameraData cameraData = CameraData.getInstance(getContext());
        cameraData.clear();

        for (ConnectedDeviceResponse.Data device : devices) {
            if (device.getProductType() == CumiiUtil.JSW_CAMERA_PRODUCT_TYPE) {
                cameraData.addCamera(device.getName(), device.getId(), device.getPassword());
            }
        }

        mDeviceAdapter.replaceDevices(gateways, devices);

    }
}
