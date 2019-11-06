package com.STIRlab.ema_diary.Helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.STIRlab.ema_diary.Activities.CollectingInformation;

import java.util.List;

public class ScrapeDataHelper {
    private Context context;

    private final String TAG = "INFORMATIONS";

    final ScrapeDataHelper.app[] appArray = new ScrapeDataHelper.app[500];


    private SharedPreferences SP;

    int appSize = 0;

    public static class app{
        public String appName, packageName, time;
    }

    public ScrapeDataHelper(Context context)
    {
        this.context = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        //Declaring an array of app objects
        for (int i = 0; i < appArray.length; i++) {
            appArray[i] = new ScrapeDataHelper.app();
        }

    }

    public boolean scrape(){
        int screenTime = UsageEvents.Event.SCREEN_INTERACTIVE - SP.getInt("screenTime", -1);
        SP.edit().putInt("screenTime", UsageEvents.Event.SCREEN_INTERACTIVE);


        final PackageManager packageManager = context.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //Making a list of all the apps
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);

        int j = 0;
        //Looping through all the apps to retrieve info

        for (ResolveInfo info : apps) {
            appSize = apps.size();

            final ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;
            final String appName = (String) applicationInfo.loadLabel(packageManager);
            final String packageName = applicationInfo.packageName;
            final String time = String.valueOf(UsageStatsHelper.getPackageUsage(packageName,context));

            appArray[j].appName = appName;
            appArray[j].packageName = packageName;
            appArray[j].time = time;

            j++;
        }
        SP.edit().putString("username", "07b0d8ac-d86c-456f-8718-a0b86a8d0106");
        String username = SP.getString("username", null);

        //This is to make sure no app is duplicated in the database
        //Calling a function to save the collected permissions in a database
        BackgroundWorker backgroundWorker = new BackgroundWorker(context, appArray, String.valueOf(screenTime), j, username);
        backgroundWorker.execute();
        return true;
    }



}