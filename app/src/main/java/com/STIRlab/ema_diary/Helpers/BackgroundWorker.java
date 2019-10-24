package com.STIRlab.ema_diary.Helpers;

// Taken from MarshmallowProject pamwis


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.STIRlab.ema_diary.Activities.CollectingInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class BackgroundWorker extends AsyncTask<Void, Void, String> {
    private final String TAG = "BACKGROUND";

    Context context;
    AlertDialog alertDialog;

    private CollectingInformation.app[] list = new CollectingInformation.app[500];

    private String screenTime, userid;
    private int size;

    private JSONObject upload, manifest;
    private JSONArray appUsage = new JSONArray();

    RDS_Connect client = new RDS_Connect();


    public BackgroundWorker(Context ctx, CollectingInformation.app[] list, String screenTime, int size, String userid) {
        context = ctx;
        this.list = list;
        this.screenTime = screenTime;

        upload = new JSONObject();
        manifest = new JSONObject();
        this.size = size;
        this.userid = userid;
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

            client.uploadFile(userid, upload);
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
