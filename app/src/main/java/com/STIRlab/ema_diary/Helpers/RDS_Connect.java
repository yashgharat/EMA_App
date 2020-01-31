package com.STIRlab.ema_diary.Helpers;

import android.util.Log;

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

    private String returnStr, userid, email;
    private JSONObject history;

    private String beginQuote = encodeValue("\""), endQuote = encodeValue("\"");

    // baseURL
    private String baseURL = "https://iq185u2wvk.execute-api.us-east-1.amazonaws.com/v1/";

    //{"user_id":"90621533-45ad-413d-bda7-aafc0bc0071f","email":"easymoney@dmailpro.net",
    //      "did_set_pw":0,"study_start_date":null,"days_left":30,"num_complete _surveys":0,"earnings":0,"survey_status":"closed"}


    public RDS_Connect(String username, String email) {
        client = new OkHttpClient();
        this.userid = username;
        this.email = email;
    }


    public String getUser() throws Exception {

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

    public JSONObject parseUserInfo() throws Exception {
        JSONObject info = new JSONObject(getUser());
        return info;
    }

    public String getEarnings() {
        try {
            double d = parseUserInfo().getDouble("earnings");

            float earnings = (float) d;

            return formatDecimal(earnings).replaceAll("^\\s+", "");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getEntryCount() {
        try {
            return parseUserInfo().getString("num_complete_surveys");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String getDaysLeft() {
        try {
            return parseUserInfo().getString("days_left");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getSurveyCount() {
        try {
            return parseUserInfo().getString("num_complete_surveys");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getScreenshotCount() {
        try {
            return parseUserInfo().getString("num_thoughts");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String getResumeUrl() {
        try {
            return parseUserInfo().getString("resume_url");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String finishedPost() throws Exception {
        return parseUserInfo().getString("took_post_survey_at");
    }

    public String getEntryBonusStatus() throws Exception {
        return parseUserInfo().getJSONObject("surveys_progress").getString("bonus_status");
    }

    public JSONArray getStatuses() throws Exception {
        return parseUserInfo().getJSONObject("surveys_progress").getJSONArray("period_statuses");
    }

    public int getPeriodCount() throws Exception {
        return parseUserInfo().getJSONObject("surveys_progress").getInt("period_count");
    }

    public String parseHistory(String historyType) throws Exception {
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

    private JSONObject getJournalHistory() throws Exception {
        JSONObject obj = new JSONObject(parseHistory("survey"));

        return obj;
    }

    public int getNumCompleted() throws Exception {
        JSONObject obj = getJournalHistory();

        return obj.getInt("num_completed");
    }

    public int getNumMissed() throws Exception {
        JSONObject obj = getJournalHistory();

        return obj.getInt("num_missed");
    }

    public ArrayList<JournalEntry> getJournalEntries() throws Exception {
        ArrayList<JournalEntry> returnHistory = new ArrayList<JournalEntry>();
        JSONObject obj = getJournalHistory();
        JSONArray array = obj.getJSONArray("surveys");
        Log.i(TAG, array.toString(2));


        for (int i = 0; i < array.length(); i++) {
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("survey_id");
            String openTime = tempObj.getString("opened_on");
            String submitTime = tempObj.getString("submitted_at");
            String status = tempObj.getString("status");


            JournalEntry tempEntry = new JournalEntry(id, openTime, submitTime, status);

            returnHistory.add(tempEntry);
        }


        return returnHistory;
    }

    private JSONObject getscreenshotsHistory() throws Exception {
        JSONObject obj = new JSONObject(parseHistory("screenshots"));
        return obj;
    }

    public ArrayList<ScreenshotEntry> getScreenshotEntries() throws Exception {
        ArrayList<ScreenshotEntry> returnHistory = new ArrayList<ScreenshotEntry>();
        JSONObject obj = getscreenshotsHistory();
        JSONArray array = obj.getJSONArray("screenshots");

        Log.i(TAG, array.toString(2));

        for (int i = 0; i < array.length(); i++) {
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("screenshot_id");
            String submitTime = tempObj.getString("submitted_at");
            int screenshots = tempObj.getInt("num_screenshots");

            ScreenshotEntry tempEntry = new ScreenshotEntry(id, submitTime, screenshots);
            returnHistory.add(tempEntry);
        }

        return returnHistory;

    }


    public String updatePassword() throws Exception {
        String url = baseURL + "did-set-pw?user_id=" + beginQuote + encodeValue(userid) + endQuote;
        patchRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    returnStr = responseStr;
                    Log.i(TAG, "here" + returnStr);
                } else {
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });
        return returnStr;
    }


    public String uploadFile(JSONObject json) throws JSONException {
        String url = baseURL + "create-scraped-data-url";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jgenerate = new JSONObject().put("user_id", userid).put("file_ext", "json");

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
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
                                if (response.isSuccessful()) {
                                    String responseStr = response.body().string();
                                    returnStr = responseStr;
                                    Log.i(TAG, "success");
                                } else {
                                    Log.e(TAG, "in PUT Call: " + response.toString());
                                    Log.e(TAG, "request not successful");
                                    Log.e(TAG, url);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "in POST Call: " + response.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return returnStr;
    }

    public String uploadInteractionWithPicture(String desc, String caption, File file) throws JSONException {
        String url = baseURL + "create-screenshot-url";

        MediaType PNG = MediaType.parse("image/png");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject pic = new JSONObject()
                .put("caption", caption)
                .put("file_ext", "png");

        JSONObject jgenerate = new JSONObject()
                .put("user_id", userid)
                .put("description", desc)
                .put("screenshot", pic);

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
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
                            if (response.isSuccessful()) {
                                String responseStr = response.body().string();
                                returnStr = responseStr;
                                Log.i(TAG, "here" + returnStr);
                            } else {
                                Log.e(TAG, "in PUT Call: " + response.toString());
                                Log.e(TAG, "request not successful");
                                Log.e(TAG, url);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "in POST Call: " + response.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return returnStr;
    }

    public String uploadInteraction(String userid, String desc) throws JSONException {
        String url = baseURL + "create-screenshot-url";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jgenerate = new JSONObject()
                .put("user_id", userid)
                .put("description", desc);

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        Log.i(TAG, jgenerate.toString());

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "request not successful");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    returnStr = responseStr;
                    Log.i(TAG, "here" + returnStr);
                } else {
                    Log.e(TAG, "in PUT Call: " + response.toString());
                    Log.e(TAG, "request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return "done";

    }

    public String startStudy(String userid) throws Exception {
        String url = baseURL + "start-study?user_id=" + beginQuote + encodeValue(userid) + endQuote;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        patchRequestHelper(url, new Callback() {
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
        countDownLatch.countDown();

        return returnStr;
    }


    private Call patchRequestHelper(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(null, new byte[0]))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    private Call getRequestHelper(String url, Callback callback) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call postRequestHelper(String url, RequestBody rBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(rBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call putRequestHelper(String url, RequestBody rBody, Callback callback) {
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
