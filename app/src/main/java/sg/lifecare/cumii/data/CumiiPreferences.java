package sg.lifecare.cumii.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sg.lifecare.cumii.util.CookieUtils;
import timber.log.Timber;

public class CumiiPreferences {

    private static final String PREF_FILE = "cumii_file";

    private static final String KEY_DEVICE_ID = "DEVICE_ID";
    private static final String KEY_FCM_TOKEN = "FCM_TOKEN";
    private static final String KEY_ENTITY_ID = "ENTITY_ID";

    private static final String GCM_SENDER_ID = "1076112719492";

    private final SharedPreferences mPreferences;

    CumiiPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);


        if (TextUtils.isEmpty(getDeviceId())) {
            setDeviceId(InstanceID.getInstance(context).getId());
        }

        if (TextUtils.isEmpty(getFcmToken()) && checkGooglePlayServices(context)) {
            Observable.create((ObservableEmitter<String> aSubscriber) -> {
                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    String token = instanceID.getToken(GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    aSubscriber.onNext(token);
                } catch (IOException e) {
                            Timber.e(e, e.getMessage());
                    //aSubscriber.onError(e);
                    aSubscriber.onNext("");
                }
                aSubscriber.onComplete();
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setFcmToken,
                            throwable -> Timber.e(throwable, throwable.getMessage()));
        }
    }

    private boolean checkGooglePlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);

        Timber.d("checkGooglePlayServices: %b", resultCode == ConnectionResult.SUCCESS);

        return resultCode == ConnectionResult.SUCCESS;
    }

    public void clear(Context context) {
        CookieUtils.getCookieJar(context).clear();
        setEntityId("");
    }

    public String getDeviceId() {
        return mPreferences.getString(KEY_DEVICE_ID, null);
    }

    public String getFcmToken() {
        return mPreferences.getString(KEY_FCM_TOKEN, null);
    }

    public String getEntityId() {
        return mPreferences.getString(KEY_ENTITY_ID, null);
    }

    private void setDeviceId(String deviceId) {
        mPreferences.edit().putString(KEY_DEVICE_ID, deviceId).apply();
    }

    private void setFcmToken(String token) {
        mPreferences.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public void setEntityId(String entityId) {
        mPreferences.edit().putString(KEY_ENTITY_ID, entityId).apply();
    }

}
