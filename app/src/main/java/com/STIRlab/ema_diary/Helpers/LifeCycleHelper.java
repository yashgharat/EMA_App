package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.STIRlab.ema_diary.Activities.AuthenticationActivity;
import com.STIRlab.ema_diary.Activities.PinActivity;

import static com.STIRlab.ema_diary.Helpers.APIHelper.isNetworkAvailable;

public class LifeCycleHelper implements LifecycleObserver {

    private static final String TAG = "LIFE_CYCLE";
    private Context context;
    private SharedPreferences SP;

    public static boolean flag = true;

    public LifeCycleHelper(Context context) {
        this.context = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        //Toast.makeText(context, "In Foreground", Toast.LENGTH_SHORT).show();

        if (!isNetworkAvailable(context))
            context.startActivity(new Intent(context, AuthenticationActivity.class));
        else if (SP.getString("Pin", null) != null && flag)
            context.startActivity(new Intent(context, PinActivity.class));

        LifeCycleHelper.flag = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        //Toast.makeText(context, "In Background", Toast.LENGTH_SHORT).show();
    }
}
