package com.STIRlab.ema_diary.Helpers;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
    private JSONObject history;

    private String beginQuote = encodeValue("\""), endQuote = encodeValue("\"");

    // baseURL
    private String baseURL = "https://iq185u2wvk.execute-api.us-east-1.amazonaws.com/v1/";

    //{"user_id":"90621533-45ad-413d-bda7-aafc0bc0071f","email":"easymoney@dmailpro.net",
    //      "did_set_pw":0,"study_start_date":null,"days_left":30,"num_complete _surveys":0,"earnings":0,"survey_status":"closed"}


    public RDS_Connect(){
        client = new OkHttpClient();
    }


    public String getUser(String userid, String email) throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

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
                        countDownLatch.countDown();
                        String responseStr = response.body().string();
                        returnStr = responseStr;
                        Log.e(TAG, url);
                    } else {
                        countDownLatch.countDown();
                        Log.e(TAG, call.toString());
                        Log.e(TAG, "request not successful");
                        Log.e(TAG, url);
                    }
                }
            });

            countDownLatch.await();

        return returnStr;

    }

    public JSONObject parseUserInfo(String userid, String email) throws Exception {
        JSONObject info = new JSONObject(getUser(userid, email));
        return info;
    }

    public String getEarnings(String userid, String email){
        try {
            double d = parseUserInfo(userid, email).getDouble("earnings");

            float earnings = (float)d;

            return formatDecimal(earnings).replaceAll("^\\s+","");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getStreak(String userid, String email){
        try {
            return parseUserInfo(userid, email).getString("streak");
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
            return parseUserInfo(userid, email).getString("survey_status");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }
    public String getSurveyCount(String userid, String email){
        try {
            return parseUserInfo(userid, email).getString("num_complete_surveys");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String finishedPost(String userid, String email) throws Exception {
        return parseUserInfo(userid, email).getString("took_post_survey_at");
    }

    public String parseHistory(String userid, String historyType) throws Exception {
        String url = baseURL + historyType + "-history?user_id=" + beginQuote + encodeValue(userid) + endQuote;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        getRequestHelper(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "call failed: " + e.toString());
                    countDownLatch.countDown();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        returnStr = responseStr;
                        countDownLatch.countDown();
                    } else {
                        Log.e(TAG, call.toString());
                        Log.e(TAG, "request not successful");
                        Log.e(TAG, url);
                        countDownLatch.countDown();
                    }
                }
            });

        countDownLatch.await();

        return returnStr;
    }

    private JSONObject getJournalHistory(String user_id) throws Exception {
        JSONObject obj = new JSONObject(parseHistory(user_id, "survey"));

        return obj;
    }

    public int getNumCompleted(String userid) throws Exception {
        JSONObject obj = getJournalHistory(userid);

        return  obj.getInt("num_completed");
    }

    public int getNumMissed(String userid) throws Exception {
        JSONObject obj = getJournalHistory(userid);

        return  obj.getInt("num_missed");
    }

    public ArrayList<JournalEntry> getJournalEntries(String userid) throws Exception {
        ArrayList<JournalEntry> returnHistory = new ArrayList<JournalEntry>();
        JSONObject obj = getJournalHistory(userid);
        JSONArray array = obj.getJSONArray("surveys");
        Log.i(TAG, array.toString(2));


        for(int i = 0; i < array.length(); i++){
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("survey_id");
            String openTime = tempObj.getString("opened_on");
            String submitTime = tempObj.getString("submitted_at");
            double initEarnings = tempObj.getDouble("earnings_so_far");
            String earnings = formatDecimal((float)initEarnings).replaceAll("^\\s+","");

            boolean isComplete = tempObj.getBoolean("is_complete");
            double initIncrement = tempObj.getDouble("earnings_added");
            String increment = formatDecimal((float)initIncrement).replaceAll("^\\s+","");



            JournalEntry tempEntry = new JournalEntry(id, openTime, submitTime, isComplete,
                    earnings, increment);

            returnHistory.add(tempEntry);
        }


        return returnHistory;
    }

    private JSONObject getThoughtsHistory(String user_id) throws Exception {
        JSONObject obj = new JSONObject(parseHistory(user_id, "thoughts"));
        return obj;
    }

    public ArrayList<ThoughtEntry> getThoughtEntries(String userid) throws Exception {
        ArrayList<ThoughtEntry> returnHistory = new ArrayList<ThoughtEntry>();
        JSONObject obj = getThoughtsHistory(userid);
        JSONArray array = obj.getJSONArray("thoughts");

        Log.i(TAG, array.toString(2));

        for(int i = 0; i < array.length(); i++){
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("thought_id");
            String submitTime = tempObj.getString("submitted_at");
            int screenshots = tempObj.getInt("num_screenshots");

            ThoughtEntry tempEntry= new ThoughtEntry(id, submitTime, screenshots);
            returnHistory.add(tempEntry);
        }

        return returnHistory;

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
                                    Log.i(TAG, "success");
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

    public String uploadInteractionWithPicture(String userid, String desc, String caption, File file) throws JSONException {
        String url = baseURL + "create-thought-url";

        MediaType PNG = MediaType.parse("image/png");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject pic = new JSONObject()
                .put("caption", caption)
                .put("file_ext", "png");

        JSONObject jgenerate= new JSONObject()
                .put("user_id", userid)
                .put("description",desc)
                .put("screenshot", pic);

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


                    RequestBody jsonFile = RequestBody.create(PNG, file);
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

    public String uploadInteraction(String userid, String desc) throws JSONException {
        String url = baseURL + "create-thought-url";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jgenerate= new JSONObject()
                .put("user_id", userid)
                .put("description",desc);

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        Log.i(TAG, jgenerate.toString());

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "request not successful");
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

        return "done";

    }

    public String startStudy(String userid) throws Exception
    {
        String url = baseURL + "start-study?user_id=" + beginQuote + encodeValue(userid) + endQuote;

        CountDownLatch countDownLatch = new CountDownLatch(1);

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
        countDownLatch.countDown();

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

    public String formatDecimal(float number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }
}
