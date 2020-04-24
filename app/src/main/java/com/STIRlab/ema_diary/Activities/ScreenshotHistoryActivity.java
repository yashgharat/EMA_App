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
        client = new APIHelper(userId, email, ScreenshotHistoryActivity.this);
        client.makeCognitoSettings(this);

        swipeRefreshLayout = findViewById(R.id.screenshots_swipe);
        previous = findViewById(R.id.screenshots_history_previous);
        label = findViewById(R.id.when_empty);

        recyclerView = findViewById(R.id.recycler_screenshots);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            init(this);
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
                    init(ScreenshotHistoryActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void init(Context context) throws Exception{
        Thread t = new Thread(() -> {
        try {
            history = client.getThoughtEntries();
        } catch (Exception e) {
            e.printStackTrace();
        }

            ScreenshotHistoryActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if(history != null && history.size() > 0) {
                        label.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new ThoughtAdapter(context, history);
                        recyclerView.setAdapter(adapter);
                    }
                    else {
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
    public void onResume(){
        super.onResume();

    }
}
