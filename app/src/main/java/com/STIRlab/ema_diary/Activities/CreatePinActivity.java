package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.R;
import com.poovam.pinedittextfield.CirclePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

public class CreatePinActivity extends AppCompatActivity {

    private CirclePinField inputPin;
    private SharedPreferences SP;
    private TextView title;
    private AlertDialog userDialog;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        title = findViewById(R.id.title_pin);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        editor = SP.edit();

        title.setText(SP.getString("pinTitle", "NULL"));

        inputPin = findViewById(R.id.pin_field);
        inputPin.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputPin.requestFocus();

        inputPin.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete(@NotNull String str) {
                pinConfirm(str);
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

                finish();
                Intent intent = new Intent(CreatePinActivity.this, MainActivity.class);
                this.startActivity(intent);

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
