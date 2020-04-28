package com.STIRlab.ema_diary.Activities.Onboarding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.STIRlab.ema_diary.Activities.MainActivity;
import com.STIRlab.ema_diary.Helpers.LifeCycleHelper;
import com.STIRlab.ema_diary.Helpers.ScrapeDataHelper;
import com.STIRlab.ema_diary.R;

import de.mustafagercek.library.LoadingButton;

public class TutorialActivity extends AppCompatActivity {
    private final String TAG = "TUTORIAL";
    private final int PHONESTATE = 69;

    private LoadingButton btnScrape;

    private ScrapeDataHelper dataHelper;

    private View.OnClickListener ok, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        dataHelper = new ScrapeDataHelper(this);

        btnScrape = findViewById(R.id.button_scrape);

        LifeCycleHelper.flag = false;

        btnScrape.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ok = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 10);
            }
        };

        next = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnScrape.setButtonColor(getColor(R.color.disabled));
                btnScrape.setTextColor(getColor(R.color.apparent));
                btnScrape.onStartLoading();

                dataHelper.scrape();
                btnScrape.setClickable(false);
                long delayInMillis = 3000;
                LifeCycleHelper.flag = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnScrape.onStopLoading();
                        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                    }
                }, delayInMillis);
            }
        };

        btnScrape.setButtonOnClickListener(ok);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                LifeCycleHelper.flag = false;
                if(isAccessGranted(this)) {
                    btnScrape.setButtonText("Next");
                    btnScrape.setButtonOnClickListener(next);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PHONESTATE);
                    int i = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                }

                break;
        }
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