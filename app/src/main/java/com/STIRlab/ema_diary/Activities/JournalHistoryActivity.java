package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.R;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JournalHistoryActivity extends AppCompatActivity {
    private final String TAG = "JOURNAL_HISTORY";

    private JournalEntryAdapter adapter;

    private List<JournalEntry> history;

    private RecyclerView recyclerView;

    private RDS_Connect client;

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        client = new RDS_Connect();

        recyclerView = findViewById(R.id.recyclerJournal);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            history = client.getJournalEntries(SP.getString("username", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new JournalEntryAdapter(this, history);

        recyclerView.setAdapter(adapter);
    }
}
