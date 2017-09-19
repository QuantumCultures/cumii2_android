package sg.lifecare.cumii.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.ui.base.BaseFragment;
import sg.lifecare.cumii.ui.dashboard.profile.ActitvityFragment;
import sg.lifecare.cumii.ui.dashboard.profile.UtilityFragment;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment {

    private static final String KEY_MEMBER_POSITION = "position";

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabs;

    private ViewPagerAdapter mAdapter;

    private int mPosition;

    public static ProfileFragment newInstance(int position) {
        Bundle data = new Bundle();
        data.putInt(KEY_MEMBER_POSITION, position);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPosition = getArguments().getInt(KEY_MEMBER_POSITION);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);

        setupView();

        return view;
    }

    private void setupView() {

        mAdapter = new ViewPagerAdapter(getChildFragmentManager());

        mAdapter.addFragment(ActitvityFragment.newInstance(mPosition), ContextCompat.getDrawable(getContext(), R.drawable.tab_activity_selector));
        mAdapter.addFragment(UtilityFragment.newInstance(mPosition), ContextCompat.getDrawable(getContext(), R.drawable.tab_utility_selector));


        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());

        mTabs.post(new Runnable() {
            @Override
            public void run() {
                mTabs.setupWithViewPager(mViewPager);

                for (int i = 0; i < mTabs.getTabCount(); i++) {
                    mTabs.getTabAt(i).setIcon(mAdapter.getDrawable(i));
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<Drawable> mDrawableList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        Drawable getDrawable(int position) {
            return mDrawableList.get(position);
        }

        void addFragment(Fragment fragment, Drawable drawable) {
            mFragmentList.add(fragment);
            mDrawableList.add(drawable);
        }
    }
}
