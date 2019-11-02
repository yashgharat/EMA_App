package com.STIRlab.ema_diary.Activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.NotificationService;
import com.STIRlab.ema_diary.Helpers.NotifyPublisher;
import com.STIRlab.ema_diary.R;
import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";



    private SharedPreferences SP;
    private Handler mHandler = new Handler();

    private TextView viewHistory_1, studyCounter, earnings;

    private CardView cardJournal;
    private CardView cardSettings;
    private CardView cardHelp;

    private int curCount = 30;

    private CognitoSettings cognitoSettings;
    private CognitoUserPool pool;
    private CognitoUserSession session;
    private RDS_Connect client;

    private NotificationService notifs;

    public ProgressBar userProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");
        setContentView(R.layout.activity_main);

        cognitoSettings = new CognitoSettings(this);
        pool = cognitoSettings.getUserPool();
        cognitoSettings.refreshSession(SP);

        client = new RDS_Connect();
        try {
            client.doGetRequest(username, email);
        } catch (Exception e) {
            e.printStackTrace();
        }


        viewHistory_1 = findViewById(R.id.viewHistory_1);
        userProgress = findViewById(R.id.progressBar);

        cardJournal = findViewById(R.id.cardJournal);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelp = findViewById(R.id.cardHelp);

        studyCounter = findViewById(R.id.studyCounter);
        earnings = findViewById(R.id.earnings);

        notifs = new NotificationService(this);
        notifs.createNotificationChannel();

        if (SP.getBoolean("virgin", true)) {

            Intent i = new Intent(this, newPassword.class);
            startActivityForResult(i, 10);

            SP.edit().putInt("screenTime", UsageEvents.Event.SCREEN_INTERACTIVE);

            //Log.i("VIRGIN: ", "HERE");
            SP.edit().putBoolean("virgin", false).apply();

            SP.edit().putInt("hour", 14).apply();
            SP.edit().putInt("minute", 0).apply();
            studyCounter.setText(String.valueOf(curCount));

            int localPin = SP.getInt("Pin", -1);
            if(localPin == -1)
            {
                SP.edit().putBoolean("Remember", true);
            }

        }

        int hour = SP.getInt("hour", 14);
        int min = SP.getInt("minute", 0);
        notifs.sendNotification(hour, min);

        earnings.setText("$"+ client.getEarnings(username, email));
        studyCounter.setText(client.getDaysLeft(username, email));

        viewHistory_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "here");

                try {
                    Log.i(TAG, client.getEarnings(username, email));
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        cardJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://ucf.qualtrics.com/jfe/form/SV_2i6xiz49SKg0JRb?user_id=" + username;

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                builder.setShowTitle(true);

                CustomTabsIntent viewSurvey = builder.build();
                viewSurvey.launchUrl(MainActivity.this, Uri.parse(url));

                userProgress.setProgress(userProgress.getProgress() + 3);

                studyCounter.setText(String.valueOf(--curCount));
            }
        });

        cardHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, 50);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                Intent i = new Intent(this, createPinUIActivity.class);
                startActivityForResult(i, 20);
            case 20:
                Intent o = new Intent(this, ManifestActivity.class);
                startActivityForResult(o, 30);
            case 30:
                break;
            case 50:
                Log.i(TAG, "in result");
                int hour = SP.getInt("hour", 14);
                int min = SP.getInt("minute", 0);
                notifs.sendNotification(hour, min);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

}
