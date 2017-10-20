package sg.lifecare.cumii.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import sg.lifecare.cumii.data.DataManager;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.EntityDetailResponse;
import sg.lifecare.cumii.data.server.response.Response;
import sg.lifecare.cumii.ui.CumiiNotification;
import sg.lifecare.cumii.ui.NotificationActivity;
import timber.log.Timber;

public class CumiiFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.d("message_id=%s", remoteMessage.getMessageId());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.d("Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.d("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // check if user still login
        EntityDetailResponse.Data user = DataManager.getInstance(this).getUser();
        if ((user == null) || (TextUtils.isEmpty(user.getId()))) {
            Timber.w("user not login");
            CumiiNotification.removeAlertNotification(this);
            return;
        }

        CumiiNotification.addAlertNotification(this, remoteMessage.getData());

        Map<String, String> data = remoteMessage.getData();
        String type = data.containsKey("type") ? data.get("type") : "";
        String entityId = data.containsKey("entityId") ? data.get("entityId") : "";

        if (TextUtils.isEmpty(entityId)) {
            Timber.w("unknown user");
            return;
        }

        if("10".equalsIgnoreCase(type) || "11".equalsIgnoreCase(type)) {
            NotificationActivity.NotificationData notificationData = new NotificationActivity.NotificationData();
            notificationData.setRuleId(data.containsKey("ruleId") ? data.get("ruleId") : "");
            notificationData.setRuleName(data.containsKey("eventName") ? data.get("eventName") : "");
            notificationData.setRuleEntityId(data.containsKey("entityId") ? data.get("entityId") : "");
            notificationData.setRuleMessage(data.containsKey("message") ? data.get("message") : "");
            notificationData.setRuleWho(data.containsKey("deviceName") ? data.get("deviceName") : "0");
            //notificationData.setRuleEditable(data.getOrDefault("rule_edit_able", false)); //TODO
            notificationData.setCoordinate(data.containsKey("latitude") ? data.get("latitude") :"",
                    data.containsKey("longitude") ? data.get("longitude") :"");
            notificationData.setRuleType(data.containsKey("ruleType") ? data.get("ruleType") : "");
            notificationData.setRulePhone(data.containsKey("callGsm") ? data.get("callGsm") : "");


            if ("10".equalsIgnoreCase(type)) {
                notificationData.setType("P");
                //LifecareSharedPreference.getInstance().addPanicNotification(data.getString("entityId", ""));
            } else if ("11".equalsIgnoreCase(type)) {
                notificationData.setType("A");
                //LifecareSharedPreference.getInstance().addOtherNotification(data.getString("entityId", ""));
            }

            AssistsedEntityResponse.Data member = DataManager.getInstance(this).getMemeberById(entityId);

            if(member != null) {
                String gsmNumber = data.containsKey("callGsm") ? data.get("callGsm") : "";
                if (TextUtils.isEmpty(gsmNumber)) {

                    Response.Phone phone = member.getDefaultPhone();
                    if (phone != null) {
                        notificationData.setRulePhone(phone.getPhoneDigits());
                    }
                }

                Bundle intentData = new Bundle();
                intentData.putSerializable("data", notificationData);

                Intent intent = new Intent(this, NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(intentData);
                startActivity(intent);
            } else {
                //no user profile
                Timber.d("cannot find assisted user");

                //i.setClass(this, LoginActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(i);
            }
        }

    }
}
