package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Thought {

    private String thoughtID, status;
    private String submitTime, formattedTime;

    public Thought(String screenshotID, String submitTime, String status) {
        this.thoughtID = screenshotID;
        this.submitTime = submitTime;
        this.status = status;
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


    public String getStatus() {
        return status;
    }


}
