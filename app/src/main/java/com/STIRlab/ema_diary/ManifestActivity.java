package com.STIRlab.ema_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ManifestActivity extends AppCompatActivity {

    private Button btnGrant;
    private TextView lblMoreInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifest);

        btnGrant = findViewById(R.id.btnGrantAccess);
        lblMoreInfo = findViewById(R.id.moreInfo);

        btnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManifestActivity.this, CollectingInformation.class);
                finish();
                startActivity(i);
            }
        });
    }
}
