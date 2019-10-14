package com.example.ema_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.util.concurrent.Executor;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";
    private CognitoSettings cognitoSettings;
    private CognitoUser thisUser;
    private Context context;

    private SharedPreferences SP;
    private SharedPreferences.Editor editor;
    private NewPasswordContinuation newPass;
    private String temp;
    private int localPin;

    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        final EditText editTextEmail = findViewById(R.id.email);
        final EditText editTextPassword = findViewById(R.id.password);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        editor = SP.edit();
        localPin = SP.getInt("Pin", -1);
        editor.putString("pinTitle", "Set a Pin");

        cognitoSettings = new CognitoSettings(AuthenticationActivity.this);


        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

                Log.i(TAG, "Login successful, can get tokens here!");
                cognitoSettings.setCurrSession(userSession);

                String token = userSession.getRefreshToken().toString();

                thisUser = cognitoSettings.getUserPool()
                        .getUser(String.valueOf(editTextEmail.getText()));
                CognitoSettings.user = thisUser;

                editor.putString("token", token);
                editor.apply();

                Log.d("TOKEN: ", token);

                //editor.putString("oldPass", String.valueOf(editTextPassword.getText()));
                CognitoSettings.oldPass = String.valueOf(editTextPassword.getText());


                Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                startActivity(myIntent);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation
                    , String userId) {

                Log.i(TAG, "in getAuthenticationDetails()....");

                /*need to get the userId & password to continue*/
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId
                        , String.valueOf(editTextPassword.getText()), null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();

            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.i(TAG, "in getMFACode()....");
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.i(TAG, "in authenticationChallenge()....");
                Log.i(TAG, continuation.getChallengeName());
                newPass = (NewPasswordContinuation) continuation;

                Intent i = new Intent(AuthenticationActivity.this, newPassword.class);
                startActivityForResult(i,1);

                Log.i("POST INTENT", "HERE");


                newPass.setPassword(temp);
                Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                startActivity(myIntent);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG, "Login failed: " + exception.getLocalizedMessage());

                ConnectivityManager cm = (ConnectivityManager)AuthenticationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(isConnected) {
                    new AlertDialog.Builder(AuthenticationActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Email and/or password is incorrect")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                }
                else
                {
                    new AlertDialog.Builder(AuthenticationActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Not connected to the internet")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })
                            .show();
                }
            }
        };

        Button buttonLogin = findViewById(R.id.btnSignIn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("email", String.valueOf(editTextEmail.getText()));
                editor.apply();
                thisUser = cognitoSettings.getUserPool()
                        .getUser(String.valueOf(editTextEmail.getText()));
                CognitoSettings.user = thisUser;
                // Sign in the use
                Log.i(TAG, "in button clicked....");

                thisUser.getSessionInBackground(authenticationHandler);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            temp = intent.getStringExtra("result");
            Log.i(TAG, "HERE");
        }
    }

    @Override
    public void onBackPressed() {

    }
}
