package sg.lifecare.cumii.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Map;

import sg.lifecare.cumii.R;
import sg.lifecare.cumii.ui.dashboard.DashboardActivity;

public class CumiiNotification {

    public static void removeAlertNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void addAlertNotification(Context context, Map<String, String> data) {
        Intent notificationIntent = new Intent(context, DashboardActivity.class);
        String type = data.containsKey("type") ? data.get("type") : "";
        String entityId = data.containsKey("entityId") ? data.get("entityId") : "";
        String message = data.containsKey("message") ? data.get("message") : "";
        String deviceName = data.containsKey("deviceName") ? data.get("deviceName") : "";

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("notification", true);
        notificationIntent.putExtra("push_type", type);
        notificationIntent.putExtra("push_entity_id", entityId);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);
        if(deviceName != null && !deviceName.isEmpty()) {
            title = title + " - " + deviceName;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setWhen(when)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
