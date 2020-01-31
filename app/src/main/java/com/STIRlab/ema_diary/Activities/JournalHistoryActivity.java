package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class JournalHistoryActivity extends AppCompatActivity {
    private final String TAG = "JOURNAL_HISTORY";

    private JournalEntryAdapter adapter;

    private List<JournalEntry> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scroll;

    private TextView label;
    private String username;

    private FloatingActionButton previous;



    private APIHelper client;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email);

        previous = findViewById(R.id.journalHistoryPrevious);


        label = findViewById(R.id.when_empty_journal);

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
            history = client.getJournalEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(history != null) {
            label.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new JournalEntryAdapter(this, history);
            recyclerView.setAdapter(adapter);
        }
        else {
            label.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }
}
