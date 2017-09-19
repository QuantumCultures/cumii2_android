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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.ActivityDataResponse;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.cumii.ui.dashboard.adapter.EventListAdapter;
import timber.log.Timber;

public class EventListFragment extends BaseFragment implements EventListAdapter.OnItemClickListener {

    private static final String KEY_MEMBER_POSITION = "position";

    private static final int NUM_OF_DATA = 100;

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.list)
    RecyclerView mListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private AssistsedEntityResponse.Data mMember;
    private EventListAdapter mAdapter;

    private int mSkipSize = 0;
    private boolean mHasExtra = false;

    public static EventListFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        EventListFragment fragment = new EventListFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        Timber.d("onCreateView");

        ButterKnife.bind(this, view);

        setupView();

        mSwipeView.setOnRefreshListener(() -> {
            mSkipSize = 0;
            getEvents(mMember.getId());
        });

        return view;
    }

    private void setupView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mListView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));

        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.addItemDecoration(dividerItemDecoration);

        mAdapter = new EventListAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");

        getEvents(mMember.getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.d("onDestroy");
    }

    private void getEvents(String id) {

        mSwipeView.setRefreshing(true);
        mMessageText.setVisibility(View.INVISIBLE);

        mHasExtra = false;

        Observable<ActivityDataResponse> observable = getDataManager().getCumiiService().getActivitiesData(id, NUM_OF_DATA + 1, mSkipSize);
        getCompositeDisposible().add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(activityDataResponse -> {
                    mSwipeView.setRefreshing(false);

                    onGetEventsResult(activityDataResponse.getData());

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


    private void onGetEventsResult(List<ActivityDataResponse.Data> events) {
        if (events == null) {
            getActivity().finish();
        } else {

            if (events.size() == 0) {
                // show no member
                mMessageText.setVisibility(View.VISIBLE);
            } else {
                // check has extra events
                if (events.size() == (NUM_OF_DATA + 1)) {
                    mHasExtra = true;
                    events.remove(events.size() - 1);
                } else {
                    mHasExtra = false;
                }

                if (mSkipSize == 0) {
                    mAdapter.replaceEvents(events);
                } else {
                    // check duplicated events
                    if (mAdapter.getItemCount() > 0) {
                        ActivityDataResponse.Data lastEvent = mAdapter.getLastEvent();
                        Date lastEventDate = lastEvent.getCreateDate();

                        do {
                            ActivityDataResponse.Data newEvent = events.get(0);
                            Date newEventDate = newEvent.getCreateDate();

                            if (newEventDate.compareTo(lastEventDate) > 0) {
                                events.remove(0);
                            } else if (newEventDate.compareTo(lastEventDate) == 0) {
                                if (lastEvent.getId().equals(newEvent.getId())) {
                                    events.remove(0);
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } while (events.size() > 0);
                    }

                    mAdapter.addEvents(events);
                }

                mSkipSize += mAdapter.getItemCount();
            }
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLastItemVisible() {
        if (mHasExtra) {
            getEvents(getDataManager().getUser().getId());
        }
    }
}
