package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.STIRlab.ema_diary.R;

public class ForgotPasswordActivity extends AppCompatActivity {


    private EditText pass;
    private EditText code;
    private Button btnReset;

    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        pass = findViewById(R.id.forgotPass);
        code = findViewById(R.id.veriCode);
        btnReset = findViewById(R.id.btnReset);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("destination")) {
                String dest = extras.getString("destination");
                String delMed = extras.getString("deliveryMed");
                showDialogMessage("Attention", "Code to set a new password was sent to " + dest + " via " + delMed, false);
            }
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass = pass.getText().toString();
                String verCode = code.getText().toString();

                if(checkPass(newPass)) {
                    exit(newPass, verCode);
                }
            }
        });


    }

    private void exit(String newPass, String code) {
        Intent intent = new Intent();
        if (newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
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
    }

    private boolean checkPass(String password){
        if(password.equals(password.toLowerCase())){
            //showDialogMessage("Error", "Must have an uppercase Character", false);
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, "Must have an uppercase Character", Toast.LENGTH_LONG);
            toast.show();

            return false;
        }
        if(password.length() < 6){
            //showDialogMessage("Error", "Must be at least 6 characters", false);
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, "Must be at least 6 characters", Toast.LENGTH_LONG);
            toast.show();


            return false;
        }
        if (!password.matches(".*\\d.*")){
            //showDialogMessage("Error", "Must contain a numeric character", false);
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, "Must contain a numeric character", Toast.LENGTH_LONG);
            toast.show();


            return false;
        }
        return true;
    }
}
