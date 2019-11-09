package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JournalHistoryActivity extends AppCompatActivity {

    private JournalEntryAdapter adapter;

    private List<JournalEntry> history;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_history);

        recyclerView = findViewById(R.id.recyclerJournal);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        history = new ArrayList<JournalEntry>();

        //TODO: add logic for grabbing array from AWS

        adapter = new JournalEntryAdapter(this, history);

        recyclerView.setAdapter(adapter);
    }
}
