package com.example.ema_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class PinActivity extends AppCompatActivity {

    private SharedPreferences SP;
    private int localPin;
    private CognitoSettings cognitoSettings;

    final String TAG = "PIN_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getInt("Pin", -1);

        cognitoSettings = new CognitoSettings(this);


        CirclePinField inputPin = findViewById(R.id.pinField);

        inputPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                int pin = Integer.parseInt(str);

                //17:25 e24

                if(pin == localPin){
                    cognitoSettings.getUserPool().getCurrentUser().getSession(new AuthenticationHandler() {
                        @Override
                        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                            Log.i(TAG, "success");
                            SP.edit().putString("token", userSession.getRefreshToken().toString());
                            SP.edit().apply();
                            Intent main = new Intent(PinActivity.this, MainActivity.class);
                            startActivity(main);
                        }

                        @Override
                        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) { }

                        @Override
                        public void getMFACode(MultiFactorAuthenticationContinuation continuation) { }

                        @Override
                        public void authenticationChallenge(ChallengeContinuation continuation) { }

                        @Override
                        public void onFailure(Exception exception) {
                            new AlertDialog.Builder(PinActivity.this, R.style.AlertDialogStyle)
                                    .setTitle("Error")
                                    .setMessage("Auto Sign In failed, redirecting to authentication")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(PinActivity.this, AuthenticationActivity.class);
                                            startActivity(i);
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    new AlertDialog.Builder(PinActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Pin not recognized")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
                return false;
            }
        });
    }
}