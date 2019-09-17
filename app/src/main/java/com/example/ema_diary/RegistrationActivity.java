package com.example.ema_diary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerUser();
    }

    private void registerUser() {

        final EditText newPass = findViewById(R.id.newPassword1);
        final EditText newEmail = findViewById(R.id.newEmail);

        Button newAcct = findViewById(R.id.RegisterAcct);

        final CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        final SignUpHandler signupCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState
                    , CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Sign-up was successful
                Log.i(TAG, "sign up success...is confirmed: " + signUpConfirmationState);
                // Check if this user (cognitoUser) needs to be confirmed
                if (!signUpConfirmationState) {
                    Log.i(TAG, "sign up success...not confirmed, verification code sent to: "
                            + cognitoUserCodeDeliveryDetails.getDestination());
                } else {
                    // The user has already been confirmed
                    Log.i(TAG, "sign up success...confirmed");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-up failed, check exception for the cause
                Log.i(TAG, "sign up failure: " + exception.getLocalizedMessage());
            }
        };

        newAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userAttributes.addAttribute("given_name","Yash");
                CognitoSettings cognitoSettings = new CognitoSettings(RegistrationActivity.this);

                cognitoSettings.getUserPool().signUpInBackground(String.valueOf(newEmail.getText())
                        , String.valueOf(newPass.getText()), userAttributes
                        , null, signupCallback);

                Intent myIntent = new Intent(RegistrationActivity.this, AuthenticationActivity.class);
                RegistrationActivity.this.startActivity(myIntent);
            }
        });
    }
}


