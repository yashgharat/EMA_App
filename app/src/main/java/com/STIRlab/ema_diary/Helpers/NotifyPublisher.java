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

import com.STIRlab.ema_diary.Activities.MainActivity;
import com.STIRlab.ema_diary.Activities.SplashScreen;
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

        Intent notifyIntent = new Intent(context, SplashScreen.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyUser")
                .setContentTitle("Daily Journal Reminder")
                .setContentText("Remember to submit your journal entry today.")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher_foreground))
                .setSmallIcon(R.drawable.ic_border_color_blue_24dp)
                .setContentIntent(notifyPendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, notification);

        Log.i(TAG, "HERE");


    }

}
