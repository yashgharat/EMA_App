package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class NewPassword extends AppCompatActivity {

    private CognitoSettings cognitoSettings;
    private final String TAG = "NEW_PASS";

    private SharedPreferences SP;

    private APIHelper client;
    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email);

        cognitoSettings = new CognitoSettings(NewPassword.this);

        Button btnSet = findViewById(R.id.button_set_pass);

        EditText txtPassUno = findViewById(R.id.new_pass_uno);

        String oldPass = SP.getString("oldPass","null");
        

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