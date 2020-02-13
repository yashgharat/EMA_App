package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.Thought;
import com.STIRlab.ema_diary.Helpers.ThoughtAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ScreenshotHistoryActivity extends AppCompatActivity {

    private ThoughtAdapter adapter;
    private List<Thought> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private APIHelper client;
    private SharedPreferences SP;

    private FloatingActionButton previous;
    private TextView label;

    private String userId, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        userId = SP.getString("username", null);
        email = SP.getString("email", null);
        client = new APIHelper(userId, email);

        swipeRefreshLayout = findViewById(R.id.screenshots_swipe);
        previous = findViewById(R.id.screenshots_history_previous);
        label = findViewById(R.id.when_empty);

        recyclerView = findViewById(R.id.recycler_screenshots);
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
            history = client.getThoughtEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(history != null) {
            label.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ThoughtAdapter(this, history);
            recyclerView.setAdapter(adapter);
        }
        else {
            label.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(SP.getString("Pin", null) != null)
            startActivity(new Intent(this, PinActivity.class));

    }
}
