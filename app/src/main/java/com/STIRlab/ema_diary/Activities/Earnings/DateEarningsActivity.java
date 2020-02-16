package com.STIRlab.ema_diary.Activities.Earnings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.STIRlab.ema_diary.Activities.PinActivity;
import com.STIRlab.ema_diary.Helpers.EarningsPeriod;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

public class DateEarningsActivity extends AppCompatActivity {

    private FloatingActionButton prev;

    private TextView title, date, approvedCount, surveyBonusTitle, thoughtBonusTitle;
    private TextView basicEarnings, surveyBonus, screenshotBonus, earningsAdded;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_earnings);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);


        EarningsPeriod item = (EarningsPeriod) getIntent().getSerializableExtra("period");

        prev = findViewById(R.id.date_earnings_previous);
        title = findViewById(R.id.date_earnings_title);
        date = findViewById(R.id.date_sub);

        surveyBonusTitle = findViewById(R.id.survey_bonus_title);
        thoughtBonusTitle = findViewById(R.id.thought_bonus_title);

        approvedCount = findViewById(R.id.approved_entries_title);
        basicEarnings = findViewById(R.id.basic_earnings);
        surveyBonus = findViewById(R.id.survey_bonus);
        screenshotBonus = findViewById(R.id.thought_bonus);

        earningsAdded = findViewById(R.id.earnings_added);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            if (item.getFormattedDate().equals("Current Earnings")) {
                title.setText(item.getFormattedDate());
                date.setText(item.dateFormatter());
            } else {
                title.setText("Past Earnings");
                date.setText(item.getFormattedDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (item.getApprove() != 1) {
            approvedCount.setText(item.getApprove() + " Approved Journal Entries");
        } else {
            approvedCount.setText("1 Approved Journal Entry");
        }

        basicEarnings.setText(currencyFormat(item.getSurveyBasicEarnings()));
        surveyBonus.setText(currencyFormat(item.getSurveyBonusEarnings()));
        screenshotBonus.setText(currencyFormat(item.getThoughtsBonusEarnings()));
        earningsAdded.setText(currencyFormat(item.getIncrement()));

        surveyBonusTitle.setCompoundDrawableTintList(ColorStateList.valueOf(getBonusColor(item.getSurveysBonus(), this)));
        thoughtBonusTitle.setCompoundDrawableTintList(ColorStateList.valueOf(getBonusColor(item.getThoughtsBonus(), this)));



        if (item.getSurveyBasicEarnings() == 0)
            basicEarnings.setTextColor(getColor(R.color.disabled));
        else
            basicEarnings.setTextColor(getColor(R.color.positive));

        if (item.getSurveyBonusEarnings() == 0)
            surveyBonus.setTextColor(getColor(R.color.disabled));
        else
            surveyBonus.setTextColor(getColor(R.color.positive));

        if (item.getThoughtsBonusEarnings() == 0)
            screenshotBonus.setTextColor(getColor(R.color.disabled));
        else
            screenshotBonus.setTextColor(getColor(R.color.positive));

        if (item.getIncrement() == 0)
            earningsAdded.setTextColor(getColor(R.color.disabled));
        else
            earningsAdded.setTextColor(getColor(R.color.positive));


    }

    private int getBonusColor(String status, Context context){
        if(status.equals("submitted"))
        {
            return context.getColor(R.color.primaryDark);
        }
        else if(status.equals("open"))
        {
            return context.getColor(R.color.neutral);
        }
        else if(status.equals("missed"))
        {
            return context.getColor(R.color.disabled);
        }
        else if(status.equals("approved"))
        {
            return context.getColor(R.color.positive);
        }
        return 0;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(SP.getString("Pin", null) != null)
            startActivity(new Intent(this, PinActivity.class));

    }

    private String currencyFormat(double amount){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(Locale.getDefault()));

        return format.format(amount);
    }
}
