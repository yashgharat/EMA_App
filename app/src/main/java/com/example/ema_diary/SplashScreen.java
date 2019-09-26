package com.example.ema_diary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences SP;
    private int localPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SP = getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getInt("Pin", -1);

        if(localPin != -1){
            Intent i = new Intent(SplashScreen.this, PinActivity.class);
            Log.d("PIN: ", String.valueOf(localPin));
            Log.d("DEBUG", "PinActivity");
            startActivity(i);
        }else{
            Intent i = new Intent(SplashScreen.this, AuthenticationActivity.class);
            Log.d("DEBUG", "AuthActivity");
            startActivity(i);
        }
    }
}
