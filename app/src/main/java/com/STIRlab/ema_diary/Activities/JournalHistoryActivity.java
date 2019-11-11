package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JournalHistoryActivity extends AppCompatActivity {
    private final String TAG = "JOURNAL_HISTORY";

    private JournalEntryAdapter adapter;

    private List<JournalEntry> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView missedCount, completeCount;
    private String userId;

    private FloatingActionButton previous;

    private RDS_Connect client;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        userId = SP.getString("username", null);
        client = new RDS_Connect();

        previous = findViewById(R.id.journalHistoryPrevious);

        missedCount = findViewById(R.id.missedCount);
        completeCount = findViewById(R.id.completedCount);

        swipeRefreshLayout = findViewById(R.id.journal_swipe);

        recyclerView = findViewById(R.id.recyclerJournal);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void init(){
        try {
            history = client.getJournalEntries(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new JournalEntryAdapter(JournalHistoryActivity.this, history);
        recyclerView.setAdapter(adapter);
        try {
            completeCount.setText(String.valueOf(client.getNumCompleted(userId)));
            missedCount.setText(String.valueOf(client.getNumMissed(userId)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
