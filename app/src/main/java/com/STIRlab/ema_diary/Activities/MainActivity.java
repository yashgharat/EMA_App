package com.STIRlab.ema_diary.Activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.NotifyPublisher;
import com.STIRlab.ema_diary.Helpers.ScrapeDataHelper;
import com.STIRlab.ema_diary.R;
import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";

    private SharedPreferences SP;
    private Handler mHandler = new Handler();
    private AlertDialog userDialog;

    private TextView viewHistory_1, viewHistory_2;
    private TextView numSurveys;

    private CardView cardJournal;
    private CardView cardSettings;
    private CardView cardHelp;
    private CardView cardThoughts;

    private LinearLayout layoutJournal;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CognitoSettings cognitoSettings;
    private CognitoUserPool pool;
    private CognitoUserSession session;
    private RDS_Connect client;
    private ScrapeDataHelper scraper;

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
            client.getUser(username, email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        scraper = new ScrapeDataHelper(this);


        viewHistory_1 = findViewById(R.id.viewHistory_1);



        cardJournal = findViewById(R.id.cardJournal);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelp = findViewById(R.id.cardHelp);
        cardThoughts = findViewById(R.id.cardThoughts);

        layoutJournal = findViewById(R.id.layoutJournal);

        numSurveys = findViewById(R.id.numSurveys);


        swipeRefreshLayout = findViewById(R.id.main_swipe);


        if (SP.getBoolean("virgin", true)) {
            SP.edit().putBoolean("virgin", false).apply();

            Intent i = new Intent(this, newPassword.class);
            startActivityForResult(i, 10);

            SP.edit().putInt("hour", 14).apply();
            SP.edit().putInt("minute", 0).apply();

            try {
                client.startStudy(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SP.edit().putBoolean("Remember", true).apply();

        try {
            Log.i(TAG, client.getUser(username, email));
        } catch (Exception e) {
            Log.i(TAG, CognitoSettings.formatException(e));
        }

        try {
            String pass = client.finishedPost(username, email);
            if(!pass.equals("null"))
            {
                Intent post = new Intent(this, PostActivity.class);
                post.putExtra("data", pass);
                startActivity(post);
            }
        } catch (Exception e) {
            Log.i(TAG, CognitoSettings.formatException(e));
        }

        setNotification();

        String curEarnings = client.getEarnings(username, email);

        if(curEarnings == null)
            curEarnings = "0";

        numSurveys.setText(client.getSurveyCount(username,email));
        userProgress.setProgress((30-Integer.parseInt(client.getDaysLeft(username, email)))*3);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                client.getSurveyStatus(username, email);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        String status = client.getSurveyStatus(username, email);
        TextView cardTitle = findViewById(R.id.titleJournal);
        TextView cardMsg = findViewById(R.id.msgJournal);



        if(status == null || status.equals("closed"))
        {
            cardTitle.setText("Daily Journal Later Today");
            cardMsg.setText("Available from 2PM to midnight");

            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.primaryDark)),
                    new ColorDrawable(getResources().getColor(R.color.themeBackground)));
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

        }
        else if(status.equals("pending"))
        {
            String url = client.getResumeUrl(username, email);
            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Daily Journal in Progress");
            cardMsg.setText("Due by Midnight");
            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.apparent)),
                    new ColorDrawable(getResources().getColor(R.color.secondary)));
            layoutJournal.setBackground(getDrawable(R.drawable.ripple_effect));

            cardJournal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                    builder.setShowTitle(true);

                    CustomTabsIntent viewSurvey = builder.build();
                    viewSurvey.launchUrl(MainActivity.this, Uri.parse(url));


                }
            });

        }
        else if(status.equals("open"))
        {
            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Ready for Daily Journal");
            cardMsg.setText("Due by Midnight");
            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.apparent)),
                    new ColorDrawable(getResources().getColor(R.color.primaryDark)));
            layoutJournal.setBackground(getDrawable(R.drawable.ripple_effect));

            cardJournal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "http://ucf.qualtrics.com/jfe/form/SV_9z6wKsiRjfT6hJb?user_id=" + username;

                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                    builder.setShowTitle(true);

                    CustomTabsIntent viewSurvey = builder.build();
                    viewSurvey.launchUrl(MainActivity.this, Uri.parse(url));

                    scraper.scrape();
                    SP.edit().putLong("total_screen_time", 0).apply();

                }
            });

        }
        else if(status.equals("submitted"))
        {
            cardTitle.setText("Daily Journal Complete");
            cardMsg.setText("Available again tomorrow at 2PM");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);
            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.primaryDark)),
                    new ColorDrawable(getResources().getColor(R.color.themeBackground)));
        }

        viewHistory_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHistory_1.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, JournalHistoryActivity.class);
                startActivityForResult(intent, 15);
            }
        });

        viewHistory_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHistory_2.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, screenshotHistoryActivity.class);
                startActivityForResult(intent, 15);
            }
        });

        cardThoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, screenshotPromptActivity.class);
                startActivityForResult(i, 40);
            }
        });

        cardHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivityForResult(i, 15);
            }
        });

        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, 50);
            }
        });

//        Button scrapeBtn = findViewById(R.id.scrapeBtn);
//        scrapeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                scraper.scrape();
//            }
//        });


    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.journaldev.AN_INTENT");
        intent.setComponent(new ComponentName(getPackageName(),"com.STIRlab.ema_diary.Helpers.NotifyPublisher"));
        getApplicationContext().sendBroadcast(intent);
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
                setNotification();
                break;
            case 15:
                break;
        }
    }

    public void setNotification(){

        createNotificationChannel();

        int hour = SP.getInt("hour", 14);
        int min = SP.getInt("minute", 0);

        Intent i = new Intent(this, NotifyPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 200,i, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.i(TAG, hour + ":"+ min);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        Log.i(TAG, String.valueOf(calendar.getTimeInMillis() - System.currentTimeMillis()));

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void setCardColorTran(LinearLayout layout, ColorDrawable start, ColorDrawable end) {
        ColorDrawable[] color = {start, end};
        TransitionDrawable trans = new TransitionDrawable(color);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            layout.setBackground(trans);
        } else {
            layout.setBackgroundDrawable(trans);
        }
        trans.startTransition(1000);
    }

    public void createNotificationChannel()
    {

        NotificationChannel notificationChannel = new NotificationChannel("notifyUser",
                "Daily notification", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.CYAN);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("test notification");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();

        viewHistory_1.setEnabled(true);
        viewHistory_2.setEnabled(true);
    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    onBackPressed();
                }
            }
        });
        userDialog = builder.create();
        userDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        userDialog.show();

    }


}
