package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class JournalHistoryActivity extends AppCompatActivity {
    private final String TAG = "JOURNAL_HISTORY";

    private JournalEntryAdapter adapter;

    private List<JournalEntry> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView label;
    private String username;

    private FloatingActionButton previous;

    private APIHelper client;
    private CognitoSettings cognitoSettings;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email, JournalHistoryActivity.this);
        client.makeCognitoSettings(this);

        previous = findViewById(R.id.journal_history_previous);

        label = findViewById(R.id.when_empty_journal);

        swipeRefreshLayout = findViewById(R.id.journal_swipe);

        recyclerView = findViewById(R.id.recycler_journal);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            init(JournalHistoryActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    init(JournalHistoryActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(Context context) throws Exception {
        Thread t = new Thread(() -> {
            try {
                history = client.getJournalEntries();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JournalHistoryActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (history != null && history.size() > 0) {
                        label.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new JournalEntryAdapter(context, history);
                        recyclerView.setAdapter(adapter);
                    } else {
                        label.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        });


        swipeRefreshLayout.setRefreshing(true);
        t.start();
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
