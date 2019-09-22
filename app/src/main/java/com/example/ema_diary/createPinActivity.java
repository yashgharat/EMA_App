package com.example.ema_diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class createPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        CirclePinField inPin = findViewById(R.id.pinField);
        inPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                pinConfirm(str);

                return false;
            }
        });
    }

    private void pinConfirm(String str) {
        Intent in = getIntent();
        if(in.getStringExtra("inputPin") == null){
            in = new Intent(createPinActivity.this, createPinActivity.class);
            in.putExtra("inputPin", str);
        } else {
            if(str.equals(in.getStringExtra("inputPin"))){
                UserAttributes.localPin = Integer.parseInt(in.getStringExtra("inputPin"));
            }
        }
    }
}
