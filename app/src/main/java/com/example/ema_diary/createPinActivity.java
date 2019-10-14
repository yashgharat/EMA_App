package com.example.ema_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class createPinActivity extends AppCompatActivity {

    private CirclePinField inPin;
    private SharedPreferences SP;
    private TextView title;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        title = findViewById(R.id.title_pin);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        editor = SP.edit();

        title.setText(SP.getString("pinTitle", "NULL"));

        inPin = findViewById(R.id.pinField);
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
            editor.putString("pinTitle", "Set a Pin");
            createPinActivity.this.startActivity(in);
        } else {
            if(str.equals(in.getStringExtra("inputPin"))){
                //UserAttributes.localPin = Integer.parseInt(in.getStringExtra("inputPin"));
                editor.putInt("Pin", Integer.parseInt(in.getStringExtra("inputPin")));
                editor.apply();

                finish();
                Intent intent = new Intent(createPinActivity.this, MainActivity.class);
                this.startActivity(intent);

            } else {
                new AlertDialog.Builder(createPinActivity.this, R.style.AlertDialogStyle)
                        .setTitle("Error")
                        .setMessage("Pins do not match up")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                inPin.setText(null);
                Log.i("pinFail", "didnt work");
            }

        }
    }
}
