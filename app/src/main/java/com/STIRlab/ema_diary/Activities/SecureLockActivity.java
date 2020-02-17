package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SecureLockActivity extends AppCompatActivity {

    private FloatingActionButton prev;
    private Button changePasscode;
    private SwitchCompat secureLockSwitch;
    private String pin;

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_lock);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);


        prev = findViewById(R.id.secure_lock_previous);
        secureLockSwitch = findViewById(R.id.secure_lock_switch);
        changePasscode = findViewById(R.id.change_passcode);

        pin = SP.getString("Pin", null);

        if(pin != null) {
            secureLockSwitch.setChecked(true);
        } else {
            secureLockSwitch.setChecked(false);
        }
        secureLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    if(pin == null)
                    {
                        Intent intent = new Intent(SecureLockActivity.this, CreatePinActivity.class);
                        SP.edit().putString("pinTitle", "Set a Passcode").apply();
                        intent.putExtra("is_first", false);
                        startActivityForResult(intent, 10);
                    }
                }
                else
                {
                    SP.edit().remove("Pin").apply();
                    changePasscode.setVisibility(View.GONE);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecureLockActivity.this, CreatePinActivity.class);
                SP.edit().putString("pinTitle", "Change Passcode").apply();
                intent.putExtra("is_first", false);
                startActivityForResult(intent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                changePasscode.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if (pin != null) {
            secureLockSwitch.setChecked(true);
        } else {
            secureLockSwitch.setChecked(false);
        }

    }
}
