package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIHelper {
    private final String TAG = "APIHelper";

    private OkHttpClient client;

    public String userReturnStr, earningsReturnStr, historyReturnStr;
    private String userid, email;
    private JSONObject history, userInfo;

    private CognitoSettings cognitoSettings;
    private SharedPreferences SP;

    private String beginQuote = encodeValue("\""), endQuote = encodeValue("\"");

    private Callback getCallback;

    private Context context;

    // baseURL
    private String baseURL = "https://iq185u2wvk.execute-api.us-east-1.amazonaws.com/v1/";

    public APIHelper(String username, String email, Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10MB
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .cache(new Cache(context.getCacheDir(), cacheSize))
                .addNetworkInterceptor(new CacheInterceptor())
                .build();

        this.userid = username;
        this.email = email;
    }

    public String getUser(boolean isInit) throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        String url = baseURL + "user?id=" + beginQuote +
                encodeValue(userid) + endQuote + ((isInit) ? "&email=" + beginQuote + encodeValue(email) + endQuote : "");


        getRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getUser failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    countDownLatch.countDown();
                    String responseStr = response.body().string();
                    userReturnStr = responseStr;
                    Log.e(TAG, url);
                } else {
                    countDownLatch.countDown();
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "getUser request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        countDownLatch.await();

        return userReturnStr;
    }

    public void getUserWithCallback(boolean isInit, Callback callback) throws Exception {
        getUser(isInit);

        this.getCallback = callback;

        String url = baseURL + "user?id=" + beginQuote +
                encodeValue(userid) + endQuote + ((isInit) ? "&email=" + beginQuote + encodeValue(email) + endQuote : "");

        getRequestHelper(url, getCallback);

    }

    public JSONObject parseUserInfo() throws Exception {
        if (userReturnStr == null) {
            getUser(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        userInfo = new JSONObject(userReturnStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
        userInfo = new JSONObject(userReturnStr);
        return userInfo;
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

    public int didSetPass() {
        try {
            int i = parseUserInfo().getInt("did_set_pw");
            return i;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return 0;
    }

    public boolean didStartStudy() {
        try {

            if (parseUserInfo().getString("study_start_date") != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return false;
    }

    public String getDaysLeft() {
        try {
            return parseUserInfo().getString("days_left");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public String getTotalSurveyCount() {
        try {
            return parseUserInfo().getString("num_complete_surveys");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String getTotalScreenshotCount() {
        try {
            return parseUserInfo().getString("num_thoughts");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String getCurrentSurvey() {
        try {
            return parseUserInfo().getString("current_survey_id");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public String finishedPost() throws Exception {
        return parseUserInfo().getString("took_post_survey_at");
    }


    public JSONArray getStatuses() throws Exception {
        JSONArray temp = parseUserInfo().getJSONObject("surveys_progress").getJSONArray("period_statuses");
        return temp;
    }

    public int getPeriodSurveyCount() {
        try {
            return parseUserInfo().getJSONObject("surveys_progress").getInt("period_count");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getPeriodSurveyBonusStatus() {
        try {
            return parseUserInfo().getJSONObject("surveys_progress").getString("bonus_status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPeriodThoughtCount() {
        try {
            return parseUserInfo().getJSONObject("thoughts_progress").getInt("period_count");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getPeriodThoughtBonusStatus() {
        try {
            return parseUserInfo().getJSONObject("thoughts_progress").getString("bonus_status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getAllEarnings() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        String url = baseURL + "/earnings?user_id=" + beginQuote +
                encodeValue(userid) + endQuote;

        getRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getEarnings call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    earningsReturnStr = responseStr;
                    Log.e(TAG, url);
                    countDownLatch.countDown();
                } else {
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "getEarnings request not successful");
                    Log.e(TAG, url);
                    countDownLatch.countDown();

                }
            }
        });

        countDownLatch.await();


        return earningsReturnStr;

    }


    public JSONObject parseEarnings() throws Exception {
        return new JSONObject(getAllEarnings());

    }

    public double getTotalEarnings() {
        JSONObject earnings = null;
        try {
            earnings = parseEarnings();
            return earnings.getDouble("total_earnings");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getPossibleEarnings() {

        JSONObject earnings = null;
        try {
            earnings = parseEarnings();
            return earnings.getDouble("possible_earnings");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<EarningsPeriod> getPeriods() throws Exception {
        ArrayList<EarningsPeriod> returnEarnings = new ArrayList<EarningsPeriod>();

        JSONObject obj = parseEarnings();
        JSONArray array = obj.getJSONArray("periods");

        for (int i = 0; i < array.length(); i++) {
            JSONObject tempObj = array.getJSONObject(i);

            JSONObject surveys = tempObj.getJSONObject("surveys"), thoughts = tempObj.getJSONObject("thoughts");

            double earnings = tempObj.getDouble("earnings_so_far"), increment = tempObj.getDouble("earnings_added");
            double surveyBonusEarnings = surveys.getDouble("bonus_earnings"), thoughtsBonusEarnings = thoughts.getDouble("bonus_earnings");
            double surveyBasicEarnings = surveys.getDouble("basic_earnings");

            int survey_count = surveys.getInt("submit_count"), thoughts_count = thoughts.getInt("submit_count");
            int approve = surveys.getInt("approve_count");
            String start_date = tempObj.getString("start_date"), end_date = tempObj.getString("end_date");
            String surveys_bonus = surveys.getString("bonus_status"), thoughts_bonus = thoughts.getString("bonus_status");
            boolean isFirst = (i == 0);

            EarningsPeriod tempEntry = new EarningsPeriod(earnings, increment, surveyBonusEarnings, thoughtsBonusEarnings, surveyBasicEarnings,
                    survey_count, thoughts_count, approve, start_date, end_date,
                    surveys_bonus, thoughts_bonus, isFirst);

            returnEarnings.add(tempEntry);
        }


        return returnEarnings;
    }

    public EarningsPeriod getCurrentPeriod() throws Exception {
        return getPeriods().get(0);
    }


    public String parseHistory(String historyType) throws Exception {
        String url = baseURL + historyType + "-history?user_id=" + beginQuote + encodeValue(userid) + endQuote;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        getRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getHistory call failed: " + e.toString());
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    historyReturnStr = responseStr;
                    Log.e(TAG, responseStr);
                    countDownLatch.countDown();
                } else {
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "getHistory request not successful");
                    Log.e(TAG, url);
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        return historyReturnStr;
    }

    private JSONObject getJournalHistory() throws Exception {
        JSONObject obj = new JSONObject(parseHistory("survey"));
        Log.e(TAG, obj.toString());
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

        for (int i = 0; i < array.length(); i++) {
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("survey_id");
            String openTime = tempObj.getString("opened_on");
            String submitTime = tempObj.getString("submitted_at");
            String status = tempObj.getString("status");
            String decisionTime = tempObj.getString("set_validity_at");
            String researcherMessage = tempObj.getString("researcher_msg");

            JournalEntry tempEntry = new JournalEntry(id, openTime, submitTime, status, decisionTime, researcherMessage);

            returnHistory.add(tempEntry);
        }


        return returnHistory;
    }

    private JSONObject getThoughtsHistory() throws Exception {
        JSONObject obj = new JSONObject(parseHistory("thoughts"));
        return obj;
    }

    public ArrayList<Thought> getThoughtEntries() throws Exception {
        ArrayList<Thought> returnHistory = new ArrayList<Thought>();
        JSONObject obj = getThoughtsHistory();
        JSONArray array = obj.getJSONArray("thoughts");

        for (int i = 0; i < array.length(); i++) {
            JSONObject tempObj = array.getJSONObject(i);

            String id = tempObj.getString("thought_id");
            String submitTime = tempObj.getString("submitted_at");
            String status = tempObj.getString("status");
            String decisionTime = tempObj.getString("set_validity_at");
            String researcherMessage = tempObj.getString("researcher_msg");

            Thought tempEntry = new Thought(id, submitTime, status, decisionTime, researcherMessage);
            returnHistory.add(tempEntry);
        }

        return returnHistory;

    }


    public String updatePassword() throws Exception {
        String url = baseURL + "did-set-pw?user_id=" + beginQuote + encodeValue(userid) + endQuote;
        patchRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "updatePassword call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    historyReturnStr = responseStr;
                    Log.i(TAG, "here" + historyReturnStr);
                } else {
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "updatePassword request not successful");
                    Log.e(TAG, url);
                }
            }
        });
        return historyReturnStr;
    }


    public String uploadFile(JSONObject json) throws JSONException {
        String url = baseURL + "create-scraped-data-url";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jgenerate = new JSONObject().put("user_id", userid).put("file_ext", "json");

        RequestBody rBody = RequestBody.create(JSON, jgenerate.toString(1));

        postRequestHelper(url, rBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "uploadFile call failed: " + e.toString());

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
                                Log.e(TAG, "putFile call failed: " + e.toString());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String responseStr = response.body().string();
                                    userReturnStr = responseStr;
                                    Log.i(TAG, "success");
                                } else {
                                    Log.e(TAG, "in PUT Call: " + response.toString());
                                    Log.e(TAG, "put File request not successful");
                                    Log.e(TAG, url);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "in POST Call: " + response.toString());
                    Log.e(TAG, "post file request not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return userReturnStr;
    }

    public String uploadInteractionWithPicture(String desc, String caption, File file) throws JSONException {
        String url = baseURL + "create-thought-url";

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
                Log.e(TAG, "post Interaction call failed: " + e.toString());

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
                            Log.e(TAG, "put interaction call failed: " + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String responseStr = response.body().string();
                            } else {
                                Log.e(TAG, "in PUT Call: " + response.toString());
                                Log.e(TAG, "put interaction request not successful");
                                Log.e(TAG, url);
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "in POST Call: " + response.toString());
                    Log.e(TAG, "post interactionrequest not successful");
                    Log.e(TAG, url);
                }
            }
        });

        return userReturnStr;
    }

    public String startStudy(String userid) throws Exception {
        String url = baseURL + "start-study?user_id=" + beginQuote + encodeValue(userid) + endQuote + "&platform=" + beginQuote + "android" + endQuote;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        patchRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "start study call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    userReturnStr = responseStr;
                } else {
                    Log.e(TAG, call.toString());
                    Log.e(TAG, "start study request not successful");
                    Log.e(TAG, url);
                }
            }
        });
        countDownLatch.countDown();

        return userReturnStr;
    }

    public void makeCognitoSettings(Context context) {
        cognitoSettings = new CognitoSettings(context);
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
    }

    private Call patchRequestHelper(String url, Callback callback) {
        String token = cognitoSettings.getToken(SP);

        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(null, new byte[0]))
                .addHeader("Authorization", token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    private Call getRequestHelper(String url, Callback callback) {
        String token = cognitoSettings.getToken(SP);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call postRequestHelper(String url, RequestBody rBody, Callback callback) {
        String token = cognitoSettings.getToken(SP);

        Request request = new Request.Builder()
                .url(url)
                .post(rBody)
                .addHeader("Authorization", token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call putRequestHelper(String url, RequestBody rBody, Callback callback) {
        String token = cognitoSettings.getToken(SP);

        Request request = new Request.Builder()
                .url(url)
//                .addHeader("Authorization", token)
                .put(rBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
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

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))

                        return true;
                } else {

                    try {
                        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                            return true;
                        }
                    } catch (Exception e) {
                        Log.i("update_status", "" + e.getMessage());
                    }
                }
            }
        }
        return false;
    }
}

class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        CacheControl cacheControl = new CacheControl.Builder()
                .minFresh(5, TimeUnit.MINUTES)
                .build();

        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build();
    }
}