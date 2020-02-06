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
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Activities.Earnings.allEarningsActivity;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.NotifyPublisher;
import com.STIRlab.ema_diary.Helpers.ScrapeDataHelper;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";

    private SharedPreferences SP;
    private Handler mHandler = new Handler();
    private AlertDialog userDialog;

    private TextView viewHistory_1, viewHistory_2, totalEntries, totalScreenshots, studyCounter;
    private TextView numSurveys, cardTitle, cardMsg, viewEarnings;

    private org.fabiomsr.moneytextview.MoneyTextView totalEarnings;

    private CardView cardJournal;
    private CardView cardSettings;
    private CardView cardHelp;
    private CardView cardscreenshots;

    private LinearLayout layoutJournal;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CognitoSettings cognitoSettings;
    private CognitoUserPool pool;
    private CognitoUserSession session;
    private APIHelper client;
    private ScrapeDataHelper scraper;

    private JSONArray statuses;

    private ImageView[] progressBar;
    private ImageView info, journalState;

    private String cardStatus, username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        username = SP.getString("username", "null");
        email = SP.getString("email", "null");
        setContentView(R.layout.activity_main);

        progressBar = new ImageView[]{findViewById(R.id.prog_0), findViewById(R.id.prog_1), findViewById(R.id.prog_2),
                findViewById(R.id.prog_3), findViewById(R.id.prog_4)};

        cognitoSettings = new CognitoSettings(this);
        pool = cognitoSettings.getUserPool();
        cognitoSettings.refreshSession(SP);

        client = new APIHelper(username, email);
        try {
            client.getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scraper = new ScrapeDataHelper(this);


        viewHistory_1 = findViewById(R.id.viewHistory_1);
        viewHistory_2 = findViewById(R.id.viewHistory_2);
        viewEarnings = findViewById(R.id.view_earnings);

        cardJournal = findViewById(R.id.cardJournal);
        cardSettings = findViewById(R.id.cardSettings);
        cardHelp = findViewById(R.id.cardHelp);
        cardscreenshots = findViewById(R.id.cardscreenshots);

        totalEarnings = findViewById(R.id.total_earnings);
        totalEntries = findViewById(R.id.total_entries);
        totalScreenshots = findViewById(R.id.total_screenshots);
        studyCounter = findViewById(R.id.study_ctr);

        layoutJournal = findViewById(R.id.layoutJournal);
        journalState = findViewById(R.id.journalDrawable);

        numSurveys = findViewById(R.id.numSurveys);

        swipeRefreshLayout = findViewById(R.id.main_swipe);

        int currency = Integer.parseInt(client.getEarnings());


        studyCounter.setText(client.getDaysLeft());
        totalEarnings.setAmount(Float.parseFloat(client.getEarnings()));
        totalEntries.setText(client.getEntryCount());
        totalScreenshots.setText(client.getScreenshotCount());

        try {
            statuses = client.getStatuses();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        info = findViewById(R.id.main_info);

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
            Log.i(TAG, client.getUser());
        } catch (Exception e) {
            Log.i(TAG, CognitoSettings.formatException(e));
        }

        try {
            String pass = client.finishedPost();
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

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, infoActivity.class);
                startActivity(intent);
            }
        });

        numSurveys.setText(client.getSurveyCount());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    updateProgress();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                setCardColor();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        cardStatus = null;
        try {
            cardStatus = statuses.getString(statuses.length()-1);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        cardTitle = findViewById(R.id.titleJournal);
        cardMsg = findViewById(R.id.msgJournal);

        try {
            updateProgress();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        setCardColor();


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

        viewEarnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewEarnings.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, allEarningsActivity.class);
                startActivityForResult(intent, 15);
            }
        });


        cardscreenshots.setOnClickListener(new View.OnClickListener() {
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

    public void updateProgress() throws Exception {
        for(int i = 0; i < 5; i++)
        {
            String curStatus = statuses.getString(i);

            int curDay = client.getPeriodCount();

            if(curStatus == null)
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.normal));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.normal));
            }
            else if(i == curDay - 1)
            {
                if(curStatus.equals("approved"))
                {
                    progressBar[i].setColorFilter(Color.parseColor("#A7E9DB"));
                    progressBar[i].setBackgroundColor(getResources().getColor(R.color.positive));
                }
                else if(curStatus.equals("rejected"))
                {
                    progressBar[i].setColorFilter(Color.parseColor("#FFC0C5"));
                    progressBar[i].setBackgroundColor(getResources().getColor(R.color.destructive));
                }
                else if(curStatus.equals("submitted"))
                {
                    progressBar[i].setColorFilter(Color.parseColor("#BAD2FF"));
                    progressBar[i].setBackgroundColor(getResources().getColor(R.color.primaryDark));
                }
                else if(curStatus.equals("pending") || curStatus.equals("open"))
                {
                    progressBar[i].setColorFilter(Color.parseColor("#FCF6DF"));
                    progressBar[i].setBackgroundColor(getResources().getColor(R.color.neutral));
                }
                else if(curStatus.equals("missed") || curStatus.equals("closed"))
                {
                    progressBar[i].setColorFilter(getResources().getColor(R.color.normal));
                    progressBar[i].setBackgroundColor(getResources().getColor(R.color.apparent));
                }
            }
            else if(curStatus.equals("approved"))
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.positive));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.themeBackground));

            }
            else if(curStatus.equals("rejected"))
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.destructive));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.themeBackground));
            }
            else if(curStatus.equals("submitted"))
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.primaryDark));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.themeBackground));
            }
            else if(curStatus.equals("pending") || curStatus.equals("open"))
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.neutral));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.themeBackground));
            }
            else if(curStatus.equals("missed") || curStatus.equals("closed"))
            {
                progressBar[i].setColorFilter(getResources().getColor(R.color.disabled));
                progressBar[i].setBackgroundColor(getResources().getColor(R.color.disabled));

            }

        }
    }

    public void updateEntryBonus(){

    }

    public void setCardColor()
    {
        Log.i(TAG, cardStatus);
        if(cardStatus == null || cardStatus.equals("closed") || cardStatus.equals("missed"))
        {
            cardTitle.setText("Come Back at 2 PM");
            cardMsg.setText("Daily Journal will be available later");

            cardTitle.setTextColor(getResources().getColor(R.color.black));
            cardMsg.setTextColor(getResources().getColor(R.color.normal));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_remove_circle_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.disabled));


            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.primaryDark)),
                    new ColorDrawable(getResources().getColor(R.color.themeBackground)));
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

        }
        else if(cardStatus.equals("pending"))
        {
            Log.i(TAG, "inPending");
            String url = client.getResumeUrl();
            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Finish Daily Journal Entry");
            cardMsg.setText("Complete by Midnight");

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.journal_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));

            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.themeBackground)),
                    new ColorDrawable(getResources().getColor(R.color.neutral)));
            layoutJournal.setBackground(getDrawable(R.drawable.ripple_effect_yellow));

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
        else if(cardStatus.equals("open"))
        {
            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Finish Daily Journal Entry");
            cardMsg.setText("Complete by Midnight");

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.journal_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));


            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.themeBackground)),
                    new ColorDrawable(getResources().getColor(R.color.neutral)));
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
        else if(cardStatus.equals("submitted"))
        {
            cardTitle.setText("Daily Journal Complete");
            cardMsg.setText("Waiting for researcher to approve");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_check_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));

            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.neutral)),
                    new ColorDrawable(getResources().getColor(R.color.primaryDark)));
        }
        else if(cardStatus.equals("approved"))
        {
            cardTitle.setText("Daily Journal Complete");
            cardMsg.setText("Researcher approved");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_check_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));


            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.primaryDark)),
                    new ColorDrawable(getResources().getColor(R.color.positive)));
        }
        else if(cardStatus.equals("rejected"))
        {
            cardTitle.setText("Daily Journal Complete");
            cardMsg.setText("Researcher approved");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_close_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));

            setCardColorTran(layoutJournal, new ColorDrawable(getResources().getColor(R.color.primaryDark)),
                    new ColorDrawable(getResources().getColor(R.color.destructive)));
        }
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
        viewEarnings.setEnabled(true);

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
