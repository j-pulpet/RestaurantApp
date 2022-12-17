package com.foodapp.app.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.foodapp.app.R;
import com.foodapp.app.activity.DashboardActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInstanceIDListenerService extends FirebaseMessagingService {

    private static final int REQUEST_CODE = 1;
    private static int NOTIFICATION_ID = 6578;

    public MyInstanceIDListenerService() {
        super();
    }

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Log.e("getData", new Gson().toJson(remoteMessage.toString()));
            JSONObject object =new JSONObject(new Gson().toJson(remoteMessage.getNotification()));
            Log.e("isPeram",object.toString());
            String title =object.getString("title");
            String message =object.getString("body");
            showNotifications(getString(R.string.app_name), title, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotifications(String title, String title1, String message) {

        Intent intent = new Intent(this, DashboardActivity.class);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            manager.createNotificationChannel(mChannel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(getIcon())
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(title1)
                    .setContentText(message);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            manager.notify(NOTIFICATION_ID, mBuilder.build());

        } else {

            PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentText(message)
                    .setContentTitle(title1)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(getIcon())
                    .build();

            manager.notify(NOTIFICATION_ID, notification);
        }

        NOTIFICATION_ID++;
    }

    private int getIcon() {
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.ic_small_notification : R.drawable.ic_small_notification;
        return icon;
    }
}