package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "SETTINGS";

    private CardView setTime, newPin;
    private TextView signOut;
    private FloatingActionButton previous;
    private SwitchCompat switchCompat;

    private LinearLayout layout;

    private CognitoSettings cognitoSettings;

    private SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        cognitoSettings = new CognitoSettings(this);

        setTime = findViewById(R.id.time_pick);
        newPin = findViewById(R.id.secure_lock);
        previous = findViewById(R.id.settings_previous);
        signOut = findViewById(R.id.settings_sign_out);
        //switchCompat = findViewById(R.id.switchTheme);
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
                Intent i = new Intent(SettingsActivity.this, CreatePinActivity.class);
                startActivityForResult(i, 10);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
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


        //TODO: Add dark mode

//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
//            switchCompat.setChecked(true);

//        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    SwitchTheme.getInstance(SettingsActivity.this).setIsDark(true);
//                    Intent intent = getIntent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    finish();
//                    startActivity(intent);
//
//                } else {
//                    SwitchTheme.getInstance(SettingsActivity.this).setIsDark(false);
//                    Intent intent = getIntent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    finish();
//                    startActivity(intent);
//                }
//
//
//            }
//        });

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
