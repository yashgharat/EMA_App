package com.STIRlab.ema_diary.Helpers;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RDS_Connect {
    private final String TAG = "RDS_Connect";

    private OkHttpClient client;

    private String returnStr;
    private String beginQuote = encodeValue("\""), endQuote = encodeValue("\"");

    // baseURL
    private String baseURL = "https://iq185u2wvk.execute-api.us-east-1.amazonaws.com/dev/";




    public RDS_Connect(){
        client = new OkHttpClient();
    }


    public String doGetRequest(String userid, String email) throws Exception {

        while(returnStr == null) {
            String url = baseURL + "user?id=" + beginQuote +
                    encodeValue(userid) + endQuote + "&email=" + beginQuote + encodeValue(email) + endQuote;

            getRequestHelper(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "call failed: " + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        returnStr = responseStr;
                    } else {
                        Log.e(TAG, call.toString());
                        Log.e(TAG, "request not successful");
                        Log.e(TAG, url);
                    }
                }
            });
        }

        return returnStr;

    }

    public JSONObject parseUserInfo(String userid, String email) throws Exception {
        JSONObject info = new JSONObject(doGetRequest(userid, email));
        return info;
    }

    public String getEarnings(String userid, String email){
        try {
            double earnings = parseUserInfo(userid, email).getDouble("earnings");
            return String.valueOf(earnings);
        } catch (Exception e) {
            Log.e(TAG, "HERE"+ e.toString());
        }

        return null;
    }

    public String getStreak(String userid, String email){
        try {
            JSONObject earnings = parseUserInfo(userid, email).getJSONObject("earnings");
            return earnings.getString("earnings");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getDaysLeft(String userid, String email){
        try {
            return parseUserInfo(userid, email).getString("days_left");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getSurveyStatus(String userid, String email){
        try {
            JSONObject earnings = parseUserInfo(userid, email).getJSONObject("survey_status");
            return earnings.getString("survey_status");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String updatePassword(String userid) throws Exception{
        String url = baseURL + "did-set-pw?user_id=" + beginQuote + encodeValue(userid) + endQuote;
        patchRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseStr = response.body().string();
                    returnStr = responseStr;
                    Log.i(TAG, "here" + returnStr);
                }
                else{
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });
        return returnStr;
    }


    public String uploadFile(String userid, JSONObject json) throws JSONException {
        String url = baseURL + "create-scraped-data-url";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jgenerate= new JSONObject().put("user_id", userid).put("file_ext","json");

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseStr = response.body().string();
                    Log.i(TAG, responseStr);


                    try {
                        RequestBody jsonFile = RequestBody.create(JSON, json.toString(1));
                        putRequestHelper(responseStr, jsonFile, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, "call failed: " + e.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful()){
                                    String responseStr = response.body().string();
                                    returnStr = responseStr;
                                    Log.i(TAG, "here" + returnStr);
                                }
                                else{
                                    Log.e(TAG, "in PUT Call: " + response.toString());
                                    Log.e(TAG, "request not successful");
                                    Log.e(TAG, url);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
                else{
                    Log.e(TAG, "in POST Call: " + response.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return returnStr;
    }



    private Call patchRequestHelper(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(null, new byte[0]))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    private Call getRequestHelper(String url, Callback callback){

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call postRequestHelper(String url, RequestBody rBody, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .post(rBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call putRequestHelper(String url, RequestBody rBody, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .put(rBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
