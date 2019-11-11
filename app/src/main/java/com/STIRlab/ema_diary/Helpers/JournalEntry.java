package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JournalEntry {
    private final String TAG = "JOURNAL_ENTRY";

    private String submitTime, formattedTime, openTime, surveyId;
    private boolean complete;
    private String totalEarnings, incrementEarnings;

    public JournalEntry(String surveyId, String openTime, String submitTime, boolean complete, String totalEarnings, String incrementEarnings)
    {
        this.surveyId = surveyId;
        this.openTime = openTime;
        this.submitTime = submitTime;
        this.complete = complete;
        this.totalEarnings = totalEarnings;
        this.incrementEarnings = incrementEarnings;
    }

    public String getID()
    {
        return surveyId;
    }

    public String getFormattedTime(Context context) throws ParseException {
        Date ParsedDate;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        if (complete)
        {
            ParsedDate = InputDateFormat.parse(submitTime);
//            InputDateFormat.setTimeZone(TimeZone.getDefault());
//            String temp = InputDateFormat.format(ParsedDate);

            formattedTime = DateUtils.getRelativeDateTimeString(context, ParsedDate.getTime(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_CAP_AMPM
                            | DateUtils.FORMAT_CAP_MIDNIGHT).toString();

            formattedTime = formattedTime.replace(", ", " at ");
        }
        else
        {
            ParsedDate = InputDateFormat.parse(openTime);

            long now = System.currentTimeMillis();

            formattedTime = DateUtils.getRelativeTimeSpanString(ParsedDate.getTime(), now, DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_CAP_AMPM).toString();

            formattedTime += " by Midnight";


        }

        return formattedTime;
    }

    public boolean isComplete(){

        return complete;
    }

    public String gettotalEarnings()
    {
        return totalEarnings;
    }

    public String getIncrementEarnings(){ return incrementEarnings; }

    public String getOpenTime() { return openTime; }

}
