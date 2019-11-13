package com.STIRlab.ema_diary.Helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SwitchTheme{
    private static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isDark = false;

    private static SwitchTheme singleton = null;
    private static Context sContext;

    private static SharedPreferences SP;

    public static SwitchTheme getInstance(Context context)
    {
        sContext = context;
        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        if(singleton == null)
        {
            singleton = new SwitchTheme();
        }
        return singleton;
    }


    public boolean isDark() {
        return isDark;
    }

    public void setIsDark(boolean isDark) {
        this.isDark = isDark;

        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean(NIGHT_MODE, isDark);
        editor.apply();
    }

}
