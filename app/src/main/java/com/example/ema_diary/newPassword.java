package com.example.ema_diary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class newPassword extends AppCompatActivity {

    private CognitoSettings cognitoSettings;
    private final String TAG = "NEW_PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        cognitoSettings = new CognitoSettings(newPassword.this);

        Button btnSet = findViewById(R.id.btnSetPass);

        EditText txtPassUno = findViewById(R.id.newPassUno);
        EditText txtPassDos = findViewById(R.id.newPassDos);

        Log.i(TAG, "REACHED");


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPassDos.getText().toString().equals(txtPassUno.getText().toString())){
                    Log.i(TAG, "REACHED");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", txtPassDos.getText());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
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

//                if(txtPassDos.getText().toString().equals(txtPassUno.getText().toString())){
//                    cognitoSettings.getUser().changePasswordInBackground(oldPass, txtPassDos.getText().toString(), new GenericHandler() {
//                        @Override
//                        public void onSuccess() {
//                            Log.i(TAG, "REACHED");
//                            Intent myIntent = new Intent(newPassword.this, createPinUIActivity.class);
//                            startActivity(myIntent);
//                        }
//
//                        @Override
//                        public void onFailure(Exception exception) {
//                            Log.i(TAG, "change failed");
//                        }
//                    });
//                }
            }
        });


    }

    @Override
    public void onBackPressed() {

    }
}
