package com.example.ema_diary;

// Taken from MarshmallowProject pamwis

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class CollectingPermissions extends AppCompatActivity {

    private Button next2;

    //Variable declarations
    String buildNumber;
    String version;
    int appSize = 0;
    Context context;

    //App object that contains all the name and permissions about each app
    static class app{
        String Appname;
        String location;
        String storage;
        String sms;
        String camera;
        String sensor;
        String phone;
        String microphone;
        String calendar;
        String contacts;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collecting_permissions);

        //Declaring an array of app objects
        final app[] appArray = new app[100];
        for (int i = 0; i < appArray.length; i++) {
            appArray[i] = new app();
        }

        next2 = (Button) findViewById(R.id.btnNext);

        final PackageManager packageManager = getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //Making a list of all the apps
        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);

        //Retrieve the build number for the phone
        buildNumber = Build.MODEL;

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        //Hashing the device Id to create a unique Id for the device
        //The permission phone state has to be accepted in order to get the unique ID
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        //Store the hashed string into deviceId
        String deviceId = deviceUuid.toString();
        int j=0;
        //Looping through all the apps to retrieve info
        for (ResolveInfo info : apps) {
            appSize = apps.size();

            final ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;
            final String appName = (String) applicationInfo.loadLabel(packageManager);

            if (appName != null) {
                try {
                    appArray[j].Appname = appName; //Retrieving the app name

                    final PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                    final String[] requestedPermissions = packageInfo.requestedPermissions;
                    version = packageInfo.versionName; //Retrieving the device version number


                    //Looping through all the permissions for each app
                    if (packageInfo.permissions != null) {
                        // For each defined permission
                        for (int i = 0, len = requestedPermissions.length; i < len; i++) {

                            //Variable to store each permission one at a time
                            String requestedPerm = requestedPermissions[i];
                            boolean granted = (packageInfo.requestedPermissionsFlags[i] & packageInfo.REQUESTED_PERMISSION_GRANTED) != 0;

                            //Variable to check if permission was granted or denied
                            String check;
                            if (granted == true)
                                check = "Granted";
                            else
                                check = "Denied";

                            //Filters out the dangerous permissions only by checking for the key words
                            if (requestedPerm.contains("STORAGE")) {
                                appArray[j].storage = check;
                            }

                            if (requestedPerm.contains("CAMERA")) {
                                appArray[j].camera = check;
                            }

                            if (requestedPerm.contains("CONTACTS")) {
                                appArray[j].contacts = check;
                            }

                            if (requestedPerm.contains("LOCATION")) {
                                appArray[j].location = check;
                            }

                            if (requestedPerm.contains("MICROPHONE")) {
                                appArray[j].microphone = check;
                            }

                            if (requestedPerm.contains("PHONE")) {
                                appArray[j].phone = check;
                            }

                            if (requestedPerm.contains("SENSORS")) {
                                appArray[j].sensor = check;
                            }

                            if (requestedPerm.contains("CALENDAR")) {
                                appArray[j].calendar = check;
                            }

                            if (requestedPerm.contains("SMS")) {
                                appArray[j].sms = check;
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            j++;
        }

        //The progress bar that shows that the data is being collected
        final ProgressDialog dialog = new ProgressDialog(CollectingPermissions.this);
        dialog.setTitle("Collecting Data...");
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Timer for the progress bar
        long delayInMillis = 3000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, delayInMillis);

        for(int m = 0; m<appSize; m++)
        {
            //Setting the string variable 'choice' to 'permissions' so that it would store the
            // collected permissions in the permission table in the database
            String choice = "permissions";
            String appname = appArray[m].Appname;
            String calendar = appArray[m].calendar;
            String microphone = appArray[m].microphone;
            String storage = appArray[m].storage;
            String location = appArray[m].location;
            String sms = appArray[m].sms;
            String camera = appArray[m].camera;
            String sensor = appArray[m].sensor;
            String phone = appArray[m].phone;
            String contacts = appArray[m].contacts;

            //Some of the apps don't have all of the permissions
            //If an app doesn't have a permission then it's null
            //This is to check if the permission is null or not
            //If it's not null then it's assigned to it's given value

            if (calendar == null)
                calendar = "null";
            else
                calendar = appArray[m].calendar;

            if (contacts == null)
                contacts = "null";
            else contacts = appArray[m].contacts;

            if (camera == null)
                camera = "null";
            else camera = appArray[m].camera;


            if (microphone == null)
                microphone = "null";
            else
                microphone = appArray[m].microphone;

            if (storage == null)
                storage = "null";
            else
                storage = appArray[m].storage;

            if (sms == null)
                sms = "null";
            else sms = appArray[m].sms;

            if (sensor == null)
                sensor = "null";
            else sensor = appArray[m].sensor;

            if (location == null)
                location = "null";
            else location = appArray[m].location;

            if (phone == null)
                phone = "null";
            else phone = appArray[m].phone;


            //This is to make sure no app is duplicated in the database
            //Calling a function to save the collected permissions in a database
            BackgroundWorker backgroundWorker = new BackgroundWorker(context);
            backgroundWorker.execute(choice, String.valueOf(m), appname, calendar, microphone, storage, camera, contacts, sms, sensor, phone, location, deviceId);
            //Adding the app name to an array
        }
        //OnClickListener to direct the user to the next activity
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent main = new Intent(CollectingPermissions.this, MainActivity.class);
                startActivity(main);
            }
        });

    }
}