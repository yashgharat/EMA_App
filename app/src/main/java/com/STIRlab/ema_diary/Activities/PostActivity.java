package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.NotificationHelper;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PostActivity extends AppCompatActivity {
    private final String TAG = "POST";

    private Button surveyBtn;
    private FloatingActionButton close;

    private APIHelper client;
    private SharedPreferences SP;
    private CognitoSettings cognitoSettings;
    private NotificationHelper notificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email, PostActivity.this);
        client.makeCognitoSettings(this);

        cognitoSettings = new CognitoSettings(this);

        notificationHelper = new NotificationHelper(this);
        notificationHelper.cancelNotification();

        surveyBtn = findViewById(R.id.button_post);
        close = findViewById(R.id.post_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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

        try {
            Log.i(TAG, client.getUser(false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
