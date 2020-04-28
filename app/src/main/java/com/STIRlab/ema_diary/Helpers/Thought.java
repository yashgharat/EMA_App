package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Thought implements Serializable {

    private static final String TAG = Thought.class.getSimpleName();
    private String thoughtID, status;
    private String submitTime, formattedTime;
    private String decisionTime, formattedDecisionTime;
    private String researcherMessage;

    public Thought(String screenshotID, String submitTime, String status, String decisionTime, String researcherMessage) {
        this.thoughtID = screenshotID;
        this.submitTime = submitTime;
        this.status = status;
        this.decisionTime = decisionTime;
        Log.e(TAG, decisionTime);
        this.researcherMessage = researcherMessage;
    }

    public String getThoughtID() {
        return thoughtID;
    }

    public String getFormattedTime(Context context) throws ParseException {
        Date ParsedDate;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        ParsedDate = InputDateFormat.parse(submitTime);

        formattedTime = DateUtils.getRelativeDateTimeString(context, ParsedDate.getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_CAP_AMPM
                        | DateUtils.FORMAT_CAP_MIDNIGHT).toString();

        formattedTime = formattedTime.replace(", ", " at ");

        return formattedTime;
    }

    public String getFormattedDecisionTime(Context context) throws ParseException {
        Date ParsedDate;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        ParsedDate = InputDateFormat.parse(decisionTime);

        formattedDecisionTime = DateUtils.getRelativeDateTimeString(context, ParsedDate.getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_CAP_AMPM
                        | DateUtils.FORMAT_CAP_MIDNIGHT).toString();

        formattedDecisionTime = formattedDecisionTime.replace(", ", " at ");

        return formattedDecisionTime;
    }


    public String getStatus() {
        return status;
    }


    public String getResearcherMessage() {
        return researcherMessage;
    }
}
