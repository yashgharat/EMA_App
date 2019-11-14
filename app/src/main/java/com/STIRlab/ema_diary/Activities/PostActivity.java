package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.STIRlab.ema_diary.R;

public class PostActivity extends AppCompatActivity {

    private Button surveyBtn;

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        String username = SP.getString("username", "null");

        surveyBtn = findViewById(R.id.btnPost);
        surveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://ucf.qualtrics.com/jfe/form/SV_8of6tU4A415j6tv" + username;

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(PostActivity.this, R.color.primaryDark));
                builder.setShowTitle(true);

                CustomTabsIntent viewSurvey = builder.build();
                viewSurvey.launchUrl(PostActivity.this, Uri.parse(url));

            }
        });
    }
}
