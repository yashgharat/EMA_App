package com.STIRlab.ema_diary.Activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.R;

import static com.STIRlab.ema_diary.Helpers.APIHelper.isNetworkAvailable;

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

        if (remember && isNetworkAvailable(this)) {
            boolean hasPermissions = isAccessGranted(this);
            if(hasPermissions) {
                Intent main = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(main);
            }
            else
            {
                Intent data = new Intent(this, MissingPermissionsActivity.class);
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
