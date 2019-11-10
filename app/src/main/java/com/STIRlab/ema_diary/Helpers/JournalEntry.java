package com.STIRlab.ema_diary.Helpers;

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

    public String getFormattedTime(){
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
