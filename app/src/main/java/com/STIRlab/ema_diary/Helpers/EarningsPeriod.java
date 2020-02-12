package com.STIRlab.ema_diary.Helpers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;

public class EarningsPeriod implements Serializable {

    private double earnings, increment, surveyBonusEarnings, thoughtsBonusEarnings, surveyBasicEarnings;
    private int surveys, thoughts, approve;
    private String startDate, endDate;
    private String surveysBonus, thoughtsBonus;
    private boolean isFirst;


    public EarningsPeriod(double earnings, double increment, double surveyBonusEarnings, double thoughtsBonusEarnings, double surveyBasicEarnings,
                          int surveys, int thoughts, int approve, String startDate, String endDate,
                          String surveysBonus, String thoughtsBonus, boolean isFirst) {

        this.earnings = earnings;
        this.increment = increment;
        this.surveyBonusEarnings = surveyBonusEarnings;
        this.thoughtsBonusEarnings = thoughtsBonusEarnings;
        this.surveyBasicEarnings = surveyBasicEarnings;
        this.surveys = surveys;
        this.thoughts = thoughts;
        this.approve = approve;
        this.startDate = startDate;
        this.endDate = endDate;
        this.surveysBonus = surveysBonus;
        this.thoughtsBonus = thoughtsBonus;
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

        Start = InputDateFormat.parse(startDate);
        End = InputDateFormat.parse(endDate);

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.setTime(Start);
        endCal.setTime(End);


        if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)) {

            if (startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)) {
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

    public String getSurveysBonus() {
        return surveysBonus;
    }

    public String getThoughtsBonus() {
        return thoughtsBonus;
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
