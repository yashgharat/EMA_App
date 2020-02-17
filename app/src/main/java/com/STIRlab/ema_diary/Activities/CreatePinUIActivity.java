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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.R;

public class CreatePinUIActivity extends AppCompatActivity {

    private final String TAG = "CREATE_PIN_UI";

    private SharedPreferences SP;
    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin_ui);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        Button btnLock = findViewById(R.id.button_quick_sign_in);

        TextView skipLink = findViewById(R.id.skip_link);

        Log.e(TAG, "Reached");

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreatePinUIActivity.this, CreatePinActivity.class);
                startActivity(i);

                SP.edit().putBoolean("Remember", true).apply();
            }
        });

        skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreatePinUIActivity.this, ManifestActivity.class);
                startActivity(i);
            }
        });
    }

}
