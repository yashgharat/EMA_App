package com.STIRlab.ema_diary.Activities.Screenshots;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.Thought;
import com.STIRlab.ema_diary.R;

public class IndividualScreenshotActivity extends AppCompatActivity {

    private TextView submitTimeText, decisionText, decisionTimeText, researcherMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_screenshot);
        Thought entry = (Thought) getIntent().getSerializableExtra("entry");

        submitTimeText = findViewById(R.id.individual_screenshot_sub);
        decisionText = findViewById(R.id.individual_screenshot_decision);
        decisionTimeText = findViewById(R.id.individual_screenshot_decision_date);
        researcherMessageText = findViewById(R.id.individual_screenshot_message);

    }
}
