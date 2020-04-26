package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.EarningsPeriod;
import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.R;

public class IndividualJournalEntryActivity extends AppCompatActivity {

    private TextView submitTimeText, decisionText, decisionTimeText, researcherMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_journal_entry);
        JournalEntry entry = (JournalEntry) getIntent().getSerializableExtra("entry");

        submitTimeText = findViewById(R.id.individual_journal_entry_sub);
        decisionText = findViewById(R.id.individual_journal_entry_decision);
        decisionTimeText = findViewById(R.id.individual_journal_entry_decision_date);
        researcherMessageText = findViewById(R.id.individual_journal_entry_message);



    }
}
