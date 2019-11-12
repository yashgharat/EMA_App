package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.Helpers.ThoughtEntry;
import com.STIRlab.ema_diary.Helpers.ThoughtEntryAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ThoughtsHistoryActivity extends AppCompatActivity {

    private ThoughtEntryAdapter adapter;
    private List<ThoughtEntry> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RDS_Connect client;
    private SharedPreferences SP;

    private FloatingActionButton previous;

    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        client = new RDS_Connect();
        userId = SP.getString("username", null);

        swipeRefreshLayout = findViewById(R.id.thoughts_swipe);
        previous = findViewById(R.id.thoughtsHistoryPrevious);

        recyclerView = findViewById(R.id.recyclerThoughts);
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
            history = client.getThoughtEntries(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new ThoughtEntryAdapter(this, history);
        recyclerView.setAdapter(adapter);
    }
}
