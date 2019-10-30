package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.STIRlab.ema_diary.Helpers.NotificationHelper;
import com.STIRlab.ema_diary.R;

public class SettingsActivity extends AppCompatActivity {

    private CardView setTime, newPin;
    private TextView back, signOut;
    private LinearLayout layout;

    private SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);

        setTime = findViewById(R.id.timePick);
        newPin = findViewById(R.id.SecureLock);
        back = findViewById(R.id.finishSettings);
        signOut = findViewById(R.id.linkSignOut);
        layout = findViewById(R.id.settingsLayout);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker timePicker = new TimePicker(SettingsActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 20, 20, 20);
                timePicker.setLayoutParams(layoutParams);
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hourofDay, int minute) {
                        NotificationHelper.triggerNotif(SettingsActivity.this, "Test", "This is a test notification",
                                hourofDay, minute);

                        SP.edit().putInt("hour", hourofDay).apply();
                        SP.edit().putInt("minute", minute).apply();

//                        timePicker.setVisibility(View.GONE);
                    }
                });
                timePicker.setVisibility(View.VISIBLE);

                if(layout != null){
                    layout.addView(timePicker);
                }
            }
        });

        newPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, createPinActivity.class);
                startActivityForResult(i, 10);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log our feature added here
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                break;
        }
    }
}
