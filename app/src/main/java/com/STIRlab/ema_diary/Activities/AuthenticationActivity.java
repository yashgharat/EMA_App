package com.STIRlab.ema_diary.Activities;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoIdToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import de.mustafagercek.library.LoadingButton;

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

    private LoadingButton buttonLogin;

    private Handler handler = new Handler();
    private TextView forgotPassword;
    private AlertDialog userDialog;
    private EditText editTextEmail, editTextPassword;


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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        forgotPassword = findViewById(R.id.forgot_password_link);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        editor = SP.edit();
        localPin = SP.getInt("Pin", -1);
        editor.putString("pinTitle", "Set a Passcode");

        cognitoSettings = new CognitoSettings(AuthenticationActivity.this);


        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

                Log.i(TAG, "Login successful, can get tokens here!");
                cognitoSettings.setCurrSession(userSession);

                String refToken = userSession.getRefreshToken().toString();

                CognitoRefreshToken refreshToken = userSession.getRefreshToken();
                CognitoIdToken idToken = userSession.getIdToken();

                Log.i(TAG, "Username: " + userSession.getUsername());

                editor.putString("username", userSession.getUsername()).apply();


                thisUser = cognitoSettings.getUserPool()
                        .getUser(String.valueOf(editTextEmail.getText()));
                CognitoSettings.user = thisUser;

                editor.putString("email", editTextEmail.getText().toString()).apply();

                editor.putString("refToken", refToken).apply();

                Log.d("TOKEN: ", refToken);

                //editor.putString("oldPass", String.valueOf(editTextPassword.getText()));
                CognitoSettings.oldPass = String.valueOf(editTextPassword.getText());

                buttonLogin.setButtonColor(getColor(R.color.primaryDark));
                buttonLogin.setTextColor(getColor(R.color.themeBackground));
                buttonLogin.onStopLoading();

                Intent myIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                startActivity(myIntent);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation
                    , String userId) {

                Log.i(TAG, "in getAuthenticationDetails()....");

                /*need to get the userId & password to continue*/
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId
                        , String.valueOf(editTextPassword.getText().toString()), null);

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
                if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                    NewPasswordContinuation newPasswordContinuation = (NewPasswordContinuation) continuation;
                    newPasswordContinuation.setPassword(String.valueOf(editTextPassword.getText()));
                    continuation.continueTask();
                }
                buttonLogin.setButtonColor(getColor(R.color.primaryDark));
                buttonLogin.setTextColor(getColor(R.color.themeBackground));
                buttonLogin.onStopLoading();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG, "Login failed: " + exception.getLocalizedMessage());

                ConnectivityManager cm = (ConnectivityManager) AuthenticationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                buttonLogin.setEnabled(true);
                buttonLogin.setButtonColor(getColor(R.color.primaryDark));
                buttonLogin.setTextColor(getColor(R.color.themeBackground));
                buttonLogin.onStopLoading();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    showDialogMessage("Error", "Email and/or password is incorrect", false);
                } else {
                    showDialogMessage("Error", "Not connected to the internet", false);
                }
            }
        };

        buttonLogin = findViewById(R.id.button_sign_in);
        buttonLogin.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogin.setButtonColor(getColor(R.color.disabled));
                buttonLogin.setTextColor(getColor(R.color.apparent));
                buttonLogin.setEnabled(false);
                buttonLogin.onStartLoading();
                editor.putString("email", String.valueOf(editTextEmail.getText()));
                editor.apply();

                buttonLogin.onStartLoading();

                if (editTextEmail.getText().length() > 0 && editTextPassword.getText().length() > 0) {
                    thisUser = cognitoSettings.getUserPool()
                            .getUser(String.valueOf(editTextEmail.getText()));
                    CognitoSettings.user = thisUser;
                    // Sign in the use
                    Log.i(TAG, "Login button clicked....");

                    SP.edit().putString("dwString", String.valueOf(editTextPassword.getText())).apply();

                    thisUser.getSessionInBackground(authenticationHandler);
                } else {
                    showDialogMessage("Error", "Please enter email and password", false);
                    buttonLogin.setEnabled(true);
                    buttonLogin.setButtonColor(getColor(R.color.primaryDark));
                    buttonLogin.setTextColor(getColor(R.color.themeBackground));
                    buttonLogin.onStopLoading();
                }

            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword.setEnabled(false);
                Intent i = new Intent(AuthenticationActivity.this, EnterEmailActivity.class);
                startActivityForResult(i, 3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            temp = data.getStringExtra("result");
            Log.i(TAG, "HERE");
        }
        else if(requestCode == 3){
            forgotPassword.setEnabled(true);
            editTextEmail.setText("");
            editTextPassword.setText("");
            editTextEmail.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    static class ClassDataSerializer implements JsonSerializer<CognitoUser> {

        @Override
        public JsonElement serialize(CognitoUser src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getUserId());
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
        userDialog.show();

    }


}
