package com.example.ema_diary;

// Taken from MarshmallowProject pamwis


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    AlertDialog alertDialog;

    private final String TAG = "BACKGROUND";

    BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        String appname = params[0];
        String packageName = params[1];
        String time = params[2];
        try {
            String post_data = URLEncoder.encode("appName", "UTF-8") + "=" + URLEncoder.encode(appname, "UTF-8")+ "&"
                    + URLEncoder.encode("packageName", "UTF-8") + "=" + URLEncoder.encode(packageName, "UTF-8")+ "&"
                    + URLEncoder.encode("timeInForeground", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");

            Log.i(TAG, post_data);
        } catch (Exception e) {
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
