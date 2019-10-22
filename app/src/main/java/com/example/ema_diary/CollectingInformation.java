package com.example.ema_diary;

// Taken from MarshmallowProject pamwis

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.List;

public class CollectingInformation extends AppCompatActivity {

    private final String TAG = "INFORMATIONS";
    private final int PHONE_STATE = 69;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");


    private Button next2;

    //Variable declarations
    String buildNumber;
    String version;
    int appSize = 0;
    Context context;

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collecting_permissions);

        //Declaring an array of app objects
        final String[] appNames = new String[100];

        next2 = (Button) findViewById(R.id.btnNext);

        context = this;

        if (UsageStatsHelper.getUsageStatsList(this).isEmpty()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, 85);
        }

        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //Making a list of all the apps
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE);
            int i = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            Log.i(TAG, String.valueOf(i));
        }
        int j = 0;
        //Looping through all the apps to retrieve info

        for (ResolveInfo info : apps) {
            appSize = apps.size();

            final ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;
            final String appName = (String) applicationInfo.loadLabel(packageManager);
            final String packageName = applicationInfo.packageName;

            appNames[j] = appName;

            String time = String.valueOf(UsageStatsHelper.getPackageUsage(packageName,this));
            //This is to make sure no app is duplicated in the database
            //Calling a function to save the collected permissions in a database
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(appName, packageName, time);
            //Adding the app name to an array

            j++;
        }
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}