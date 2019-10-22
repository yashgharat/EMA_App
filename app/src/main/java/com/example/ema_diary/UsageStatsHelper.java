package com.example.ema_diary;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;

public class UsageStatsHelper {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UsageStatsHelper.class.getSimpleName();

    private static final int DAY_IN_MILLISECONDS = 86400000;

    public static long getPackageUsage(String packageName, Context context){
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> list = getUsageStatsList(context);
        UsageStats stat = list.get(1);

        for(UsageStats u : list){
            if(u.getPackageName().equals(packageName))
                stat = u;
        }

        if(stat == null)
        {
            return 0;
        }
        else
            return stat.getTotalTimeInForeground();
    }

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager manager = getUsageStatsManager(context);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - DAY_IN_MILLISECONDS;

        List<UsageStats> usageStatsList = manager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,startTime,endTime);
        return usageStatsList;
    }

    public static void printUsageStats(List<UsageStats> usageStatsList, Context context){
        for (UsageStats u : usageStatsList){

            String name = u.getPackageName();

            if(name != null)
                Log.d(TAG, "Pkg: " + name);
        }

    }

    public static void printCurrentUsageStatus(Context context){
        printUsageStats(getUsageStatsList(context), context);
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService("usagestats");
        return manager;
    }



}
