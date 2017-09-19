package sg.lifecare.cumii.ui.base;

import android.content.Context;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import sg.lifecare.cumii.data.DataManager;

public class BaseFragment extends Fragment {

    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            mActivity = activity;
        }
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public DataManager getDataManager() {
        if (mActivity != null) {
            return mActivity.mDataManager;
        }

        return null;
    }

    public CompositeDisposable getCompositeDisposible() {
        if (mActivity != null) {
            return mActivity.mCompositeDisposable;
        }

        return null;
    }
}
