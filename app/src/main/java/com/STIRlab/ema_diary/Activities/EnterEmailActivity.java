package com.STIRlab.ema_diary.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.regex.Pattern;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class EnterEmailActivity extends AppCompatActivity {

    private CircularProgressButton sendEmail;
    private FloatingActionButton previous;
    private EditText editTextEmail;

    private ForgotPasswordContinuation forgotPasswordContinuation;
    private AlertDialog userDialog;

    private CognitoSettings cognitoSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_email);
        cognitoSettings = new CognitoSettings(this);

        editTextEmail = findViewById(R.id.reset_email);
        sendEmail = findViewById(R.id.button_send_email);

        previous = findViewById(R.id.enter_email_previous);

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextEmail.getText().toString();

                if (username.length() > 0 && isValid(username)) {
                    cognitoSettings.getUserPool().getUser(username).forgotPasswordInBackground(new ForgotPasswordHandler() {
                        @Override
                        public void onSuccess() {
                            showDialogMessage("Success", "Password successfully changed!", true);
                        }

                        @Override
                        public void getResetCode(ForgotPasswordContinuation continuation) {
                            getForgotPasswordCode(continuation);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            showDialogMessage("Password reset failed", CognitoSettings.formatException(exception), false);

                        }
                    });
                }
                else{
                    showDialogMessage("Error", "Enter valid email", false);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && resultCode == RESULT_OK){
            String newPass = data.getStringExtra("newPass");
            String code = data.getStringExtra("code");
            if (newPass != null && code != null) {
                if (!newPass.isEmpty() && !code.isEmpty()) {
                    forgotPasswordContinuation.setPassword(newPass);
                    forgotPasswordContinuation.setVerificationCode(code);
                    forgotPasswordContinuation.continueTask();
                }
            }
        }
    }

    private static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void getForgotPasswordCode(ForgotPasswordContinuation continuation) {
        this.forgotPasswordContinuation = continuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination", forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
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
        userDialog.show();

    }
}
