package com.STIRlab.ema_diary.Helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.STIRlab.ema_diary.Activities.MainActivity;
import com.STIRlab.ema_diary.R;

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

    public void triggerNotif(){
        String notificationTitle = "Test Notification";
        String notificationText = "This is a test Notification";

        this.buildNotif();


        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, test_notification_channel)
                .setSmallIcon(R.drawable.ic__ionicons_svg_ios_create)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(10, notificationBuilder.build());
    }

}
