package com.STIRlab.ema_diary.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.STIRlab.ema_diary.Activities.MainActivity;


public class NotifyPublisher extends BroadcastReceiver {
    public static final String TAG = "Publisher";
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";


    private SharedPreferences SP;


    @Override
    public void onReceive(Context context, Intent intent) {
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        NotificationManager notificationManager = NotificationService.mNotifyManager;

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        Log.i(TAG, "HERE");

        notificationManager.notify(id, notification);
    }
}
