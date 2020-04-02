package com.STIRlab.ema_diary.Activities;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Activities.Earnings.AllEarningsActivity;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.KeyStoreHelper;
import com.STIRlab.ema_diary.Helpers.LifeCycleHelper;
import com.STIRlab.ema_diary.Helpers.NotificationHelper;
import com.STIRlab.ema_diary.Helpers.ScrapeDataHelper;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";
    private final static String KEY_ALIAS = "ANDROID_KEY";

    private SharedPreferences SP;
    private Handler mHandler = new Handler();
    private AlertDialog userDialog;

    private TextView totalEarnings, totalEntries, totalScreenshots, studyCounter;
    private TextView numSurveys, numScreenshots, cardTitle, cardMsg, viewEarnings;

    private CardView cardJournal;
    private CardView cardSettings;
    private CardView cardHelp;
    private CardView cardscreenshots;
    private CardView cardViewEntries, cardViewScreenshots;

    private ConstraintLayout layoutJournal, surveyHistory, screenshotHistory;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CognitoSettings cognitoSettings;
    private CognitoUserPool pool;
    private CognitoUserSession session;
    private APIHelper client;
    private ScrapeDataHelper scraper;
    private NotificationHelper notificationHelper;
    private LifeCycleHelper lifeCycleHelper;
    KeyStoreHelper keyStoreHelper;

    private int statusLength;
    private JSONArray statuses;

    private ImageView[] progressBar;
    private ImageView info, journalState, dashLogo, screenshotState;

    private String cardStatus, username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        username = SP.getString("username", "null");
        email = SP.getString("email", "null");
        setContentView(R.layout.activity_main);

        progressBar = new ImageView[]{findViewById(R.id.prog_0), findViewById(R.id.prog_1), findViewById(R.id.prog_2),
                findViewById(R.id.prog_3), findViewById(R.id.prog_4)};

        cognitoSettings = new CognitoSettings(this);
        pool = cognitoSettings.getUserPool();
        cognitoSettings.getToken(SP);

        try {
            keyStoreHelper = new KeyStoreHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client = new APIHelper(username, email);
        scraper = new ScrapeDataHelper(this);
        notificationHelper = new NotificationHelper(this);
        lifeCycleHelper = new LifeCycleHelper(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifeCycleHelper);

        viewEarnings = findViewById(R.id.view_earnings);

        cardJournal = findViewById(R.id.card_journal);
        cardSettings = findViewById(R.id.card_settings);
        cardHelp = findViewById(R.id.card_help);
        cardscreenshots = findViewById(R.id.card_screenshots);
        cardViewEntries = findViewById(R.id.card_view_entries);
        cardViewScreenshots = findViewById(R.id.card_view_screenshots);
        cardTitle = findViewById(R.id.title_journal);
        cardMsg = findViewById(R.id.msg_journal);


        totalEarnings = findViewById(R.id.total_earnings);
        totalEntries = findViewById(R.id.total_entries);
        totalScreenshots = findViewById(R.id.total_screenshots);
        studyCounter = findViewById(R.id.study_ctr);

        dashLogo = findViewById(R.id.dash_logo);

        layoutJournal = findViewById(R.id.journal);
        journalState = findViewById(R.id.journal_drawable);
        screenshotState = findViewById(R.id.card_screenshots_bonus);

        numSurveys = findViewById(R.id.num_surveys);
        numScreenshots = findViewById(R.id.num_screenshots);
        surveyHistory = findViewById(R.id.survey_history);
        screenshotHistory = findViewById(R.id.screenshot_history);

        swipeRefreshLayout = findViewById(R.id.main_swipe);

        info = findViewById(R.id.main_info);


        initUser();

        if (SP.getBoolean("virgin", true)) {

            int didSetPass = client.didSetPass();
            if (didSetPass == 0) {
                Intent i = new Intent(this, NewPassword.class);
                startActivityForResult(i, 10);
            } else {
                Intent i = new Intent(this, CreatePinUIActivity.class);
                startActivityForResult(i, 20);
            }

            SP.edit().putInt("hour", 14).apply();
            SP.edit().putInt("minute", 0).apply();

            try {
                client.startStudy(username);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SP.edit().putBoolean("virgin", false).apply();
        }

        try {
            String pass = client.finishedPost();
            if (!pass.equals("null")) {
                Intent post = new Intent(this, PostActivity.class);
                post.putExtra("data", pass);
                startActivity(post);
            }
        } catch (Exception e) {
            Log.i(TAG, CognitoSettings.formatException(e));
        }


        init(this);

        dashLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_rotate);
                dashLogo.startAnimation(rotate);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init(MainActivity.this);
            }
        });

        viewEarnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewEarnings.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, AllEarningsActivity.class);
                startActivityForResult(intent, 15);
            }
        });


        cardscreenshots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ScreenshotPromptActivity.class);
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
//        scrapeBtn.setOnClickListener(
//                view -> {
//                    try {
//                        Log.e(TAG, cognitoSettings.getToken(SP));
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                });


    }

    private void initUser() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.getUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        t.start();
        swipeRefreshLayout.setRefreshing(true);
    }

    private void init(Context context) {
        initUser();

        Thread t = new Thread(() -> {

            try {
                statuses = client.getStatuses();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            String daysLeft = client.getDaysLeft();

            String surveyCount = client.getTotalSurveyCount();
            String screenshotCount = client.getTotalScreenshotCount();


            String periodSurveyCount = Integer.toString(client.getPeriodSurveyCount());
            String periodScreenshotCount = Integer.toString(client.getPeriodThoughtCount());
            String periodSurveyBonusStatus = client.getPeriodSurveyBonusStatus();
            String periodThoughtBonusStatus = client.getPeriodThoughtBonusStatus();


            cardStatus = null;
            double amount = client.getTotalEarnings();

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    studyCounter.setText(daysLeft);
                    totalEarnings.setText(currencyFormat(amount));
                    totalEntries.setText(periodSurveyCount);
                    totalScreenshots.setText(periodScreenshotCount);

                    totalEntries.setCompoundDrawableTintList(ColorStateList.valueOf(getBonusColor(periodSurveyBonusStatus, context)));
                    totalScreenshots.setCompoundDrawableTintList(ColorStateList.valueOf(getBonusColor(periodThoughtBonusStatus, context)));
                    totalEntries.setTextColor(getBonusColor(periodSurveyBonusStatus, context));
                    totalScreenshots.setTextColor(getBonusColor(periodThoughtBonusStatus, context));

                    screenshotState.setImageTintList(ColorStateList.valueOf(getBonusColor(periodThoughtBonusStatus, context)));

                    numSurveys.setText(surveyCount);
                    numScreenshots.setText(screenshotCount);

                    if (Integer.parseInt(surveyCount) == 0) {
                        surveyHistory.setVisibility(View.GONE);
                        cardViewEntries.setOnClickListener(null);
                    } else {
                        surveyHistory.setVisibility(View.VISIBLE);
                        cardViewEntries.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cardViewEntries.setEnabled(false);
                                Intent intent = new Intent(MainActivity.this, JournalHistoryActivity.class);
                                startActivityForResult(intent, 15);
                            }
                        });
                    }

                    if (Integer.parseInt(screenshotCount) == 0) {
                        screenshotHistory.setVisibility(View.GONE);
                        cardViewScreenshots.setOnClickListener(null);
                    } else {
                        screenshotHistory.setVisibility(View.VISIBLE);
                        cardViewScreenshots.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cardViewScreenshots.setEnabled(false);
                                Intent intent = new Intent(MainActivity.this, ScreenshotHistoryActivity.class);
                                startActivityForResult(intent, 15);
                            }
                        });
                    }

                    statusLength = statuses.length();

                    try {
                        cardStatus = statuses.getString(statusLength - 1);
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }

                    try {
                        updateProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setCardColor();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });
        swipeRefreshLayout.setRefreshing(true);
        t.start();
    }

    public void updateProgress() throws Exception {

        int curDay = statuses.length();

        for (int i = 0; i < 5; i++) {
            String curStatus;

            if (i < statusLength)
                curStatus = statuses.getString(i);
            else
                curStatus = null;

            if (curStatus == null) {
                progressBar[i].setImageResource(R.drawable.light_gray_no_border);
            } else if (i == curDay - 1) {
                if (curStatus.equals("approved")) {
                    progressBar[i].setImageResource(R.drawable.green_border);
                } else if (curStatus.equals("rejected")) {
                    progressBar[i].setImageResource(R.drawable.red_border);

                } else if (curStatus.equals("submitted")) {
                    progressBar[i].setImageResource(R.drawable.blue_border);

                } else if (curStatus.equals("pending") || curStatus.equals("open")) {

                    progressBar[i].setImageResource(R.drawable.yellow_border);

                } else if (curStatus.equals("missed") || curStatus.equals("closed")) {
                    progressBar[i].setImageResource(R.drawable.gray_border);

                }
            } else if (curStatus.equals("approved")) {
                progressBar[i].setImageResource(R.drawable.green_no_border);


            } else if (curStatus.equals("rejected")) {
                progressBar[i].setImageResource(R.drawable.red_no_border);

            } else if (curStatus.equals("submitted")) {
                progressBar[i].setImageResource(R.drawable.blue_no_border);

            } else if (curStatus.equals("pending") || curStatus.equals("open")) {
                progressBar[i].setImageResource(R.drawable.yellow_no_border);

            } else if (curStatus.equals("missed") || curStatus.equals("closed")) {
                progressBar[i].setImageResource(R.drawable.gray_no_border);

            }

        }
    }

    public void setCardColor() {
        if (cardStatus == null || cardStatus.equals("closed") || cardStatus.equals("missed")) {
            cardTitle.setText("Come Back at 2 PM");
            cardMsg.setText("Daily Journal entry available soon");

            cardTitle.setTextColor(getResources().getColor(R.color.apparent));
            cardMsg.setTextColor(getResources().getColor(R.color.disabled));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_remove_circle_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.disabled));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.themeBackground));

            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

        } else if (cardStatus.equals("pending")) {
            String curID = client.getCurrentSurvey();

            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Finish Daily Journal Entry");
            cardMsg.setText("Complete by Midnight");

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_journal));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.neutral));

            layoutJournal.setBackground(getDrawable(R.drawable.ripple_effect_yellow));

            cardJournal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                    builder.setShowTitle(true);

                    CustomTabsIntent viewSurvey = builder.build();
                    String url = "http://ucf.qualtrics.com/jfe/form/SV_9z6wKsiRjfT6hJb?survey_id=" + curID;
                    Log.e(TAG, url);
                    viewSurvey.launchUrl(MainActivity.this, Uri.parse(url));
                }
            });

        } else if (cardStatus.equals("open")) {

            String curID = client.getCurrentSurvey();

            cardMsg.setClickable(true);
            cardMsg.setEnabled(true);
            cardTitle.setText("Start Daily Journal Entry");
            cardMsg.setText("Complete by Midnight");

            cardTitle.setTextColor(getResources().getColor(R.color.themeBackground));
            cardMsg.setTextColor(getResources().getColor(R.color.themeBackground));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_journal));
            journalState.setColorFilter(getResources().getColor(R.color.themeBackground));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.neutral));

            layoutJournal.setBackground(getDrawable(R.drawable.ripple_effect_yellow));

            cardJournal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primaryDark));
                    builder.setShowTitle(true);

                    String url = "http://ucf.qualtrics.com/jfe/form/SV_9z6wKsiRjfT6hJb?survey_id=" + curID;
                    Log.e(TAG, url);

                    CustomTabsIntent viewSurvey = builder.build();
                    viewSurvey.launchUrl(MainActivity.this, Uri.parse(url));

                    scraper.scrape();
                    SP.edit().putLong("total_screen_time", 0).apply();

                }
            });

        } else if (cardStatus.equals("submitted")) {
            cardTitle.setText("Daily Journal Complete");
            cardMsg.setText("Next entry available tomorrow");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.apparent));
            cardMsg.setTextColor(getResources().getColor(R.color.disabled));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_check_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.primaryDark));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.white));

        } else if (cardStatus.equals("approved")) {
            cardTitle.setText("Daily Journal Entry Apprvoed");
            cardMsg.setText("Next entry available tomorrow");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.apparent));
            cardMsg.setTextColor(getResources().getColor(R.color.disabled));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_check_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.positive));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.white));

        } else if (cardStatus.equals("rejected")) {
            cardTitle.setText("Daily Journal Entry Rejected");
            cardMsg.setText("Next entry available tomorrow");
            cardMsg.setClickable(false);
            cardMsg.setEnabled(false);

            cardTitle.setTextColor(getResources().getColor(R.color.apparent));
            cardMsg.setTextColor(getResources().getColor(R.color.disabled));

            journalState.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_close_black_20dp));
            journalState.setColorFilter(getResources().getColor(R.color.destructive));

            layoutJournal.setBackgroundColor(getResources().getColor(R.color.white));


        }
    }

    private int getBonusColor(String status, Context context) {
        if (status.equals("submitted")) {
            return context.getColor(R.color.primaryDark);
        } else if (status.equals("open")) {
            return context.getColor(R.color.neutral);
        } else if (status.equals("missed")) {
            return context.getColor(R.color.disabled);
        } else if (status.equals("approved")) {
            return context.getColor(R.color.positive);
        } else if (status.equals("closed")) {
            return context.getColor(R.color.disabled);
        }
        return 0;
    }

    public void setCardColorTran(ConstraintLayout layout, ColorDrawable start, ColorDrawable end) {
        ColorDrawable[] color = {start, end};
        TransitionDrawable trans = new TransitionDrawable(color);
        layout.setBackground(trans);
        trans.startTransition(1000);
    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.journaldev.AN_INTENT");
        intent.setComponent(new ComponentName(getPackageName(), "com.STIRlab.ema_diary.Helpers.NotifyPublisher"));
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
                Intent i = new Intent(this, CreatePinUIActivity.class);
                break;
            case 20:
                boolean hasPermissions = isAccessGranted(this);
                Intent o = new Intent(this, ManifestActivity.class);

                SP.edit().putBoolean("Remember", true).apply();

                if (!hasPermissions)
                    startActivityForResult(o, 30);
                break;
            case 30:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationHelper.setNotificationOreo(this);
                } else {
                    notificationHelper.setNotification(this);
                }
                break;
            case 50:
                break;
            case 15:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    public void onResume() {
        super.onResume();

        init(this);

        cardViewEntries.setEnabled(true);
        cardViewScreenshots.setEnabled(true);
        viewEarnings.setEnabled(true);

//        if(CognitoSettings.isLocked == -1)
//            CognitoSettings.isLocked *= -1;
//        else if (SP.getString("Pin", null) != null && CognitoSettings.isLocked == 1)
//            startActivity(new Intent(this, PinActivity.class));

    }

    private String currencyFormat(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(Locale.getDefault()));

        return format.format(amount);
    }

    private boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}
