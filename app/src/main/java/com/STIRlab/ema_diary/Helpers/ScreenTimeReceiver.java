package com.STIRlab.ema_diary.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ScreenTimeReceiver extends BroadcastReceiver {
    private static final String TAG = "SCREENTIME";

    SharedPreferences SP;
    @Override
    public void onReceive(Context context, Intent intent) {

        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        switch(intent.getAction()){
            case Intent.ACTION_SCREEN_ON:
                SP.edit().putLong("screen_on", System.currentTimeMillis()).apply();
                Log.i(TAG, "Screen is on: ");
                break;
            case Intent.ACTION_SCREEN_OFF:
                SP.edit().putLong("screen_off", System.currentTimeMillis()).apply();

                long begin = SP.getLong("screen_on", -1);
                long end = SP.getLong("screen_off", -1);

                long totalTime = end - begin;

                long curTime = SP.getLong("total_screen_time", 0);
                curTime += totalTime;

                SP.edit().putLong("total_screen_time", curTime).apply();
                Log.i(TAG, "Screen is off: " + curTime);
                break;
        }
    }
}
