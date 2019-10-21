package com.example.ema_diary;

// Taken from MarshmallowProject pamwis


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        try {
            String choice = params[0];
            String test_id = params[1];
            String appname = params[2];
            String calendar = params[3];
            String microphone = params[4];
            String storage = params[5];
            String camera = params[6];
            String contacts = params[7];
            String sms = params[8];
            String sensor = params[9];
            String phone = params[10];
            String location = params[11];
            String device = params[12];

            String post_data = URLEncoder.encode("choice", "UTF-8") + "=" + URLEncoder.encode(choice, "UTF-8") + "&"
                    + URLEncoder.encode("test_id", "UTF-8") + "=" + URLEncoder.encode(test_id, "UTF-8") + "&"
                    + URLEncoder.encode("appname", "UTF-8") + "=" + URLEncoder.encode(appname, "UTF-8") + "&"
                    + URLEncoder.encode("calendar", "UTF-8") + "=" + URLEncoder.encode(calendar, "UTF-8") + "&"
                    + URLEncoder.encode("microphone", "UTF-8") + "=" + URLEncoder.encode(microphone, "UTF-8") + "&"
                    + URLEncoder.encode("storage", "UTF-8") + "=" + URLEncoder.encode(storage, "UTF-8") + "&"
                    + URLEncoder.encode("camera", "UTF-8") + "=" + URLEncoder.encode(camera, "UTF-8") + "&"
                    + URLEncoder.encode("contacts", "UTF-8") + "=" + URLEncoder.encode(contacts, "UTF-8") + "&"
                    + URLEncoder.encode("sms", "UTF-8") + "=" + URLEncoder.encode(sms, "UTF-8") + "&"
                    + URLEncoder.encode("sensor", "UTF-8") + "=" + URLEncoder.encode(sensor, "UTF-8") + "&"
                    + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&"
                    + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

            Log.i(TAG, post_data);
        } catch (Exception e) {

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
