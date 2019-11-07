package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.STIRlab.ema_diary.R;

public class MoreInfoActivity extends AppCompatActivity {

    private Button btnGrantAccess;
    private ImageView previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        btnGrantAccess = findViewById(R.id.btnGrantAccess2);
        previous = findViewById(R.id.moreinfoBack);

        btnGrantAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MoreInfoActivity.this, TutorialActivity.class);
                finish();
                startActivity(i);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
