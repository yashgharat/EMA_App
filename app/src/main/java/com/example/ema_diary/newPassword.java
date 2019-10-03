package com.example.ema_diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class newPassword extends AppCompatActivity {

    private CognitoSettings cognitoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        cognitoSettings = new CognitoSettings(newPassword.this);
        Bundle extras = getIntent().getExtras();
        String oldPass = extras.getString("oldPass");

        Button btnSet = findViewById(R.id.btnSetPass);

        EditText txtPassUno = findViewById(R.id.newPassUno);
        EditText txtPassDos = findViewById(R.id.newPassDos);


        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPassDos.getText().toString().equals(txtPassUno.getText().toString())){
                    cognitoSettings.getUser().changePasswordInBackground(oldPass, txtPassDos.getText().toString(), new GenericHandler() {
                        @Override
                        public void onSuccess() {
                            Intent i = new Intent(newPassword.this, createPinActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }
                    });
                }
            }
        });


    }
}
