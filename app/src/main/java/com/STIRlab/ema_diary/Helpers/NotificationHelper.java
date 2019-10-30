package com.STIRlab.ema_diary.Helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.STIRlab.ema_diary.Activities.MainActivity;
import com.STIRlab.ema_diary.R;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationHelper {

    private final static String test_notification_channel = "Test";
    private Context context;

    public NotificationHelper(Context context){
        this.context = context;
    }

    public void buildNotif(){
        int importance = -1;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //define the importance level of the notification
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }
        NotificationChannel channel =
                new NotificationChannel(test_notification_channel, test_notification_channel, importance);

        String description = "A channel which tests notifications";
        channel.setDescription(description);

        channel.setLightColor(Color.CYAN);

        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification getNotif(String title, String content){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, test_notification_channel)
                .setSmallIcon(R.drawable.ic__ionicons_svg_ios_create)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return notificationBuilder.build();
    }

    private void scheduleNotification(Notification notification, int hour, int minute) {
        Intent notificationIntent = new Intent(context, NotifyPublisher.class);
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    public static void triggerNotif(Context context, String title, String text, int hour, int minute){
        NotificationHelper helper = new NotificationHelper(context);

        helper.scheduleNotification(helper.getNotif(title, text), hour, minute);
    }

}
