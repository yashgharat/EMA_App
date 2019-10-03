package com.example.ema_diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class createPinUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin_ui);

        Button btnLock = findViewById(R.id.btnQuickSignIn);

        TextView skipLink = findViewById(R.id.skipLink);

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(createPinUIActivity.this, R.style.AlertDialogStyle)
                        .setTitle("Attention")
                        .setMessage("You can always set a pin in Settings if you change your mind")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(createPinUIActivity.this, createPinActivity.class);
                                startActivity(i);
                            }
                        })
                        .show();
            }
        });

        skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Permissions Later
                Intent i = new Intent(createPinUIActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
