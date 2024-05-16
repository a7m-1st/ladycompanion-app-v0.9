package com.example.HarIPart;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ladyapp.R;

import org.checkerframework.checker.units.qual.N;

public class NotificationsAreHere extends ContextWrapper {

    public NotificationsAreHere(Context base) {
        super(base);
        createChannels();
    }

    private String CHANNEL_NAME = "Emergency Unsafe Zone Notifications";
    private String CHANNEL_ID = "com.example.ladyapp"+CHANNEL_NAME;

    private void createChannels() {
        //Channel made for high priority warning notifications
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("These are notifications which are sent when in Unsafe Zones. Ensure these remain enabled for functioning of the app.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    @SuppressLint("MissingPermission")
    public void sendEmergencyNotifications(int notifid, String title, String body, String summary) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.warning_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText(summary)
                        .setBigContentTitle(title).bigText(body))
                .build();
        NotificationManagerCompat.from(this).notify(notifid, notification);
    }

}
