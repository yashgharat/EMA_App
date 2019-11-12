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
import android.widget.Button;
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoIdToken;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoRefreshToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";
    private CognitoSettings cognitoSettings;
    private CognitoUser thisUser;
    private Context context;

    private SharedPreferences SP;
    private SharedPreferences.Editor editor;
    private NewPasswordContinuation newPass;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private String temp;
    private int localPin;

    private CircularProgressButton buttonLogin;

    private Handler handler = new Handler();
    private TextView forgotPassword;
    private AlertDialog userDialog;


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

        forgotPassword = findViewById(R.id.forgotPasswordLink);

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

                buttonLogin.stopAnimation();

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
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG, "Login failed: " + exception.getLocalizedMessage());

                ConnectivityManager cm = (ConnectivityManager) AuthenticationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                buttonLogin.startMorphRevertAnimation();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    showDialogMessage("Error", "Email and/or password is incorrect", false);
                } else {
                    showDialogMessage("Error", "Not connected to the internet", false);
                }
            }
        };

        buttonLogin = findViewById(R.id.btnSignIn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("email", String.valueOf(editTextEmail.getText()));
                editor.apply();

                buttonLogin.startMorphAnimation();

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
                }
            }
        });


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextEmail.getText().toString();

                if (username.length() > 0 && isValid(username)) {
                    cognitoSettings.getUserPool().getUser(username).forgotPasswordInBackground(new ForgotPasswordHandler() {
                        @Override
                        public void onSuccess() {
                            showDialogMessage("Password successfully changed!", "", false);
                            editTextPassword.setText("");
                            editTextPassword.requestFocus();
                        }

                        @Override
                        public void getResetCode(ForgotPasswordContinuation continuation) {
                            getForgotPasswordCode(continuation);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            showDialogMessage("Password reset failed", CognitoSettings.formatException(exception), false);

                        }
                    });
                }
                else{
                    showDialogMessage("Error", "Enter valid username", false);
                }
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
        else if (requestCode == 3 && resultCode == RESULT_OK){
            String newPass = data.getStringExtra("newPass");
            String code = data.getStringExtra("code");
            if (newPass != null && code != null) {
                if (!newPass.isEmpty() && !code.isEmpty()) {
                    forgotPasswordContinuation.setPassword(newPass);
                    forgotPasswordContinuation.setVerificationCode(code);
                    forgotPasswordContinuation.continueTask();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    static class ClassDataSerializer implements JsonSerializer<CognitoUser> {

        @Override
        public JsonElement serialize(CognitoUser src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getUserId());
        }
    }

    private static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void getForgotPasswordCode(ForgotPasswordContinuation continuation) {
        this.forgotPasswordContinuation = continuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination", forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
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
