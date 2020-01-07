package com.STIRlab.ema_diary.Helpers;

// Taken from MarshmallowProject pamwis


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class BackgroundWorker extends AsyncTask<Void, Void, String> {
    private final String TAG = "BACKGROUND";

    Context context;
    AlertDialog alertDialog;

    private ScrapeDataHelper.app[] list = new ScrapeDataHelper.app[500];

    private String screenTime, username, email;
    private int size;

    private JSONObject upload, manifest;
    private JSONArray appUsage = new JSONArray();
    private SharedPreferences SP;


    private RDS_Connect client;


    public BackgroundWorker(Context ctx, ScrapeDataHelper.app[] list, String screenTime, int size, String userid) {
        context = ctx;
        this.list = list;
        this.screenTime = screenTime;

        upload = new JSONObject();
        manifest = new JSONObject();
        this.size = size;

        SP = ctx.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        username = SP.getString("username", "null");
        email = SP.getString("email", "null");

        client = new RDS_Connect(username, email);

    }

    @Override
    protected String doInBackground(Void... params) {

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events = mUsageStatsManager.queryEvents(
                System.currentTimeMillis() - AlarmManager.INTERVAL_DAY,
                System.currentTimeMillis());

        UsageEvents.Event eventCache = new UsageEvents.Event();

        for(int i = 0; i < size; i++) {

            String appname = list[i].appName;
            String packageName = list[i].packageName;
            String time = list[i].time;

            try {
                JSONObject tempapp = new JSONObject().put(appname, new JSONObject()
                                                            .put("time-in-foreground", time + " ms")
                                                            .put("package-name", packageName));
                appUsage.put(tempapp);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());

            }
        }
        try {
            upload.put("app-manifest", appUsage);
            upload.put("screen-time-millisec", screenTime);
            Log.i(TAG, upload.toString(2));

            client.uploadFile(upload);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }


        return "done";

    }

    @Override
    protected void onPreExecute() {
        //    alertDialog = new AlertDialog.Builder(context).create();
        //       alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        //alertDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "Values: " + values);
    }
}
