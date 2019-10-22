package com.example.ema_diary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class newPassword extends AppCompatActivity {

    private CognitoSettings cognitoSettings;
    private final String TAG = "NEW_PASS";

    private SharedPreferences SP;

    private RDS_Connect client = new RDS_Connect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);

        cognitoSettings = new CognitoSettings(newPassword.this);

        Button btnSet = findViewById(R.id.btnSetPass);

        EditText txtPassUno = findViewById(R.id.newPassUno);
        EditText txtPassDos = findViewById(R.id.newPassDos);

        String oldPass = SP.getString("oldPass","null");

        Log.i(TAG, "REACHED");


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPassDos.getText().toString().equals(txtPassUno.getText().toString())){
                    Log.i(TAG, "REACHED");

                    CognitoSettings.user.changePasswordInBackground(CognitoSettings.oldPass, txtPassDos.getText().toString(), new GenericHandler() {
                        @Override
                        public void onSuccess() {
                            Log.i(TAG, "REACHED");
                            SP.edit().putString("dwString", String.valueOf(txtPassDos.getText())).apply();
                            Log.i(TAG, SP.getString("dwString", "null"));

                            try {
                                Log.i(TAG, client.updatePassword(SP.getString("username", "null")));
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }

                            finish();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Log.i(TAG + "FAILURE", oldPass);
                            Log.i(TAG + "FAILURE", exception.toString());
                        }
                    });
                }
                else
                {
                    new AlertDialog.Builder(newPassword.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Passwords do not match")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

    }
}
