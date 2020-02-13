package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DailyReminderActivity extends AppCompatActivity {

    private FloatingActionButton prev;

    private TimePicker timePicker;

    private SharedPreferences SP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_reminder);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        prev = findViewById(R.id.daily_reminder_previous);
        timePicker = findViewById(R.id.time_picker);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        timePicker.setIs24HourView(false);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourofDay, int mins) {
                SP.edit().putInt("hour", hourofDay).apply();
                SP.edit().putInt("minute", mins).apply();
            }
        });
    }
}
