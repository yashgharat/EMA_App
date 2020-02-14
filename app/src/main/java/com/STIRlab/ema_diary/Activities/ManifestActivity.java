package com.STIRlab.ema_diary.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class ManifestActivity extends AppCompatActivity {
    private final String TAG = "MANIFEST";

    private Button btnGrant;
    private TextView lblMoreInfo, signOut;
    private CognitoSettings cognitoSettings;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        cognitoSettings = new CognitoSettings(this);

        btnGrant = findViewById(R.id.button_grant_access);
        lblMoreInfo = findViewById(R.id.more_info);
        signOut = findViewById(R.id.data_signout);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ManifestActivity.this, R.style.AlertDialogStyle)
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
                                Intent intent = new Intent(ManifestActivity.this, AuthenticationActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ManifestActivity.this, TutorialActivity.class);
                finish();
                startActivityForResult(i, 15);
            }
        });

        lblMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManifestActivity.this, MoreInfoActivity.class);
                finish();
                startActivityForResult(i, 15);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 15:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
