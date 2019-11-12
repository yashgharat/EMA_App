package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.R;

public class createPinUIActivity extends AppCompatActivity {

    private SharedPreferences SP;
    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin_ui);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        Button btnLock = findViewById(R.id.btnQuickSignIn);

        TextView skipLink = findViewById(R.id.skipLink);

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(createPinUIActivity.this, createPinActivity.class);
                finish();
                startActivity(i);

                SP.edit().putBoolean("Remember", true).apply();
            }
        });

        skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMessage("Attention", "You can always set a pin in Settings if you change your mind", false);
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
        userDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        userDialog = builder.create();
        userDialog.show();

    }
}
