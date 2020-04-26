package com.STIRlab.ema_diary.Activities.SecureLock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.LifeCycleHelper;
import com.STIRlab.ema_diary.R;

public class CreatePinUIActivity extends AppCompatActivity {

    private final String TAG = "CREATE_PIN_UI";

    private SharedPreferences SP;
    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin_ui);
        LifeCycleHelper.flag = false;

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        Button btnLock = findViewById(R.id.button_quick_sign_in);

        TextView skipLink = findViewById(R.id.skip_link);


        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreatePinUIActivity.this, CreatePinActivity.class);
                i.putExtra("pinTitle", "Set a Passcode");
                SP.edit().putBoolean("Remember", true).apply();
                startActivityForResult(i, 10);
            }
        });

        skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                finish();
                break;
        }
    }

}
