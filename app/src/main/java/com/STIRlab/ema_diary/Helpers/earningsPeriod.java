package com.STIRlab.ema_diary.Helpers;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class earningsPeriod implements Serializable {

    private double earnings, increment, surveyBonusEarnings, thoughtsBonusEarnings, surveyBasicEarnings;
    private int surveys, thoughts, approve;
    private String start_date, end_date;
    private String surveys_bonus, thoughts_bonus;
    private boolean isFirst;


    public earningsPeriod(double earnings, double increment, double surveyBonusEarnings, double thoughtsBonusEarnings, double surveyBasicEarnings,
                          int surveys, int thoughts, int approve, String start_date, String end_date,
                          String surveys_bonus, String thoughts_bonus, boolean isFirst) {

        this.earnings = earnings;
        this.increment = increment;
        this.surveyBonusEarnings = surveyBonusEarnings;
        this.thoughtsBonusEarnings = thoughtsBonusEarnings;
        this.surveyBasicEarnings = surveyBasicEarnings;
        this.surveys = surveys;
        this.thoughts = thoughts;
        this.approve = approve;
        this.start_date = start_date;
        this.end_date = end_date;
        this.surveys_bonus = surveys_bonus;
        this.thoughts_bonus = thoughts_bonus;
        this.isFirst = isFirst;

    }


    public double getEarnings() {
        return earnings;
    }

    public double getIncrement() {
        return increment;
    }

    public int getSurveys() {
        return surveys;
    }

    public int getThoughts() {
        return thoughts;
    }

    public String getFormattedDate() throws ParseException {

        if (!isFirst) {
            return dateFormatter();

        } else {
            return  "Current Earnings";
        }
    }

    public String dateFormatter() throws ParseException {
        StringBuilder sb;
        Date Start, End;
        SimpleDateFormat startFormat;
        SimpleDateFormat endFormat;
        SimpleDateFormat InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Start = InputDateFormat.parse(start_date);
        End = InputDateFormat.parse(end_date);
        LocalDate startLocal = Start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocal = End.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startLocal.getYear() == endLocal.getYear()) {

            if (startLocal.getMonth() == endLocal.getMonth()) {
                startFormat = new SimpleDateFormat("MMMM d");
                endFormat = new SimpleDateFormat("d, yyyy");
            } else {
                startFormat = new SimpleDateFormat("MMM d ");
                endFormat = new SimpleDateFormat(" MMM d, yyyy");
            }

        } else {
            startFormat = new SimpleDateFormat("MMM d, yyyy ");
            endFormat = new SimpleDateFormat(" MMM d, yyyy");
        }

        return startFormat.format(Start) + "-" + endFormat.format(End);
    }

    public String getSurveys_bonus() {
        return surveys_bonus;
    }

    public String getThoughts_bonus() {
        return thoughts_bonus;
    }

    public double getSurveyBonusEarnings() {
        return surveyBonusEarnings;
    }

    public double getThoughtsBonusEarnings() {
        return thoughtsBonusEarnings;
    }

    public double getSurveyBasicEarnings() {
        return surveyBasicEarnings;
    }

    public int getApprove() {
        return approve;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
