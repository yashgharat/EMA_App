package com.example.ema_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class PinActivity extends AppCompatActivity {

    private SharedPreferences SP;
    private int localPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getInt("Pin", -1);

        CirclePinField inputPin = findViewById(R.id.pinField);

        inputPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                int pin = Integer.parseInt(str);

                //17:25 e24

                if(pin == localPin){
                    Intent in = new Intent(PinActivity.this, MainActivity.class);
                    PinActivity.this.startActivity(in);
                } else {
                    new AlertDialog.Builder(PinActivity.this, R.style.AlertDialogStyle)
                            .setTitle("Error")
                            .setMessage("Pin not recognized")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
                return false;
            }
        });
    }
}
