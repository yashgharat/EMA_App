package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;

import java.io.FileNotFoundException;
import java.io.IOException;


public class screenshotPromptActivity extends AppCompatActivity {

    private static final String TAG = "SCREENSHOT_PROMPT";

    private AlertDialog userDialog;
    private Button addPic;
    private Bitmap bitmap;

    private TextView prev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_prompt);

        addPic = findViewById(R.id.add_pic_button);

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),2);
            }
        });

        prev = findViewById(R.id.chooseScreenshotCancel);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (FileNotFoundException e) {
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    } catch (IOException e) {
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    }
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);
                    Intent uploadSS = new Intent(screenshotPromptActivity.this, ScreenshotActivity.class);
                    uploadSS.putExtra("thumbimage", thumbImage);
                    uploadSS.putExtra("bitmap", bitmap);

                    startActivity(uploadSS);
                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TAG, "Selecting picture cancelled");
                }
                break;
        }
    }

}
