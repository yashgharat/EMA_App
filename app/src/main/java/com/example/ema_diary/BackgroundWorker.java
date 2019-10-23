package com.example.ema_diary;

// Taken from MarshmallowProject pamwis


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<Void, Void, String> {
    Context context;
    AlertDialog alertDialog;

    private CollectingInformation.app[] list = new CollectingInformation.app[500];

    private final String TAG = "BACKGROUND";
    private String screenTime;
    private JSONObject upload, manifest;

    private int size;

    private JSONArray appUsage = new JSONArray();


    BackgroundWorker(Context ctx, CollectingInformation.app[] list, String screenTime, int size) {
        context = ctx;
        this.list = list;
        this.screenTime = screenTime;

        upload = new JSONObject();
        manifest = new JSONObject();
        this.size = size;
    }

    @Override
    protected String doInBackground(Void... params) {
        for(int i = 0; i < size; i++) {

            String appname = list[i].appName;
            String packageName = list[i].packageName;
            String time = list[i].time;

            try {
                JSONObject tempapp = new JSONObject().put(appname, new JSONObject()
                                                            .put("Time In Foreground", time + " ms")
                                                            .put("Package-name", packageName));
                appUsage.put(tempapp);
            } catch (JSONException e) {
                Log.e(TAG, e.toString());

            }
        }
        try {
            upload.put("manifest", appUsage);
            upload.put("Screen time", screenTime);
            Log.i(TAG, upload.toString(2));
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        FileWriter fw;
        File file = new File(context.getFilesDir(), "scrape-data");
        if (!file.exists()) {
            file.mkdir();
        }

        File sdCardFile = new File(file, "sample");

        try {
            fw = new FileWriter(sdCardFile, false);
            fw.append(upload.toString(2));
            fw.flush();
            fw.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
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
