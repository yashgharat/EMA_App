package com.STIRlab.ema_diary.Activities;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.ScrapeDataHelper;
import com.STIRlab.ema_diary.Helpers.SwitchTheme;
import com.STIRlab.ema_diary.R;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences SP;
    private String localPin;
    private boolean remember;
    private APIHelper client;

    private String TAG = "SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SP = getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        localPin = SP.getString("Pin", "null");
        remember = SP.getBoolean("Remember", false);

        Log.e(TAG, String.valueOf(remember));

        if (!localPin.equals("null")) {
            Intent i = new Intent(SplashScreen.this, PinActivity.class);
            startActivityForResult(i, 10);
        } else if (remember) {
            boolean hasPermissions = isAccessGranted(this);
            if(hasPermissions) {
                Intent main = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(main);
            }
            else
            {
                Intent data = new Intent(this, ManifestActivity.class);
                startActivityForResult(data, 10);
            }
            finish();
        } else {
            Intent main = new Intent(SplashScreen.this, AuthenticationActivity.class);
            startActivity(main);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                Intent main = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(main);
                finish();
                break;
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

    private boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
