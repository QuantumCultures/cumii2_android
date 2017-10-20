package sg.lifecare.cumii.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sg.lifecare.cumii.CumiiApp;
import sg.lifecare.cumii.CumiiConfig;
import sg.lifecare.cumii.data.DataManager;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.util.NetworkUtils;
import timber.log.Timber;

// TODO:
// save topics

public class CumiiMqttService extends Service implements MqttCallbackExtended {

    public static final String ACTION_ZWAVE_STATUS = "zwave_status";

    private MqttAndroidClient mClient;
    private MqttConnectOptions mOptions;
    private NetworkReceiver mNetworkReceiver;

    private HashMap<String, String> mCameraTopics = new HashMap<>();
    private HashMap<String, String> mZwaveTopics = new HashMap<>();

    private final IBinder mBinder = new LocalBinder();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, CumiiMqttService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNetworkReceiver = new NetworkReceiver();

        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        DataManager dataManager = ((CumiiApp)getApplication()).getDataManager();
        String entityId = dataManager.getPreferences().getEntityId();

        if (TextUtils.isEmpty(entityId)) {
            Timber.w("No user");
            stopSelf();
            return;
        }

        String clientId = getClientId(entityId);
        Timber.d("client_id=%s", clientId);

        mClient = new MqttAndroidClient(getApplicationContext(), CumiiConfig.MQTT_URL, clientId);
        mClient.setCallback(this);

        mOptions = new MqttConnectOptions();
        mOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        mOptions.setKeepAliveInterval(50);
        mOptions.setAutomaticReconnect(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Timber.d("onStartCommand");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.d("onBind");
        return mBinder;
    }

    private String getClientId(String clientEntityId) {
        return clientEntityId + "@" + UUID.randomUUID().toString();
    }

    public synchronized void connect() {

        Timber.d("connect");

        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            Timber.d("connect: no network");
            return;
        }

        if (mClient.isConnected()) {
            Timber.d("connect: client still connected");
            return;
        }

        try {
            IMqttToken token = mClient.connect(mOptions);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Timber.d("connect: onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Timber.d("connect: onFailure");
                    Timber.d(exception, exception.getMessage());
                }
            });

        } catch (MqttException ex) {
            Timber.e(ex, ex.getMessage());
        }
    }

    private String getCameraStatusTopic(String entityId, String gatewayId) {
        return entityId + "/gateway/" + gatewayId + "/camera/+/status";
    }

    private String getZwaveStatusTopic(String entityId, String gatewayId) {
        return entityId + "/gateway/" + gatewayId + "/zwave/status";
    }

    public void subscribeCameraTopics(String entityId, String gatewayId) {
        Timber.d("subscribeToCameras");

        unsubscribeCameraTopics();

        String topic = getCameraStatusTopic(entityId, gatewayId);
        Timber.d("subscribeCameraTopics: topic=%s", topic);

        mCameraTopics.put(topic, topic);

        List<String> topicList = new ArrayList<>(mCameraTopics.keySet());
        subscribeTopics(topicList);
    }

    public void subscribeZwaveTopics(String entityId, String gatewayId) {
        Timber.d("subscribeToCameras");

        unsubscribeZwaveTopics();

        String topic = getZwaveStatusTopic(entityId, gatewayId);
        Timber.d("subscribeZwaveTopics: topic=%s", topic);

        mZwaveTopics.put(topic, topic);

        List<String> topicList = new ArrayList<>(mZwaveTopics.keySet());
        subscribeTopics(topicList);
    }

    public void unsubscribeCameraTopics() {
        if (mCameraTopics.size() > 0) {

            if (mClient.isConnected()) {
                List<String> topicList = new ArrayList<>(mCameraTopics.keySet());
                unsubscribeTopics(topicList);
            }

            mCameraTopics.clear();
        }
    }

    public void unsubscribeZwaveTopics() {
        if (mZwaveTopics.size() > 0) {

            if (mClient.isConnected()) {
                List<String> topicList = new ArrayList<>(mZwaveTopics.keySet());
                unsubscribeTopics(topicList);
            }

            mZwaveTopics.clear();
        }
    }

    private void subscribeTopics(List<String> topics) {

        if (!mClient.isConnected()) {
            Timber.d("subscribeTopics: client not connected");
            return;
        }

        if (topics.size() > 0) {
            int[] qos = new int[topics.size()];
            String[] subTopics = new String[topics.size()];

            for (int i = 0; i < topics.size(); i++) {
                qos[i] = 0;
                subTopics[i] = topics.get(i);
            }

            try {
                IMqttToken token = mClient.subscribe(subTopics, qos);

                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Timber.d("subscribeTopics: onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Timber.d("subscribeTopics: onFailure");
                    }
                });
            } catch (MqttException ex) {
                Timber.e(ex, ex.getMessage());
            }
        }
    }

    private void unsubscribeTopics(List<String> topics) {
        if (!mClient.isConnected()) {
            Timber.d("unsubscribeTopics: client not connected");
            return;
        }

        if (topics.size() > 0) {
            String[] subTopics = new String[topics.size()];

            for (int i = 0; i < topics.size(); i++) {
                subTopics[i] = topics.get(i);
            }

            try {
                IMqttToken token = mClient.unsubscribe(subTopics);

                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Timber.d("unsubscribeTopics: onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Timber.d("unsubscribeTopics: onFailure");
                    }
                });
            } catch (MqttException ex) {
                Timber.e(ex, ex.getMessage());
            }
        }
    }



    @Override
    public void connectComplete(boolean reconnect, String server) {
        Timber.d("connectComplete: reconnect=%b, server=%s", reconnect, server);
    }

    @Override
    public void connectionLost(Throwable cause) {
        Timber.d("connectionLost");

        Timber.e(cause, cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Timber.d("messageArrived:");
        Timber.d("    topic: %s", topic);
        Timber.d("  message: %s", message.toString());

        if (topic.contains("/zwave/")) {
            Timber.d("publish zwave status");
            Intent intent = new Intent(ACTION_ZWAVE_STATUS);
            intent.putExtra("message", message.toString());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Timber.d("deliveryComplete");
    }

    //private void sendMessage(int state) {
        //Intent intent = new Intent(ACTION_STATE);
        //intent.putExtra("state", state);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    //}

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("NetworkReceiver: onReceive");

            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");

            wl.acquire();

            if (NetworkUtils.isNetworkConnected(context)) {
                //connect();
            }

            wl.release();
        }
    }

    public class LocalBinder extends Binder {
        public CumiiMqttService getService() {
            return CumiiMqttService.this;
        }
    }
}
