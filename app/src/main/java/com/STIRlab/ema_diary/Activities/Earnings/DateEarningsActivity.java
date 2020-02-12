package com.STIRlab.ema_diary.Activities.Earnings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.EarningsPeriod;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;

public class DateEarningsActivity extends AppCompatActivity {

    private FloatingActionButton prev;

    private TextView title, date, approvedCount;
    private TextView basicEarnings, surveyBonus, screenshotBonus, earningsAdded;
    private DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_earnings);


        EarningsPeriod item = (EarningsPeriod) getIntent().getSerializableExtra("period");

        prev = findViewById(R.id.date_earnings_previous);
        title = findViewById(R.id.date_earnings_title);
        date = findViewById(R.id.date_sub);

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

        basicEarnings.setText("$" + df.format(item.getSurveyBasicEarnings()));
        surveyBonus.setText("$" + df.format(item.getSurveyBonusEarnings()));
        screenshotBonus.setText("$" + df.format(item.getThoughtsBonusEarnings()));
        earningsAdded.setText("$" + df.format(item.getIncrement()));


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
}
