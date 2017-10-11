package sg.lifecare.cumii.ui.dashboard.profile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.ActivityStatisticResponse;
import sg.lifecare.cumii.data.server.response.AggregatedActivityResponse;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.BathroomStatisticResponse;
import sg.lifecare.cumii.data.server.response.RelatedAlertMessageResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.data.server.response.SleepMedianResponse;
import sg.lifecare.cumii.data.server.response.WakeupMedianResponse;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.cumii.util.DateUtils;
import timber.log.Timber;

public class ActitvityFragment extends BaseFragment {

    private static final String KEY_MEMBER_POSITION = "position";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.today_activity_chart)
    HorizontalBarChart mChart;

    @BindView(R.id.wakeup_time_text)
    TextView mWakeupTimeText;

    @BindView(R.id.sleep_time_text)
    TextView mSleepTimeText;

    @BindView(R.id.day_usually_active_text)
    TextView mDayUsuallyActiveText;

    @BindView(R.id.night_usually_active_text)
    TextView mNightUsuallyActiveText;

    @BindView(R.id.day_usually_inactivity_for_text)
    TextView mDayUsuallyInactiveForText;

    @BindView(R.id.night_usually_inactivity_for_text)
    TextView mNightUsuallyInactiveForText;

    @BindView(R.id.day_longest_inactivity_period_text)
    TextView mDayLongestInactivityPeriodText;

    @BindView(R.id.night_longest_inactivity_period_text)
    TextView mNightLongestInactivityPeriodText;

    @BindView(R.id.day_bathroom_times_text)
    TextView mDayBathroomTimesText;

    @BindView(R.id.night_bathroom_times_text)
    TextView mNightBathroomTimesText;

    @BindView(R.id.day_bathroom_longest_text)
    TextView mDayBathroomLongestText;

    @BindView(R.id.night_bathroom_longest_text)
    TextView mNightBathroomLongestText;

    @BindView(R.id.day_bathroom_usual_text)
    TextView mDayBathroomUsualText;

    @BindView(R.id.night_bathroom_usual_text)
    TextView mNightBathroomUsualText;

    @BindView(R.id.day_bathroom_shortest_text)
    TextView mDayBathroomShortestText;

    @BindView(R.id.night_bathroom_shortest_text)
    TextView mNightBathroomShortestText;


    @BindView(R.id.median_sleep_frame)
    View mMedianSleepFrame;

    @BindView(R.id.median_wakeup_frame)
    View mMedianWakeupFrame;

    @BindView(R.id.day_activity_frame)
    View mDayActivityFrame;

    @BindView(R.id.night_activity_frame)
    View mNightActivityFrame;

    @BindView(R.id.wakeup_top_line)
    View mWakeupTopLine;

    @BindView(R.id.wakeup_bottom_line)
    View mWakeupBottomLine;

    @BindView(R.id.sleep_top_line)
    View mSleepTopLine;

    @BindView(R.id.sleep_bottom_line)
    View mSleepBottomLine;

    private AssistsedEntityResponse.Data mMember;

    public static ActitvityFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        ActitvityFragment fragment = new ActitvityFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_activity, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    private void setupView() {
        initChart();

        setDayView();

        mMedianWakeupFrame.setOnClickListener(view -> setDayView());

        mMedianSleepFrame.setOnClickListener(view -> setNightView());
    }

    private void initChart() {
        mChart.setDrawBarShadow(false);
        mChart.getXAxis().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setHighlightPerTapEnabled(false);

        Description description = new Description();
        description.setText("");
        description.setEnabled(false);
        mChart.setDescription(description);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");

        getActivity(mMember.getId());
    }

    private void getActivity(String id) {
        mSwipeView.setRefreshing(true);

        DateTimeFormatter dayFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        DateTime start = new DateTime().withTime(0, 0, 0, 0);
        DateTime end = new DateTime().withTime(23, 59, 59, 0);

        Timber.d("getActivity: start=%s, end=%s", DateUtils.getIsoTimestamp(start), DateUtils.getIsoTimestamp(end));

        Observable<AggregatedActivityResponse> aggregatedActivityObservable = getDataManager().getCumiiService()
                .getAggregatedActivity(id, DateUtils.getIsoTimestamp(start), DateUtils.getIsoTimestamp(end)/*, false*/)
                .onErrorResumeNext(Observable.empty());
        Observable<WakeupMedianResponse> medianWakeupObservable = getDataManager().getCumiiService()
                .getMedianWakeup(id, dayFormatter.print(start))
                .onErrorResumeNext(Observable.empty());
        Observable<SleepMedianResponse> medianSleepObservable = getDataManager().getCumiiService()
                .getMedianSleep(id, dayFormatter.print(start))
                .onErrorResumeNext(Observable.empty());
        Observable<ActivityStatisticResponse> activityStatisticObservable = getDataManager().getCumiiService()
                .getActivityStatistic(id, dayFormatter.print(start))
                .onErrorResumeNext(Observable.empty());
        Observable<BathroomStatisticResponse> bathroomStatisticObservable = getDataManager().getCumiiService()
                .getBathroomStatistic(id, dayFormatter.print(start))
                .onErrorResumeNext(Observable.empty());

        List<Observable<?>> list = new ArrayList<>();
        list.add(aggregatedActivityObservable);
        list.add(medianWakeupObservable);
        list.add(medianSleepObservable);
        list.add(activityStatisticObservable);
        list.add(bathroomStatisticObservable);

        getCompositeDisposible().add(Observable.concat(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    mSwipeView.setRefreshing(false);

                    if (response instanceof WakeupMedianResponse) {
                        WakeupMedianResponse.Data data = ((WakeupMedianResponse) response).getData();

                        if ((data == null) || (data.getMedianWakeupTime() == null)) {
                            mWakeupTimeText.setText(R.string.profile_activity_wakeup_time);
                        } else {
                            DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm a");
                            DateTime timestamp = new DateTime(data.getMedianWakeupTime(), DateTimeZone.getDefault());
                            mWakeupTimeText.setText(formatter.print(timestamp));
                        }
                    } else if (response instanceof SleepMedianResponse) {
                        SleepMedianResponse.Data data = ((SleepMedianResponse) response).getData();

                        if ((data == null) || (data.getMedianSleepTime() == null)) {
                            mSleepTimeText.setText(R.string.profile_activity_sleep_time);
                        } else {
                            DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mm a");
                            DateTime timestamp = new DateTime(data.getMedianSleepTime(), DateTimeZone.getDefault());
                            mSleepTimeText.setText(formatter.print(timestamp));
                        }
                    } else if (response instanceof ActivityStatisticResponse) {
                        ActivityStatisticResponse.Data data = ((ActivityStatisticResponse)response).getData();

                        if (data == null) {
                            // TODO
                        } else {
                            mDayUsuallyActiveText.setText(String.valueOf(data.getDayAverageActivityLevel()) + "%");
                            mDayUsuallyInactiveForText.setText(String.valueOf(data.getDayAverageInactivityDuration()) + " mins");
                            mDayLongestInactivityPeriodText.setText(String.valueOf(data.getDayMaxInactivityDuration()) + " mins");

                            mNightUsuallyActiveText.setText(String.valueOf(data.getNightAverageActivityLevel()) + "%");
                            mNightUsuallyInactiveForText.setText(String.valueOf(data.getNightAverageInactivityDuration()) + " mins");
                            mNightLongestInactivityPeriodText.setText(String.valueOf(data.getNightMaxInactivityDuration()) + " mins");
                        }

                    } else if (response instanceof BathroomStatisticResponse) {
                        BathroomStatisticResponse.Data data = ((BathroomStatisticResponse)response).getData();

                        if (data == null) {
                            // TODO:
                        } else {
                            mDayBathroomTimesText.setText(
                                    String.valueOf(data.getDayMedianBathroomFrequency()));
                            mDayBathroomLongestText.setText(
                                    String.valueOf(data.getDayMaxDuration()));
                            mDayBathroomUsualText.setText(
                                    String.valueOf(data.getDayAverageDuration()));
                            mDayBathroomShortestText.setText(
                                    String.valueOf(data.getDayMinDuration()));

                            mNightBathroomTimesText.setText(
                                    String.valueOf(data.getNightMedianBathroomFrequency()));
                            mNightBathroomLongestText.setText(
                                    String.valueOf(data.getNightMaxDuration()));
                            mNightBathroomUsualText.setText(
                                    String.valueOf(data.getNightAverageDuration()));
                            mNightBathroomShortestText.setText(
                                    String.valueOf(data.getNightMinDuration()));
                        }

                    } else if (response instanceof AggregatedActivityResponse) {
                        updateChart((AggregatedActivityResponse)response);
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

    private void updateChart(AggregatedActivityResponse response) {

        List<AggregatedActivityResponse.Data> datas = response.getData();

        if ((datas != null) && (datas.size() > 0)) {

            float[] values = new float[datas.size()];
            int[] colors = new int[datas.size()];
            String[] names = new String[datas.size()];
            List<BarEntry> yVals = new ArrayList<>();

            for (int i = 0; i < datas.size(); i++) {
                Timber.d("%f", datas.get(i).getActivityLevelInZone());
                values[i] = datas.get(i).getActivityLevelInZone();
                colors[i] = datas.get(i).getZone().getColor();
                names[i] = datas.get(i).getZone().getName();
            }


            yVals.add(new BarEntry(0, values));

            BarDataSet set = new BarDataSet(yVals, "");
            set.setDrawValues(false);
            set.setStackLabels(names);
            set.setColors(colors);

            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);

            BarData barData = new BarData(dataSets);
            mChart.setData(barData);
            mChart.setFitBars(true);
            mChart.invalidate();

        }

    }

    private void setDayView() {
        mDayActivityFrame.setVisibility(View.VISIBLE);
        mNightActivityFrame.setVisibility(View.INVISIBLE);

        mWakeupTopLine.setVisibility(View.VISIBLE);
        mWakeupBottomLine.setVisibility(View.INVISIBLE);

        mSleepTopLine.setVisibility(View.INVISIBLE);
        mSleepBottomLine.setVisibility(View.VISIBLE);
    }

    private void setNightView() {
        mDayActivityFrame.setVisibility(View.INVISIBLE);
        mNightActivityFrame.setVisibility(View.VISIBLE);

        mWakeupTopLine.setVisibility(View.INVISIBLE);
        mWakeupBottomLine.setVisibility(View.VISIBLE);

        mSleepTopLine.setVisibility(View.VISIBLE);
        mSleepBottomLine.setVisibility(View.INVISIBLE);
    }

}
