package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class CreatePinActivity extends AppCompatActivity {

    private final String TAG = "CREATE_PIN";

    private CirclePinField inputPin;
    private SharedPreferences SP;
    private TextView title, signOut;
    private AlertDialog userDialog;
    private SharedPreferences.Editor editor;
    private CognitoSettings cognitoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        editor = SP.edit();

        cognitoSettings = new CognitoSettings(this);

        title = findViewById(R.id.title_pin);
        signOut = findViewById(R.id.pin_signout);

        String titleText = getIntent().getStringExtra("pinTitle");

        title.setText(titleText);
        Log.e(TAG, titleText);

        if (titleText.equals("Change Passcode")) {
            signOut.setClickable(false);
            signOut.setVisibility(View.INVISIBLE);
        } else {
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(CreatePinActivity.this, R.style.AlertDialogStyle)
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
                                    Intent intent = new Intent(CreatePinActivity.this, AuthenticationActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }


        Boolean isFirst = getIntent().getBooleanExtra("is_first", true);

        inputPin = findViewById(R.id.pin_field);
        inputPin.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputPin.requestFocus();

        inputPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                editor.putString("Pin", str).apply();
                finish();
                return false;
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    private void pinConfirm(String str) {
        Intent in = getIntent();
        if(in.getStringExtra("inputPin") == null){
            in = new Intent(CreatePinActivity.this, CreatePinActivity.class);
            in.putExtra("inputPin", str);
            editor.putString("pinTitle", "Confirm Pin");
            CreatePinActivity.this.startActivity(in);
        } else {
            if(str.equals(in.getStringExtra("inputPin"))){
                editor.putString("Pin", in.getStringExtra("inputPin"));
                editor.apply();

            } else {
                showDialogMessage("Error", "Pins are different", false);
                inputPin.setText(null);
                Log.i("pinFail", "didnt work");
            }

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
        userDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        userDialog.show();

    }
}
