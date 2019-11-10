package com.STIRlab.ema_diary.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class JournalEntry {

    private String submitTime, formattedTime, openTime, surveyId;
    private Boolean complete;
    private int totalEarnings, incrementEarnings;

    public JournalEntry(String surveyId, String openTime, String submitTime, Boolean complete, int totalEarnings, int incrementEarnings)
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

    public String getFormattedTime() throws ParseException {
        Date mParsedDate;
        String mOutputDateString;

        //yyyy-MM-dd'T'HH:mm:ss.SSS'Z'

        SimpleDateFormat mInputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        SimpleDateFormat mOutputDateFormat = new SimpleDateFormat("EEEE at HH:mm a", java.util.Locale.getDefault());

        mParsedDate = mInputDateFormat.parse(submitTime);
        mOutputDateString = mOutputDateFormat.format(mParsedDate);

        return formattedTime;
    }

    public Boolean isComplete(){
        return complete;
    }

    public int gettotalEarnings()
    {
        return totalEarnings;
    }

    public int getIncrementEarnings(){ return incrementEarnings; }

    public String getOpenTime() { return openTime; }
}
