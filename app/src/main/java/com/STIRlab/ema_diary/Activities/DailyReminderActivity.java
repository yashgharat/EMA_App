package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CustomTimePicker;
import com.STIRlab.ema_diary.Helpers.NotificationHelper;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DailyReminderActivity extends AppCompatActivity {

    private static final String TAG = "DAILY_REMINDER";
    private FloatingActionButton prev;

    private CustomTimePicker timePicker;

    private NotificationHelper notificationHelper;
    private SharedPreferences SP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_reminder);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        notificationHelper = new NotificationHelper(this);

        prev = findViewById(R.id.daily_reminder_previous);
        timePicker = findViewById(R.id.time_picker);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        timePicker.setIs24HourView(false);
        timePicker.setHour(SP.getInt("hour", 14));
        timePicker.setMinute(SP.getInt("minute", 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourofDay, int mins) { ;
                SP.edit().putInt("hour", hourofDay).apply();
                SP.edit().putInt("minute", mins).apply();
            }
        });
    }

    private String formatTimePicker(int hour, int minute) {
        String hourString = "", minuteString = "";
        String am_pm ="";
        if ((hour >= 1) && (hour <= 11.59)) {
            am_pm = "AM";
        } else if (hour >= 12) {
            hour = hour - 12;
            am_pm = "PM";
        } else if (hour == 0) {
            hour = 12;
            am_pm = "AM";
        }
        if (hour < 10) hourString = "0" + hour;
        else
            hourString = "" + hour;

        if (minute < 10) minuteString = "0" + minute;
        else
            minuteString = "" + minute;

        return hourString + ":" + minuteString + " " + am_pm;
    }

    public void setNotification(Context context){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper.setNotificationOreo(context);
        } else {
            notificationHelper.setNotification(context);
        }
    }

    @Override
    public void onStop() {
        setNotification(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
