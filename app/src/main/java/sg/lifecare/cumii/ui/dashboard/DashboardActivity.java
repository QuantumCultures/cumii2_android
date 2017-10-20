package sg.lifecare.cumii.ui.dashboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.ConnectedDeviceResponse;
import sg.lifecare.cumii.data.server.response.EntityDetailResponse;
import sg.lifecare.cumii.data.server.response.LogoutResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.service.CumiiMqttService;
import sg.lifecare.cumii.ui.base.BaseActivity;
import timber.log.Timber;

public class DashboardActivity extends BaseActivity implements
        MemberListFragment.MemberListFragmentListener,
        DeviceListFragment.DeviceListFragmentListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private ActionBarDrawerToggle mDrawerToggle;

    private CircleImageView mUserProfileImage;
    private TextView mUserNameText;

    private CumiiMqttService mCumiiMqttService;
    private boolean mIsBound = false;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        setupView();

        String id = mDataManager.getPreferences().getEntityId();

        if (TextUtils.isEmpty(id)) {
            goToLoginActivity();
        } else {
            startService(CumiiMqttService.getStartIntent(this));
            getUserEntity(id);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Timber.d("onStart");

        bindService(CumiiMqttService.getStartIntent(this), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        Timber.d("onStop");

        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = true;
        }
    }

    private void setupView() {
        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().hide();
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        View headerLayout = mNavigationView.getHeaderView(0);
        mUserProfileImage = headerLayout.findViewById(R.id.user_profile_image);
        mUserNameText = headerLayout.findViewById(R.id.user_name_text);

        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_item_logout:
                    logout();
                    break;
                default:
                    break;
            }

            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    Timber.d("onBackStackChanged: count %d", getSupportFragmentManager().getBackStackEntryCount());
                    updateActionBar();
                });

        getSupportActionBar().setTitle(R.string.title_home);

    }

    private void updateActionBar() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count > 0) {

            String tag = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

            Timber.d("Tag: %s", tag);

            if (MemberListFragment.class.getSimpleName().equals(tag)) {
                //getSupportActionBar().setTitle(R.string.title_home);
                //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //getSupportActionBar().setHomeButtonEnabled(true);
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                getSupportActionBar().setTitle(R.string.title_home);
            } else if (MemberFragment.class.getSimpleName().equals(tag)) {
                //getSupportActionBar().setTitle(((MemberFragment) fragment).getTitle());
               // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //getSupportActionBar().setHomeButtonEnabled(false);
                mDrawerToggle.setDrawerIndicatorEnabled(false);

                if (fragment != null) {
                    getSupportActionBar().setTitle(((MemberFragment) fragment).getTitle());
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected: %b", mDrawerToggle.onOptionsItemSelected(item));
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed: count=%d", getSupportFragmentManager().getBackStackEntryCount());

        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void getUserEntity(String id) {
        Observable<EntityDetailResponse> observable = mDataManager.getCumiiService().getEntityDetail(id);
        mCompositeDisposable.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entityDetailResponse -> {

                    onGetUserEntityResult(entityDetailResponse.getData());

                }, throwable -> {
                    Timber.e(throwable, throwable.getMessage());

                    if (throwable instanceof SocketTimeoutException) {
                        showNetworkError(R.string.action_retry,
                                (dialogInterface, i) -> getUserEntity(mDataManager.getPreferences().getEntityId()));
                    } else if (throwable instanceof HttpException){
                        if (((HttpException) throwable).code() == 401) {
                            goToLoginActivity();
                            return;
                        }

                        Type type = new TypeToken<EntityDetailResponse>() {}.getType();
                        Response response = CumiiUtil.getResponse(
                                ((HttpException) throwable).response().errorBody(), type);

                        if (response.getErrorCode() == 401) {
                            goToLoginActivity();
                            return;
                        }

                        if (!TextUtils.isEmpty(response.getErrorDesc())) {
                            showServerError(response.getErrorDesc());
                        } else {
                            showServerError(getString(R.string.error_login_internet));
                        }
                    }
                }));
    }

    private void logout() {
        Observable<LogoutResponse> observable =
                mDataManager.getCumiiService().logout(mDataManager.getPreferences().getDeviceId());
        mCompositeDisposable.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutResponse -> {
                    onLogoutResult(logoutResponse);
                }, throwable -> {
                    Timber.e(throwable, throwable.getMessage());

                    if (throwable instanceof SocketTimeoutException) {
                        showNetworkError(R.string.action_retry,
                                (dialogInterface, i) -> getUserEntity(mDataManager.getPreferences().getEntityId()));
                    } else if (throwable instanceof HttpException){
                        if (((HttpException) throwable).code() == 401) {
                            goToLoginActivity();
                            return;
                        }

                        Type type = new TypeToken<EntityDetailResponse>() {}.getType();
                        Response response = CumiiUtil.getResponse(
                                ((HttpException) throwable).response().errorBody(), type);

                        if ((response != null) && !TextUtils.isEmpty(response.getErrorDesc())) {
                            showServerError(response.getErrorDesc());
                        } else {
                            showServerError(getString(R.string.error_login_internet));
                        }
                    }
                }));
    }

    private void onGetUserEntityResult(EntityDetailResponse.Data user) {
        ActionBar actionBar = getSupportActionBar();

        if (user == null) {
            finish();
        } else {
            if (actionBar != null) {
                getSupportActionBar().show();
            }


            mDataManager.setUser(user);
            mUserNameText.setText(user.getName());
            if (!TextUtils.isEmpty(user.getDefaultMediaUrl())) {
                Glide.with(this)
                        .load(user.getDefaultMediaUrl())
                        .into(mUserProfileImage);
            }

            /*if (user.getAuthorizationLevel() > CumiiUtil.USER_LEVEL) {
                showMemberListFragment();
            } else {
                // go to own profile
            }*/
            showMemberListFragment();
        }
    }

    private void onLogoutResult(LogoutResponse logoutResponse) {
        mDataManager.getPreferences().clear(this);
        goToLoginActivity();
    }

    private void showMemberListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, MemberListFragment.newInstance(), MemberListFragment.class.getSimpleName())
                .addToBackStack(MemberListFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void showMemberFragment(int position) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, MemberFragment.newInstance(position), MemberFragment.class.getSimpleName())
                .addToBackStack(MemberFragment.class.getSimpleName())
                .commit();
    }

    public CumiiMqttService getCumiiMqttService() {
        if (mIsBound) {
            return mCumiiMqttService;
        }

        return null;
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Timber.d("onServiceConnected");

            CumiiMqttService.LocalBinder binder = (CumiiMqttService.LocalBinder) service;
            mCumiiMqttService = binder.getService();
            mIsBound = true;

            mCumiiMqttService.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Timber.d("onServiceDisconnected");
            mIsBound = false;
        }
    };

    @Override
    public void onGatewayUpdate(String entityId, List<ConnectedDeviceResponse.Data> gateways) {

        // assume only one gateway
        if (gateways.size() > 0) {
            mCumiiMqttService.subscribeCameraTopics(entityId, gateways.get(0).getId());
            mCumiiMqttService.subscribeZwaveTopics(entityId, gateways.get(0).getId());
        }
    }
}
