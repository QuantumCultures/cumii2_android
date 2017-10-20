package sg.lifecare.cumii.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.DataManager;
import sg.lifecare.cumii.data.server.data.AcknowledgeData;
import sg.lifecare.cumii.data.server.data.AlertRuleData;
import sg.lifecare.cumii.data.server.response.EntityDetailResponse;
import timber.log.Timber;

public class NotificationActivity extends FragmentActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {

    @BindView(R.id.name_text)
    TextView mNameText;

    @BindView(R.id.title_text)
    TextView mTitleText;

    @BindView(R.id.message_text)
    TextView mMessageText;

    @BindView(R.id.more_action_label)
    TextView mActionLabel;

    @BindView(R.id.map_frame)
    View mMapFrame;

    @BindView(R.id.main_frame)
    View mMainFrame;

    @BindView(R.id.enable_away_mode_frame)
    View mEnableAwayModeFrame;

    @BindView(R.id.disable_alert_frame)
    View mDisableAlertFrame;

    @BindView(R.id.call_button)
    Button mCallButton;

    private NotificationData mNotificationData;
    private CompositeDisposable mDisposable;
    private DataManager mDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        ButterKnife.bind(this);

        mDisposable = new CompositeDisposable();
        mDataManager = DataManager.getInstance(this);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            mNotificationData = (NotificationData) data.get("data");
        }

        if (mNotificationData == null) {
            Timber.w("no data");
            finish();

            return;
        }

        mNameText.setText(mNotificationData.mRuleWho);
        mTitleText.setText(mNotificationData.mRuleName);
        mMessageText.setText(mNotificationData.mRuleMessage);

        if (TextUtils.isEmpty(mNotificationData.mRulePhone)) {
            //mCallButton.setVisibility(View.INVISIBLE);
            //mCallButton.setClickable(false);
        }
        setupView();
    }

    private void setupView() {
        if ("P".equalsIgnoreCase(mNotificationData.mType)) {
            adjustForPanicEvent();

            mEnableAwayModeFrame.setVisibility(View.GONE);
            mDisableAlertFrame.setVisibility(View.GONE);
        } else if ("A".equalsIgnoreCase(mNotificationData.mType)) {

            if ("on-activity".equalsIgnoreCase(mNotificationData.mType)) {
                mEnableAwayModeFrame.setVisibility(View.GONE);
            }
        }

        if ("disarm".equalsIgnoreCase(mNotificationData.mRuleArmState)) {
            mDisableAlertFrame.setVisibility(View.GONE);
        } else if ("disarm-auto".equalsIgnoreCase(mNotificationData.mRuleArmState)) {
            mEnableAwayModeFrame.setVisibility(View.GONE);
        }

    }

    private void adjustForPanicEvent() {
        if("P".equalsIgnoreCase(mNotificationData.mType)) {
            mMainFrame.setBackgroundColor(Color.parseColor("#ddc60a3d"));
            mTitleText.setText(getString(R.string.label_panic));
            mMessageText.setText(getString(R.string.message_panic, mNotificationData.mRuleWho));

            if ((mNotificationData.mLatitude > 0) || (mNotificationData.mLongtitude > 0)) {
                mMapFrame.setVisibility(View.VISIBLE);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }

            mActionLabel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if ((mDisposable != null) && (!mDisposable.isDisposed())) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if ((mNotificationData.mLatitude > 0) && (mNotificationData.mLongtitude > 0)) {
            if("P".equalsIgnoreCase(mNotificationData.mType)) {
                LatLng location = new LatLng(mNotificationData.mLatitude, mNotificationData.mLongtitude);
                googleMap.addMarker(new MarkerOptions().position(location).title(mNotificationData.mRuleWho));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            }
        }
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClick() {
        finish();
    }

    @OnClick(R.id.call_button)
    public void onCallButtonClick() {
        showCallDialog();
    }

    @OnClick(R.id.enable_away_mode_frame)
    public void onSetEnableAwayModeFrameClick() {
        showAwayDialog();
    }

    @OnClick(R.id.disable_alert_frame)
    public void onSetDisableAlertFrameClick() {
        showDiarmDialog();
    }

    private void showCallDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setMessage(getResources().getString(R.string.message_call_out, mNotificationData.mRuleWho));
        builder.setPositiveButton(getString(R.string.action_call),
                (dialogInterface, i) -> {
                    mCallButton.setClickable(false);

                    EntityDetailResponse.Data user = mDataManager.getUser();
                    if (user == null) {
                        finish();
                        return;
                    }

                    AcknowledgeData acknowledgeData = new AcknowledgeData(mNotificationData.mRuleEntityId,
                            user.getId(), mNotificationData.mRuleId);

                    mDisposable.add(mDataManager.getCumiiService().postAcknowledge(acknowledgeData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                Timber.d("done");
                                finish();
                            }, throwable -> {
                                Timber.e(throwable, throwable.getMessage());
                                finish();
                            }));
                });
        builder.setNegativeButton(getString(R.string.action_cancel),
                (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private void showAwayDialog() {
      showAlertDialog(getString(R.string.message_away_mode), "disarm-auto");
    }

    private void showDiarmDialog() {
        showAlertDialog(getString(R.string.message_disarm_mode), "disarm");
    }

    private void showAlertDialog(final String message, final String armState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.action_ok),
                (dialogInterface, i) -> {
                    mCallButton.setClickable(false);

                    EntityDetailResponse.Data user = mDataManager.getUser();
                    if (user == null) {
                        finish();
                        return;
                    }

                    AlertRuleData ruleData = new AlertRuleData();
                    ruleData.setEntityId(mNotificationData.mRuleEntityId);
                    ruleData.setRuleId(mNotificationData.mRuleId);
                    ruleData.setArmState(armState);
                    ruleData.setRuleEditEntityId(user.getId());


                    mDisposable.add(mDataManager.getCumiiService().postEditAlertRule(ruleData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                Timber.d("done");
                                finish();
                            }, throwable -> {
                                Timber.e(throwable, throwable.getMessage());
                                finish();
                            }));
                });
        builder.setNegativeButton(getString(R.string.action_cancel),
                (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    public static class NotificationData implements Serializable {
        String mType;
        String mRuleId;
        String mRuleName;
        String mRuleArmState;
        String mRuleEntityId;
        String mRuleType;
        String mRuleMessage;
        boolean mRuleEditable;
        String mRuleWho;
        String mRulePhone;
        double mLatitude = -1;
        double mLongtitude = -1;

        public void setType(String type) {
            mType = type;
        }

        public void setRuleId(String ruleId) {
            mRuleId = ruleId;
        }

        public void setRuleName(String ruleName) {
            mRuleName = ruleName;
        }

        public void setRuleArmState(String state) {
            mRuleArmState = state;
        }

        public void setRuleEntityId(String id) {
            mRuleEntityId = id;
        }

        public void setRuleType(String type) {
            mRuleType = type;
        }

        public void setRuleMessage(String message) {
            mRuleMessage = message;
        }

        public void setRuleEditable(boolean editable) {
            mRuleEditable = editable;
        }

        public void setRuleWho(String who) {
            mRuleWho = who;
        }

        public void setRulePhone(String phone) {
            mRulePhone = phone;
        }

        public void setCoordinate(String latitude, String longitude) {
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                try  {
                    mLatitude = Double.parseDouble(latitude);
                    mLongtitude = Double.parseDouble(longitude);
                } catch (NumberFormatException e) {
                    Timber.e(e.getMessage(), e);
                }
            }
        }
    }
}
