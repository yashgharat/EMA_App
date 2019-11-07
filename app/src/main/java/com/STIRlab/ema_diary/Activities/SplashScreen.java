package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences SP;
    private String localPin;
    private boolean remember;
    private CognitoSettings cognitoSettings;

    private String TAG = "SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SP = getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getString("Pin", "null");
        remember = SP.getBoolean("Remember", false);

        Log.i(TAG, String.valueOf(remember));

        if (!localPin.equals("null")) {
            Intent i = new Intent(SplashScreen.this, PinActivity.class);
            Log.d("PIN: ", String.valueOf(localPin));
            Log.d("DEBUG", "PinActivity");
            startActivity(i);
            finish();
        } else if (remember) {
            Intent main = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(main);
            finish();
        }
        else{
            Intent main = new Intent(SplashScreen.this, AuthenticationActivity.class);
            startActivity(main);
            finish();
        }
    }
}
