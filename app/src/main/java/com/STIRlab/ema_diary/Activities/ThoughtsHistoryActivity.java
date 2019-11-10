package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.Helpers.ThoughtEntry;
import com.STIRlab.ema_diary.Helpers.ThoughtEntryAdapter;
import com.STIRlab.ema_diary.R;

import java.util.ArrayList;
import java.util.List;

public class ThoughtsHistoryActivity extends AppCompatActivity {

    private ThoughtEntryAdapter adapter;
    private List<ThoughtEntry> history;
    private RecyclerView recyclerView;

    private RDS_Connect client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_history);

        client = new RDS_Connect();

        recyclerView = findViewById(R.id.recyclerThoughts);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        history = new ArrayList<ThoughtEntry>();

        //TODO: add logic for grabbing array from AWS

        adapter = new ThoughtEntryAdapter(this, history);

        recyclerView.setAdapter(adapter);
    }
}
