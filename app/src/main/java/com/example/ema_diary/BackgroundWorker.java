package com.example.ema_diary;

// Taken from MarshmallowProject pamwis


import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    BackgroundWorker(Context ctx) {
        context = ctx;}

    @Override
    protected String doInBackground(String... params) {
        String login_url = "https://researchstudies.engineering.nyu.edu/Malak/";
        String choice = params[0];
        if (choice.equals("permissions"))
        {
            try {
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

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =URLEncoder.encode("choice", "UTF-8") + "=" + URLEncoder.encode(choice, "UTF-8") + "&"
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
                        + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&"
                        + URLEncoder.encode("device", "UTF-8") + "=" + URLEncoder.encode(device, "UTF-8");


                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (choice.equals("survey"))
        {
            try {
                String deviceId = params[1];
                String ans1 = params[2];
                String ans2 = params[3];
                String ans3 = params[4];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data =URLEncoder.encode("choice", "UTF-8") + "=" + URLEncoder.encode(choice, "UTF-8") + "&"
                        + URLEncoder.encode("deviceId", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&"
                        + URLEncoder.encode("ans1", "UTF-8") + "=" + URLEncoder.encode(ans1, "UTF-8") + "&"
                        + URLEncoder.encode("ans2", "UTF-8") + "=" + URLEncoder.encode(ans2, "UTF-8") + "&"
                        + URLEncoder.encode("ans3", "UTF-8") + "=" + URLEncoder.encode(ans3, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
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
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }
}
