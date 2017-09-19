package sg.lifecare.cumii.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.Interceptor;
import retrofit2.HttpException;
import sg.lifecare.cumii.CumiiApp;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.DataManager;
import sg.lifecare.cumii.ui.LoginActivity;

public class BaseActivity extends AppCompatActivity {

    protected CompositeDisposable mCompositeDisposable;
    protected DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();
        mDataManager = ((CumiiApp)getApplication()).getDataManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void goToLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(this);
        startActivity(intent);

        finish();
    }

    public void showNetworkError(@StringRes int positiveButton, DialogInterface.OnClickListener positiveButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setMessage(R.string.error_network_retry);
        builder.setPositiveButton(positiveButton, positiveButtonListener);
        if (positiveButtonListener != null) {
            builder.setNegativeButton(R.string.action_cancel, null);
        }

        builder.show();
    }

    public void showNetworkError() {
        showNetworkError(R.string.action_ok, null);
    }

    public void showServerError(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.action_ok, (dialogInterface, i) -> finish());
        builder.show();
    }

}
