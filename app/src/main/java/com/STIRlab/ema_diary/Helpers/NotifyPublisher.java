package com.STIRlab.ema_diary.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.STIRlab.ema_diary.R;


public class NotifyPublisher extends BroadcastReceiver {
    public static final String TAG = "Publisher";
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";

    private NotificationManager notificationManager;


    private SharedPreferences SP;


    @Override
    public void onReceive(Context context, Intent intent) {

        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyUser")
                .setContentTitle("Daily Journal Reminder")
                .setContentText("Remember to submit your journal entry today.")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.ic_border_color_blue_24dp);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());

        Log.i(TAG, "HERE");


    }

}
