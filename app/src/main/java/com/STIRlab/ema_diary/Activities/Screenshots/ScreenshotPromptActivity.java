package com.STIRlab.ema_diary.Activities.Screenshots;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;


public class ScreenshotPromptActivity extends AppCompatActivity {

    private static final String TAG = "SCREENSHOT_PROMPT";
    private static final int REQUEST_IMAGE_OPEN = 10;

    private AlertDialog userDialog;
    private Button addPic;
    public static Bitmap bitmap, thumbImage;

    private FloatingActionButton prev;

    private SharedPreferences SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_prompt);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        addPic = findViewById(R.id.add_pic_button);

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Application"), REQUEST_IMAGE_OPEN);

            }
        });

        prev = findViewById(R.id.choose_screenshot_cancel);
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

        switch (requestCode) {

            case REQUEST_IMAGE_OPEN:
                Uri resultUri;
                if(data != null) {
                    resultUri = data.getData();
                    CropImage.activity(resultUri)
                            .setCropMenuCropButtonTitle("Next")
                            .start(this);
                }
                else {
                    finish();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = result.getUri();
                    Intent intent = new Intent(ScreenshotPromptActivity.this, ScreenshotActivity.class);
                    intent.putExtra("imagePath", resultUri.toString());
                    finish();
                    startActivity(intent);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.i(TAG, error.toString());
                }
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }

}
