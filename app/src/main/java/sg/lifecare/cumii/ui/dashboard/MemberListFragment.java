package sg.lifecare.cumii.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Collections;
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
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.cumii.ui.dashboard.adapter.MemberListAdapter;
import timber.log.Timber;

public class MemberListFragment extends BaseFragment implements MemberListAdapter.OnItemClickListener{

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeView;

    @BindView(R.id.list)
    RecyclerView mListView;

    @BindView(R.id.message)
    TextView mMessageText;

    private MemberListFragmentListener mCallback;

    private MemberListAdapter mAdapter;

    public static MemberListFragment newInstance() {
        MemberListFragment fragment = new MemberListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MemberListFragmentListener) {
            mCallback = (MemberListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MemberListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);

        ButterKnife.bind(this, view);

        setupView();

        mSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMembersEntity(getDataManager().getUser().getId());
            }
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

        mAdapter = new MemberListAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMembersEntity(getDataManager().getUser().getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawer, menu);
    }

    private void getMembersEntity(String id) {

        mSwipeView.setRefreshing(true);
        mMessageText.setVisibility(View.INVISIBLE);

        Observable<AssistsedEntityResponse> observable = getDataManager().getCumiiService().getAsisteds(id);
        getCompositeDisposible().add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(assistsedEntityResponse -> {
                    mSwipeView.setRefreshing(false);

                    onGetMembersEntityResult(assistsedEntityResponse.getData());

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

    private void onGetMembersEntityResult(List<AssistsedEntityResponse.Data> members) {
        if (members == null) {
            getActivity().finish();
        } else {
            Collections.sort(members, (d1, d2) -> d1.getName().compareTo(d2.getName()));

            getDataManager().setMembers(members);

            if (members.size() == 0) {
                // show no member
                mMessageText.setVisibility(View.VISIBLE);
            } else {
                mAdapter.replaceMembers(members);
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mCallback != null) {
            mCallback.showMemberFragment(position);
        }
    }


    public interface MemberListFragmentListener {
        void showMemberFragment(int position);
    }
}
