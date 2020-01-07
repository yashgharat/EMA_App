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
import com.STIRlab.ema_diary.Helpers.ScreenshotEntry;
import com.STIRlab.ema_diary.Helpers.ScreenshotEntryAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class screenshotHistoryActivity extends AppCompatActivity {

    private ScreenshotEntryAdapter adapter;
    private List<ScreenshotEntry> history;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RDS_Connect client;
    private SharedPreferences SP;

    private FloatingActionButton previous;

    private String userId, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        userId = SP.getString("username", null);
        email = SP.getString("email", null);
        client = new RDS_Connect(userId, email);

        swipeRefreshLayout = findViewById(R.id.screenshots_swipe);
        previous = findViewById(R.id.screenshotsHistoryPrevious);

        recyclerView = findViewById(R.id.recyclerscreenshots);
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
            history = client.getScreenshotEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new ScreenshotEntryAdapter(this, history);
        recyclerView.setAdapter(adapter);
    }
}
