package com.STIRlab.ema_diary.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.STIRlab.ema_diary.Activities.MainActivity;


public class NotifyPublisher extends BroadcastReceiver {
    public static final String TAG = "Publisher";

    private SharedPreferences SP;


    @Override
    public void onReceive(Context context, Intent intent) {
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");

                int hour = SP.getInt("hour", 0);
                int minute = SP.getInt("minute", 0);

                NotificationService.setReminder(context, NotifyPublisher.class, hour, minute);
                return;
            }
        }

        Log.d(TAG, "onReceive: ");

        //Trigger the notification
        NotificationService.showNotification(context, MainActivity.class,
                "30 Days", "Don't forget your daily journal entry");
    }
}
