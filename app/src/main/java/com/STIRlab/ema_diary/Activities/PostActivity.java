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
import com.STIRlab.ema_diary.Helpers.NotifyPublisher;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class PostActivity extends AppCompatActivity {
    private final String TAG = "POST";

    private Button surveyBtn;
    private TextView signOut;

    private APIHelper client;
    private SharedPreferences SP;
    private CognitoSettings cognitoSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        client = new APIHelper(username, email);

        cognitoSettings = new CognitoSettings(this);


        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(this, NotifyPublisher.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getService(this, 0, updateServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            Log.e(TAG, "AlarmManager update was not canceled. " + CognitoSettings.formatException(e));
        }

        String data = getIntent().getStringExtra("data");

        surveyBtn = findViewById(R.id.button_post);
        signOut = findViewById(R.id.post_sign_out);


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


            if (!data.equals("null")) {
                Log.i(TAG, "HERE");
                surveyBtn.setVisibility(View.INVISIBLE);
            }

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cognitoSettings.getUserPool().getUser(SP.getString("email", "null")).globalSignOutInBackground(new GenericHandler() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "Logged out");

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "Log out failed");
                        Log.e(TAG, CognitoSettings.formatException(exception));
                    }
                });

                SP.edit().clear().apply();
                Intent i = new Intent(PostActivity.this, AuthenticationActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
