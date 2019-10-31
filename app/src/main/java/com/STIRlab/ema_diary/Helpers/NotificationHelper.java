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

import com.STIRlab.ema_diary.R;

import java.util.Calendar;

public class NotificationHelper {

    private final static String test_notification_channel = "Test";
    private Context context;
    private String channel_ID;

    public NotificationHelper(Context context){
        this.context = context;
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_test";
            String description = "context is a test channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel_ID = String.valueOf(name.hashCode());
            NotificationChannel channel = new NotificationChannel(channel_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after context
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( context, NotifyPublisher.class ) ;
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(NotifyPublisher.NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( context, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock.elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    public Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_ID) ;
        builder.setContentTitle( "Test Notification" )
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_border_color_blue_24dp)
            .setChannelId(channel_ID);
        return builder.build() ;
    }
}
