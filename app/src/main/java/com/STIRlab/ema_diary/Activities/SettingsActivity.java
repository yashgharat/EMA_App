package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "SETTINGS";

    private CardView setTime, newPin;
    private TextView back, signOut;
    private LinearLayout layout;

    private CognitoSettings cognitoSettings;

    private SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        cognitoSettings = new CognitoSettings(this);

        setTime = findViewById(R.id.timePick);
        newPin = findViewById(R.id.SecureLock);
        back = findViewById(R.id.finishSettings);
        signOut = findViewById(R.id.linkSignOut);
        layout = findViewById(R.id.settingsLayout);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int curHour = cldr.get(Calendar.HOUR_OF_DAY);
                int curMinutes = cldr.get(Calendar.MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourofDay, int mins) {
                        Log.i(TAG, String.valueOf(hourofDay));
                        SP.edit().putInt("hour", hourofDay).apply();
                        SP.edit().putInt("minute", mins).apply();

                    }
                }, curHour, curMinutes, false);
                timePicker.show();
                
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
                cognitoSettings.getUserPool().getUser(SP.getString("email", "null")).globalSignOutInBackground(new GenericHandler() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Logged out");
                        SP.edit().clear().apply();
                        Intent i = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });

                SP.edit().clear().apply();
                Intent i = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                startActivity(i);

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

    @Override
    public void onBackPressed(){
        finish();
    }
}
