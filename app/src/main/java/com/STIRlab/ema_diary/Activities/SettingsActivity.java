package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.Set;

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

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SettingsActivity.this, DailyReminderActivity.class), 10);
            }
        });

        newPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SettingsActivity.this, SecureLockActivity.class), 10);
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

                new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogStyle)
                        .setTitle("Sign out of 30 Days?")
                        .setCancelable(false)
                        .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();


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

    @Override
    public void onResume(){
        super.onResume();
        if(SP.getString("Pin", null) != null)
            startActivity(new Intent(this, PinActivity.class));

    }
}
