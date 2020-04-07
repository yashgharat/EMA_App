package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.KeyStoreHelper;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.security.cert.CertificateException;

public class NewPassword extends AppCompatActivity {

    private CognitoSettings cognitoSettings;
    private final String TAG = "NEW_PASS";

    private SharedPreferences SP;

    private APIHelper client;
    private AlertDialog userDialog;
    private TextView signOut;

    private KeyStoreHelper keyStoreHelper;
    private final static String KEY_ALIAS = "ANDROID_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email);
        cognitoSettings = new CognitoSettings(NewPassword.this);

        Button btnSet = findViewById(R.id.button_set_pass);
        EditText txtPassUno = findViewById(R.id.new_pass_uno);
        signOut = findViewById(R.id.new_pass_signout);

        String oldPass = SP.getString("oldPass","null");

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(NewPassword.this, R.style.AlertDialogStyle)
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
                                Intent intent = new Intent(NewPassword.this, AuthenticationActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    CognitoSettings.user.changePasswordInBackground(CognitoSettings.oldPass, txtPassUno.getText().toString(), new GenericHandler() {
                        @Override
                        public void onSuccess() {
                            SP.edit().putString("dwString", String.valueOf(txtPassUno.getText())).apply();

                            try {
                                Log.i(TAG, client.updatePassword());
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }

                            finish();
                        }

                        @Override
                        public void onFailure(Exception exception) {

                            showDialogMessage("Password change failed", CognitoSettings.formatException(exception), false);
                             Log.i(TAG + "FAILURE", exception.toString());
                        }
                    });
            }
        });


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

    @Override
    public void onBackPressed() {

    }
}
