package com.example.ema_diary;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class CognitoSettings {
    private String userPoolId = "us-east-1_CTJ2jfjlB";
    private String clientId = "3pa7lb4mrprsaaqutoua1vvi3t";
    private String clientSecret = "143c0dti692kj0r405bhv8l97eq0lc4su4ueejapjv1fuo11kpte";
    private Regions cognitoRegion = Regions.US_EAST_1;
    private CognitoDevice thisDevice;


    private Context context;

    public CognitoSettings(Context context){
        this.context = context;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Regions getCognitoRegion() {
        return cognitoRegion;
    }

    public void setThisDevice(CognitoDevice thisDevice) {
        this.thisDevice = thisDevice;
    }

    public CognitoDevice getThisDevice(){
        return thisDevice;
    }

    public CognitoUserPool getUserPool(){
        return new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
    }
}
