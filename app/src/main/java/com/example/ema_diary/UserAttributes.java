package com.example.ema_diary;

import android.content.Context;

public class UserAttributes {

    private Boolean remembered;
    private Boolean quick_signIn;
    private int uniqueID;
    public static int localPin;
    private String email;

    private static Boolean virgin = true;
    private Context context;

    public UserAttributes(Context context){
        this.remembered = false;
        this.quick_signIn = false;
        this.uniqueID = 00000;
        localPin = 0;
        this.email = "";
        this.context = context;
    }

    public UserAttributes(String email, Boolean remembered, Boolean quick_signIn){
        this.remembered = remembered;
        this.quick_signIn = quick_signIn;
//        this.uniqueID = uniqueID;
        //UserAttributes.localPin = localPin;
        this.email = email;
    }

    public Boolean getRemembered() {
        return remembered;
    }

    public void setRemembered(Boolean remembered) {
        this.remembered = remembered;
    }

    public Boolean getQuick_signIn() {
        return quick_signIn;
    }

    public void setQuick_signIn(Boolean quick_signIn) {
        this.quick_signIn = quick_signIn;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
       this.email = email;
    }

//    public UserAttributes getAttributes(){
//        return new UserAttributes(email, remembered, quick_signIn);
//    }

}
