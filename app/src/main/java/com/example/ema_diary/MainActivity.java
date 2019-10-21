package com.example.ema_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";

    private SharedPreferences SP;
    private Handler mHandler = new Handler();

    private TextView viewHistory_1;

    private CognitoSettings cognitoSettings = new CognitoSettings(this);

    private CognitoUserPool pool;
    private CognitoUserSession session;
    private RDS_Connect client = new RDS_Connect();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SP = this.getSharedPreferences("com.example.ema_diary", Context.MODE_PRIVATE);
        pool = cognitoSettings.getUserPool();

        if(SP.getBoolean("virgin", true)){

            Intent i = new Intent(this, newPassword.class);
            startActivityForResult(i, 10);


            //Log.i("VIRGIN: ", "HERE");
            SP.edit().putBoolean("virgin", false).apply();
        }
        setContentView(R.layout.activity_main);

        viewHistory_1 = findViewById(R.id.viewHistory_1);



        viewHistory_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cognitoSettings.refreshSession(SP);
                Log.i(TAG, "here");
                String username = SP.getString("username", "null");
                String email = SP.getString("email", "null");

                //Log.i(TAG, "Username and email: " + username +" " + email);

                try {
                    Log.i(TAG, client.doGetRequest(username, email));
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                }
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

        }
    }

    @Override
    public void onBackPressed() {

    }
}
