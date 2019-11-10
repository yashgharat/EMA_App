package com.STIRlab.ema_diary.Helpers;

public class ThoughtEntry {

    private String thoughtID;
    private String submitTime, formattedTime;
    private int screenshotCount;

    public ThoughtEntry(String thoughtID, String submitTime, int screenshotCount)
    {
        this.thoughtID = thoughtID;
        this.submitTime = submitTime;
        this.screenshotCount = screenshotCount;
    }

    public String getThoughtID() {
        return thoughtID;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public int getScreenshotCount() {
        return screenshotCount;
    }

    public String getScreenshotLabel(){
        return "done";
    }
}
