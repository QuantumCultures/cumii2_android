package sg.lifecare.cumii.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.CumiiUtil;
import sg.lifecare.cumii.data.server.response.LoginResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.ui.base.BaseActivity;
import sg.lifecare.cumii.ui.dashboard.DashboardActivity;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText mEmailEdit;
    @BindView(R.id.password)
    EditText mPasswordEdit;
    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    Button mLoginButton;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.email_sign_in_button)
    void onLoginClick() {

        hideKeyboard();

        String email = mEmailEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showErrorDialog(R.string.error_email_empty);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showErrorDialog(R.string.error_password_empty);
            return;
        }

        login(email, password);
    }

    private void showErrorDialog(@StringRes int msg) {
        showErrorDialog(getString(msg));
    }

    private void showErrorDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.action_ok, null);
        builder.show();
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void login(String email, String password) {
        showProgress(true);
        mLoginButton.setClickable(false);

        Observable<LoginResponse> logingObservable = mDataManager.getCumiiService().login(
                email, password,
                mDataManager.getPreferences().getFcmToken(),
                mDataManager.getPreferences().getDeviceId(),
                "A");

        mCompositeDisposable.add(
            logingObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(loginResponse -> {

                        showProgress(false);

                        if (loginResponse.isError()) {
                            mLoginButton.setClickable(true);
                            showErrorDialog(loginResponse.getErrorDesc());
                        } else {
                            mDataManager.getPreferences().setEntityId(loginResponse.getData().getId());
                            goToDashboardActivity();
                        }
                    }, throwable -> {
                        showProgress(false);
                        mLoginButton.setClickable(true);

                        Timber.e(throwable, throwable.getMessage());

                        if (throwable instanceof HttpException) {
                            Type type = new TypeToken<LoginResponse>() {}.getType();
                            Response response = CumiiUtil.getResponse(
                                    ((HttpException) throwable).response().errorBody(), type);

                            //Timber.d("error => %s ", ((HttpException) throwable).response().errorBody().string());

                            if ((response != null) && !TextUtils.isEmpty(response.getErrorDesc())) {
                                showErrorDialog(response.getErrorDesc());
                            } else {
                                showErrorDialog(R.string.error_login_internet);
                            }

                        } else if (throwable instanceof SocketTimeoutException) {
                            showErrorDialog(R.string.error_login_internet);
                        } else {
                            Timber.e(throwable, throwable.getMessage());
                        }

                    }));
    }

    private void goToDashboardActivity() {
        Intent intent = DashboardActivity.getStartIntent(this);
        startActivity(intent);
        finish();
    }
}
