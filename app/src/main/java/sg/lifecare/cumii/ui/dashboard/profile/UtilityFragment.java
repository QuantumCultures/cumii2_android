package sg.lifecare.cumii.ui.dashboard.profile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.ActivityStatisticResponse;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.EnergyConsumptionResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.data.server.response.WaterConsumptionResponse;
import sg.lifecare.cumii.ui.base.BaseFragment;
import timber.log.Timber;

public class UtilityFragment extends BaseFragment {

    private static final String KEY_MEMBER_POSITION = "position";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    private AssistsedEntityResponse.Data mMember;

    public static UtilityFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        UtilityFragment fragment = new UtilityFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_utility, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    private void setupView() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getUtility(mMember.getId());
    }

    private void getUtility(String id) {
        mSwipeView.setRefreshing(true);

        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime start = new DateTime().withTime(0, 1, 0, 0);

        Observable<EnergyConsumptionResponse> energyConsumptionResponseObservable =
                getDataManager().getCumiiService().getEnergyConsumption(id, dayFormatter.print(start))
                        .onErrorResumeNext(Observable.empty());
        Observable<WaterConsumptionResponse> waterConsumptionResponseObservable =
                getDataManager().getCumiiService().getWaterConsumption(id, dayFormatter.print(start))
                        .onErrorResumeNext(Observable.empty());

        getCompositeDisposible().add(Observable.concat(energyConsumptionResponseObservable, waterConsumptionResponseObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    mSwipeView.setRefreshing(false);

                    if (response instanceof EnergyConsumptionResponse) {

                    } else if (response instanceof WaterConsumptionResponse) {

                    } else if (response instanceof ActivityStatisticResponse) {

                    }

                    //onGetEventsResult(activityDataResponse.getData());

                }, throwable -> {
                    Timber.e(throwable, throwable.getMessage());

                    //mSwipeView.setRefreshing(false);

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

}
