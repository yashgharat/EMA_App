package com.STIRlab.ema_diary.Helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationHelper {

    private static final String TAG = "NOTIFICATION_HELPER";
    private final int DAY_IN_MS = 86400000;
    private Context context;
    private SharedPreferences SP;

    public NotificationHelper(Context context) {
        this.context = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNotificationOreo() {

        createNotificationChannel(context);

        int hour = SP.getInt("hour", 14);
        int min = SP.getInt("minute", 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        Intent i = new Intent(context, NotifyPublisher.class);

        if (PendingIntent.getBroadcast(context, 200, i,
                PendingIntent.FLAG_NO_CREATE) == null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 200, i, PendingIntent.FLAG_CANCEL_CURRENT);

            Log.i(TAG, hour + ":" + min);
            Log.i(TAG, String.valueOf(calendar.getTimeInMillis() - System.currentTimeMillis()));

            long notifTime = calendar.getTimeInMillis();

            if (notifTime - System.currentTimeMillis() < 0)
                notifTime += DAY_IN_MS;

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notifTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public void setNotification() {

        int hour = SP.getInt("hour", 14);
        int min = SP.getInt("minute", 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        Intent updateServiceIntent = new Intent(context, NotifyPublisher.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled. " + CognitoSettings.formatException(e));
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
        calendar.set(java.util.Calendar.MINUTE, min);
        calendar.set(java.util.Calendar.SECOND, 0);

        Intent i = new Intent(context, NotifyPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 200, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, hour + ":" + min);
        Log.i(TAG, String.valueOf(calendar.getTimeInMillis() - System.currentTimeMillis()));

        long notifTime = calendar.getTimeInMillis();

        if (notifTime - System.currentTimeMillis() < 0)
            notifTime += DAY_IN_MS;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notifTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(Context context) {

        NotificationChannel notificationChannel = new NotificationChannel("notifyUser",
                "Daily notification", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.CYAN);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("test notification");

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager.getNotificationChannel("notifyUser") == null)
            notificationManager.createNotificationChannel(notificationChannel);
    }
}
