package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JournalEntry implements Serializable {
    private final String TAG = "JOURNAL_ENTRY";

    private String surveyId, status;
    private String submitTime, formattedTime, openTime;
    private String decisionTime, formattedDecisionTime;
    private String researcherMessage;

    public JournalEntry(String surveyId, String openTime, String submitTime, String status, String decisionTime, String researcherMessage) {
        this.surveyId = surveyId;
        this.openTime = openTime;
        this.submitTime = submitTime;
        this.status = status;
        this.decisionTime = decisionTime;
        this.researcherMessage = researcherMessage;
    }

    public String getID() {
        return surveyId;
    }

    public String getFormattedTime(Context context) throws ParseException {
        Date ParsedDate;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        if (!status.equals("missed") && !submitTime.equals("null")) {
            ParsedDate = InputDateFormat.parse(submitTime);

            formattedTime = DateUtils.getRelativeDateTimeString(context, ParsedDate.getTime(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    DateUtils.FORMAT_CAP_AMPM
                            | DateUtils.FORMAT_CAP_MIDNIGHT).toString();

            formattedTime = formattedTime.replace(", ", " at ");
        } else {
            ParsedDate = InputDateFormat.parse(openTime);

            long now = System.currentTimeMillis();

            formattedTime = DateUtils.getRelativeTimeSpanString(ParsedDate.getTime(), now, DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_CAP_AMPM).toString();

        }

        return formattedTime;
    }


    public String getFormattedDecisionTime(Context context) throws ParseException {
        Date ParsedDate;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
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


    public String getOpenTime() {
        return openTime;
    }

    public String getResearcherMessage() {
        return researcherMessage;
    }

}
