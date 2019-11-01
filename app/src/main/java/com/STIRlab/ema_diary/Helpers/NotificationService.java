package com.STIRlab.ema_diary.Helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.STIRlab.ema_diary.R;

public class NotificationService {
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channels";
    private static final int NOTIFICATION_ID = 0;
    public static final String TAG = "Service";

    private  Context context;

    public static NotificationManager mNotifyManager;


    public NotificationService(Context context){
        this.context = context;
    }

    public void sendNotification(int hour, int min)
    {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        //cancelNotification(context, NotifyPublisher.class);

        Intent notificationIntent = new Intent(context, NotifyPublisher.class);
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION, notifyBuilder.build());
        notificationIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long time = setcalendar.getTimeInMillis() - System.currentTimeMillis();

        Log.i(TAG,String.valueOf(time));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent);
        long nextTime = alarmManager.getNextAlarmClock().getTriggerTime() - System.currentTimeMillis();

        Log.i(TAG, "next time: " + String.valueOf(nextTime));


        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        //mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public static void cancelNotification(Context context,Class<?> cls)
    {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void createNotificationChannel()
    {
        mNotifyManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                "Test notification", NotificationManager
                .IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("test notification");
        mNotifyManager.createNotificationChannel(notificationChannel);
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_border_color_blue_24dp);

        return notifyBuilder;
    }
}