package com.example.ema_diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class ConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        final EditText code = findViewById(R.id.confCode);
        final EditText email = findViewById(R.id.confEmail);

        Button btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Patterns.EMAIL_ADDRESS.matcher(String.valueOf(email.getText())).matches()) {
                    new ConfirmTask().execute(String.valueOf(code.getText())
                            , String.valueOf(email.getText()));
                }
                else
                {
                    new AlertDialog.Builder(ConfirmationActivity.this, R.style.AlertDialogStyle)
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
    }

    private class ConfirmTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            final String[] result = new String[1];

            // Callback handler for confirmSignUp API
            final GenericHandler confirmationCallback = new GenericHandler() {

                @Override
                public void onSuccess() {
                    // User was successfully confirmed
                    result[0] = "Succeeded!";
                    Intent i = new Intent(ConfirmationActivity.this, AuthenticationActivity.class);
                    ConfirmationActivity.this.startActivity(i);
                }

                @Override
                public void onFailure(Exception exception) {
                    // User confirmation failed. Check exception for the cause.
                    result[0] = "Failed: "+exception.getMessage();
                    new AlertDialog.Builder(ConfirmationActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Not a valid code")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                }
            };

            CognitoSettings cognitoSettings = new CognitoSettings(ConfirmationActivity.this);

            CognitoUser thisUser = cognitoSettings.getUserPool().getUser(strings[1]);
            // This will cause confirmation to fail if the user attribute (alias) has been verified
            // for another user in the same pool
            thisUser.confirmSignUp(strings[0], false, confirmationCallback);

            return result[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i(TAG, "Confirmation result: " + result);

        }
    }
}
