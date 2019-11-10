package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.Helpers.ThoughtEntry;
import com.STIRlab.ema_diary.Helpers.ThoughtEntryAdapter;
import com.STIRlab.ema_diary.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ThoughtsHistoryActivity extends AppCompatActivity {

    private ThoughtEntryAdapter adapter;
    private List<ThoughtEntry> history;
    private RecyclerView recyclerView;

    private RDS_Connect client;

    private SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_history);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        client = new RDS_Connect();

        recyclerView = findViewById(R.id.recyclerThoughts);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO: add logic for grabbing array from AWS

        try {
            history = client.getThoughtEntries(SP.getString("username", null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ThoughtEntryAdapter(this, history);

        recyclerView.setAdapter(adapter);
    }
}
