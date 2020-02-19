package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CustomTimePicker;
import com.STIRlab.ema_diary.Helpers.NotificationHelper;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DailyReminderActivity extends AppCompatActivity {

    private static final String TAG = "DAILY_REMINDER";
    private FloatingActionButton prev;

    private CustomTimePicker timePicker;
    private Button btnNotif;

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
        btnNotif = findViewById(R.id.button_notification);

        btnNotif.setBackgroundColor(getColor(R.color.disabled));
        btnNotif.setTextColor(getColor(R.color.apparent));
        btnNotif.setEnabled(false);


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationHelper.setNotificationOreo(DailyReminderActivity.this);
                } else {
                    notificationHelper.setNotification(DailyReminderActivity.this);
                }
                btnNotif.setBackgroundColor(getColor(R.color.disabled));
                btnNotif.setTextColor(getColor(R.color.apparent));
                btnNotif.setEnabled(false);
            }
        });

        timePicker.setIs24HourView(false);
        timePicker.setHour(SP.getInt("hour", 14));
        timePicker.setMinute(SP.getInt("minute", 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourofDay, int mins) {
                btnNotif.setBackgroundColor(getColor(R.color.primaryDark));
                btnNotif.setTextColor(getColor(R.color.themeBackground));
                btnNotif.setEnabled(true);
                SP.edit().putInt("hour", hourofDay).apply();
                SP.edit().putInt("minute", mins).apply();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

    }
}
