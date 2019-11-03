package com.STIRlab.ema_diary.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HelpActivity extends AppCompatActivity {

    private CardView Q1;

    private TextView Q1Ans;

    private FloatingActionButton ret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Q1 = findViewById(R.id.cardQ1);

        ret = findViewById(R.id.helpPrevious);

        Q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = findViewById(R.id.q1Arrow);
                TextView ans = findViewById(R.id.q1Answer);

                if(ans.getVisibility() == View.GONE){
                    iv.setImageResource(R.drawable.ic_keyboard_arrow_up_blue_30dp);
                    ans.setVisibility(View.VISIBLE);
                }
                else{
                    iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    ans.setVisibility(View.GONE);
                }

            }
        });

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
