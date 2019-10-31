package com.STIRlab.ema_diary.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotifyPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int importance = NotificationManager. IMPORTANCE_HIGH ;
        NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(NOTIFICATION.hashCode()) , "NOTIFICATION_CHANNEL_NAME" , importance) ;
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel) ;
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}