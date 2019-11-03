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
    private int localPin;
    private boolean remember;
    private CognitoSettings cognitoSettings;

    private String TAG = "SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SP = getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getInt("Pin", -1);
        remember = SP.getBoolean("Remember", false);

        cognitoSettings = new CognitoSettings(this);

        Log.i(TAG, "Splash reached");

        if (!remember) {
                cognitoSettings.getUserPool().getCurrentUser().getSession(new AuthenticationHandler() {
                    @Override
                    public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                        Log.i(TAG, "success");
                        SP.edit().putString("refToken", userSession.getRefreshToken().toString());
                        SP.edit().apply();

                        CognitoSettings.setCurrSession(userSession);
                        Log.i(TAG, userSession.getUsername());

                        Intent main = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    }

                    @Override
                    public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                    }

                    @Override
                    public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                    }

                    @Override
                    public void authenticationChallenge(ChallengeContinuation continuation) {
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, exception.toString());
                        new AlertDialog.Builder(SplashScreen.this, R.style.AlertDialogStyle)
                                .setTitle("Error")
                                .setMessage("Auto Sign In failed, redirecting to authentication")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(SplashScreen.this, AuthenticationActivity.class);
                                        startActivity(i);
                                    }
                                })
                                .show();
                    }
                });
            } else if (localPin != -1) {
                Intent i = new Intent(SplashScreen.this, PinActivity.class);
                Log.d("PIN: ", String.valueOf(localPin));
                Log.d("DEBUG", "PinActivity");
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SplashScreen.this, AuthenticationActivity.class);
                Log.d("DEBUG", "AuthActivity");
                startActivity(i);
                finish();
            }
        }
    }
