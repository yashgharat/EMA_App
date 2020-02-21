package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.SwitchTheme;
import com.STIRlab.ema_diary.R;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences SP;
    private String localPin;
    private boolean remember;
    private CognitoSettings cognitoSettings;

    private String TAG = "SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (SwitchTheme.getInstance(SplashScreen.this).isDark())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SP = getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getString("Pin", "null");
        remember = SP.getBoolean("Remember", false);

        while(!isNetworkAvailable(this));

        Log.i(TAG, String.valueOf(remember));

        if (!localPin.equals("null")) {
            Intent i = new Intent(SplashScreen.this, PinActivity.class);
            startActivity(i);
            finish();
        } else if (remember) {
            Intent main = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            Intent main = new Intent(SplashScreen.this, AuthenticationActivity.class);
            startActivity(main);
            finish();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))

                        return true;
                } else {

                    try {
                        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                            Log.i("update_status", "Network is available : true");
                            return true;
                        }
                    } catch (Exception e) {
                        Log.i("update_status", "" + e.getMessage());
                    }
                }
            }
        }
        return false;
    }
}
