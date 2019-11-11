package com.STIRlab.ema_diary.Helpers;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class ScrapeDataHelper {
    private Context context;

    private final String TAG = "INFORMATIONS";

    final ScrapeDataHelper.app[] appArray = new ScrapeDataHelper.app[500];

    private SharedPreferences SP;

    int appSize = 0;

    private long totalScreentime;

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
        int screenTime = (int) SP.getLong("total_screen_time", 0);


        final PackageManager packageManager = context.getPackageManager();

        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(0);

        int j = 0;
        for(ApplicationInfo app : apps) {
            appArray[j].appName = app.loadLabel(packageManager).toString();
            appArray[j].packageName = app.packageName;

            String time = String.valueOf(UsageStatsHelper.getPackageUsage(appArray[j].packageName,context));
            appArray[j].time = time;

            totalScreentime += Long.parseLong(time);
            j++;
        }


        String username = SP.getString("username", null);

        //This is to make sure no app is duplicated in the database
        //Calling a function to save the collected permissions in a database
        BackgroundWorker backgroundWorker = new BackgroundWorker(context, appArray, String.valueOf(totalScreentime), j, username);
        backgroundWorker.execute();
        return true;
    }



}