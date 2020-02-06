package com.STIRlab.ema_diary.Activities.Earnings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.JournalEntryAdapter;
import com.STIRlab.ema_diary.Helpers.earningsPeriod;
import com.STIRlab.ema_diary.Helpers.earningsPeriodAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class allEarningsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<earningsPeriod> earnings;
    private earningsPeriodAdapter adapter;

    private FloatingActionButton prev;
    private org.fabiomsr.moneytextview.MoneyTextView allEarnings;
    private TextView possible_earnings, label;

    private APIHelper client;
    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_earnings);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email);


        prev = findViewById(R.id.allEarningsPrevious);
        allEarnings = findViewById(R.id.all_earnings);
        possible_earnings = findViewById(R.id.possible_earnings);
        label = findViewById(R.id.when_empty_earnings);

        swipeRefreshLayout = findViewById(R.id.earnings_swipe);

        recyclerView = findViewById(R.id.recyclerEarnings);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();

        try {
            allEarnings.setAmount(client.getTotalEarnings());
            possible_earnings.setText("Will earn $" + client.getPossibleEarnings() + " if pending items are approved");
        } catch (Exception e) {
            e.printStackTrace();
        }


        prev.setOnClickListener(new View.OnClickListener() {
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
            earnings = client.getPeriods();
            Log.i("EARNINGS", earnings.get(0).getSurveys_bonus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(earnings != null) {
            label.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new earningsPeriodAdapter(this, earnings);
            recyclerView.setAdapter(adapter);
        }
        else {
            label.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }
}
