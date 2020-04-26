package com.STIRlab.ema_diary.Activities.Earnings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.EarningsPeriod;
import com.STIRlab.ema_diary.Helpers.EarningsPeriodAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class AllEarningsActivity extends AppCompatActivity {

    private static final String TAG = "ALL_EARNINGS";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<EarningsPeriod> earnings;
    private EarningsPeriodAdapter adapter;

    private FloatingActionButton prev;
    private TextView allEarnings, possibleEarnings, label;

    private APIHelper client;
    private SharedPreferences SP;
    private AlertDialog dialog;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_earnings);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email, AllEarningsActivity.this);
        client.makeCognitoSettings(this);


        prev = findViewById(R.id.all_earnings_previous);
        allEarnings = findViewById(R.id.all_earnings);
        possibleEarnings = findViewById(R.id.possible_earnings);

        swipeRefreshLayout = findViewById(R.id.earnings_swipe);

        recyclerView = findViewById(R.id.recycler_earnings);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            init(this);
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
                try {
                    init(AllEarningsActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void init(Context context) throws Exception {
        t = new Thread(() -> {
            try {
                earnings = client.getPeriods();
            } catch (Exception e) {
                e.printStackTrace();
            }

            double totalEarnings = client.getTotalEarnings();
            double maybeEarnings = client.getPossibleEarnings();

            AllEarningsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (earnings != null) {
                        adapter = new EarningsPeriodAdapter(context, earnings);
                        recyclerView.setAdapter(adapter);
                    }
                    try {
                        allEarnings.setText(currencyFormat(totalEarnings));
                        possibleEarnings.setText("Will earn " + currencyFormat(maybeEarnings) + " if the researcher approves the completeness and quality of remaining items");
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private String currencyFormat(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(Locale.getDefault()));

        return format.format(amount);
    }
}
