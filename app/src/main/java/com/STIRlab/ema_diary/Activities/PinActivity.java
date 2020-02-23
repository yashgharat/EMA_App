package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class PinActivity extends AppCompatActivity {
    private final String TAG = "PIN_ACTIVITY";

    private SharedPreferences SP;
    private String localPin;
    private AlertDialog userDialog;
    private TextView title, signOut;
    private CognitoSettings cognitoSettings;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        cognitoSettings = new CognitoSettings(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        title = findViewById(R.id.title_pin);
        signOut = findViewById(R.id.pin_signout);

        localPin = SP.getString("Pin", null);

        title.setText("Enter Pin");

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PinActivity.this, R.style.AlertDialogStyle)
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
                                Intent intent = new Intent(PinActivity.this, AuthenticationActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        CirclePinField inputPin = findViewById(R.id.pin_field);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputPin.requestFocus();

        inputPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                if (str.equals(localPin)) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    CognitoSettings.isLocked *= -1;
                    finish();

                } else {
                    Log.e(TAG, localPin);

                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    incorrectVibrate();
                    inputPin.startAnimation(shake);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputPin.setText("");

                        }
                    }, 1000);

                }
                return false;
            }
        });
    }

    private void incorrectVibrate() {
        if (vibrator != null && vibrator.hasVibrator())
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect effect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(500);
            }
    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    onBackPressed();
                }
            }
        });
        userDialog = builder.create();
        userDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        userDialog.show();

    }

}
