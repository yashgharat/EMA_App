package com.example.ema_diary;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RDS_Connect {
    private final String TAG = "RDS_Connect";

    private OkHttpClient client = new OkHttpClient();

    private String returnStr;
    private String beginQuote = encodeValue("\""), endQuote = encodeValue("\"");

    // createUser
    private String baseURL = "https://iq185u2wvk.execute-api.us-east-1.amazonaws.com/dev/";

    public RDS_Connect(){}


    public String doGetRequest(String userid, String email) throws Exception {

        String url = baseURL + "user?id="+ beginQuote +
                encodeValue(userid) + endQuote + "&email=" + beginQuote + encodeValue(email) + endQuote;

        getRequestHelper(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "call failed: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseStr = response.body().string();
                    returnStr = responseStr;
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

    private Call getRequestHelper(String url, Callback callback){

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
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

    private Call patchRequestHelper(String url, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(null, new byte[0]))
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
