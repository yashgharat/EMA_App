package com.STIRlab.ema_diary.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.STIRlab.ema_diary.Activities.PinActivity;

public class LifeCycleHelper implements LifecycleObserver {

    private static final String TAG = "LIFE_CYCLE";
    private Context context;
    private SharedPreferences SP;

    public LifeCycleHelper(Context context){
        this.context = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        //Toast.makeText(context, "In Foreground", Toast.LENGTH_SHORT).show();

        if (SP.getString("Pin", null) != null)
            context.startActivity(new Intent(context, PinActivity.class));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        //Toast.makeText(context, "In Background", Toast.LENGTH_SHORT).show();

    }
}
