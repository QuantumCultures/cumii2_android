package sg.lifecare.cumii.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.ui.base.BaseFragment;
import timber.log.Timber;

public class MemberFragment extends BaseFragment {

    private static final String KEY_MEMBER_POSITION = "position";

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabs;

    private int mPosition = -1;
    private AssistsedEntityResponse.Data mMember;

    public static MemberFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        MemberFragment fragment = new MemberFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mPosition = getArguments().getInt(KEY_MEMBER_POSITION);
        mMember = getDataManager().getMember(mPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    private void setupView() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(EventListFragment.newInstance(mPosition), getString(R.string.title_events));
        adapter.addFragment(ProfileFragment.newInstance(mPosition), getString(R.string.title_profile));
        adapter.addFragment(DeviceListFragment.newInstance(mPosition), getString(R.string.title_devices));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);

        mTabs.post(new Runnable() {
            @Override
            public void run() {
                mTabs.setupWithViewPager(mViewPager);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.member, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected: %d    %d", item.getItemId(), R.id.action_camera);
        switch (item.getItemId()) {
            case R.id.action_camera:
                startCumiiDashboardActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startCumiiDashboardActivity() {
        Intent intent = new Intent(getActivity(), sg.lifecare.jsw.ui.DashboardActivity.class);
        startActivity(intent);
    }

    public String getTitle() {
        return mMember.getName();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Timber.d("getCount %s", mTitleList.get(position));
            return mTitleList.get(position);
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }
    }
}
