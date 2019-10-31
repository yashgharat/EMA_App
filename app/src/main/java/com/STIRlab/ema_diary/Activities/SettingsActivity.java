package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.NotificationHelper;
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

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);

        cognitoSettings = new CognitoSettings(this);

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
                cognitoSettings.getUserPool().getUser(SP.getString("email", "null")).globalSignOutInBackground(new GenericHandler() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Logged out");
                        SP.edit().clear();
                        Intent i = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });


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
