package com.STIRlab.ema_diary.Activities.Earnings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class allEarningsActivity extends AppCompatActivity {

    private FloatingActionButton prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_earnings);


        prev = findViewById(R.id.allEarningsPrevious);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
