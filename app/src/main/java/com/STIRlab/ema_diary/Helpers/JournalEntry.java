package com.STIRlab.ema_diary.Helpers;

public class JournalEntry {

    private String rawTime, formattedTime, surveyId;
    private Boolean complete;
    private int totalEarnings, incrementEarnings;

    public JournalEntry(String surveyId, String rawTime, Boolean complete, int totalEarnings, int incrementEarnings)
    {
        this.surveyId = surveyId;
        this.rawTime = rawTime;
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

    public int getIncrementEarnings(){
        return incrementEarnings;
    }
}
