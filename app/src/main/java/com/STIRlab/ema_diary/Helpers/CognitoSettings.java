package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Regions;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.security.cert.CertificateException;

public class CognitoSettings {
    private static String TAG = "CognitoSettings";
    private String userPoolId = "us-east-1_CTJ2jfjlB";
    private String clientId = "3pa7lb4mrprsaaqutoua1vvi3t";
    private String clientSecret = "143c0dti692kj0r405bhv8l97eq0lc4su4ueejapjv1fuo11kpte";
    private Regions cognitoRegion = Regions.US_EAST_1;

    public static CognitoUser user = null;
    public static String oldPass = null;
    private static CognitoUserSession currSession;
    private KeyStoreHelper keyStoreHelper;
    private Context context;
    public static Context staticContext;
    public static int isLocked = 1;

    public CognitoSettings(Context context) {
        this.context = context;
        staticContext = context;
    }

    public static CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setCurrSession(CognitoUserSession currSession) {
        CognitoSettings.currSession = currSession;
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

    public void refreshSession(SharedPreferences SP){

        String email = SP.getString("email", "null");
        String dwString = SP.getString("dwString", "null");

        CognitoUserPool pool = getUserPool();
        CognitoUser thisUser = pool.getUser(email);
        try {
            keyStoreHelper = new KeyStoreHelper();
            Log.e(TAG, keyStoreHelper.decryptData("password" , KeyStoreHelper.encryption, keyStoreHelper.getIv()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        thisUser.getSession(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                Log.i(TAG, "in Auth Details..");

                /*need to get the userId & password to continue*/
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(email
                        , dwString, null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.i(TAG, "in Auth MFA Code..");

            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.i(TAG, "in Auth Challenge..");

            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG, "Failed: " + exception);

            }
        });
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e(TAG, " -- Error: "+exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if(temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if(temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return  formattedString;
    }

    public CognitoUserPool getUserPool() {
        return new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);
    }

}
