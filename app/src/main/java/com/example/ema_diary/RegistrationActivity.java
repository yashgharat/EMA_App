package com.example.ema_diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

        final EditText newPass1 = findViewById(R.id.newPassword1);
        final EditText newPass2 = findViewById(R.id.newPassword2);
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

                if(String.valueOf(newPass1.getText()).equals(String.valueOf(newPass2.getText())) && Patterns.EMAIL_ADDRESS.matcher(String.valueOf(newEmail.getText())).matches()){
                    cognitoSettings.getUserPool().signUpInBackground(String.valueOf(newEmail.getText())
                            , String.valueOf(newPass1.getText()), userAttributes
                            , null, signupCallback);

                    Intent myIntent = new Intent(RegistrationActivity.this, ConfirmationActivity.class);
                    RegistrationActivity.this.startActivity(myIntent);
                }
                else if(!(String.valueOf(newPass1.getText()).equals(String.valueOf(newPass2.getText()))))
                {
                    Log.i(TAG, newPass1.getText() + ", " + newPass2.getText());
                    new AlertDialog.Builder(RegistrationActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Passwords do not match")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(RegistrationActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Not a valid email")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                }
            }
        });

        TextView sILink = findViewById(R.id.signInLink);
        sILink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, AuthenticationActivity.class);
                RegistrationActivity.this.startActivity(i);
            }
        });
    }
}


