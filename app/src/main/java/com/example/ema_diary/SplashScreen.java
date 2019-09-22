package com.example.ema_diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(UserAttributes.localPin != 0){
            Intent i = new Intent(SplashScreen.this, PinActivity.class);
            startActivity(i);
        }else{
            Intent i = new Intent(SplashScreen.this, AuthenticationActivity.class);
            startActivity(i);
        }
    }
}
