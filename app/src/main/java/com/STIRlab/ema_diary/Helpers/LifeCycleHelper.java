package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.STIRlab.ema_diary.Activities.AuthenticationActivity;
import com.STIRlab.ema_diary.Activities.PinActivity;
import com.STIRlab.ema_diary.R;

import static com.STIRlab.ema_diary.Helpers.APIHelper.isNetworkAvailable;

public class LifeCycleHelper implements LifecycleObserver {

    private static final String TAG = "LIFE_CYCLE";
    private Context context;
    private SharedPreferences SP;

    private AlertDialog userDialog;

    ConnectivityHelper connectivityHelper;

    public static boolean flag = true;

    public LifeCycleHelper(Context context) {
        this.context = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        connectivityHelper = new ConnectivityHelper(context);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        connectivityHelper.changeListener(true);

        if(!isNetworkAvailable(context))
            showDialogMessage("Low Internet", "There seems to be no internet connection. Please check your network settings.");
        else if (SP.getString("Pin", null) != null && flag)
            context.startActivity(new Intent(context, PinActivity.class));

        LifeCycleHelper.flag = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        connectivityHelper.changeListener(false);
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
}
