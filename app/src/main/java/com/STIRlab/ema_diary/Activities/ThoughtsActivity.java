package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ThoughtsActivity extends AppCompatActivity {

    private final String TAG = "THOUGHTS";
    final int THUMBSIZE = 128;

    private FloatingActionButton ret;
    private Button submit;

    private TextView addPic;

    private EditText inputInteraction;

    private AlertDialog userDialog;
    private Bitmap bitmap, thumbImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts);

        ret = findViewById(R.id.thoughtsPrevious);
        addPic = findViewById(R.id.screenshotLink);
        inputInteraction = findViewById(R.id.thoughts_upload);
        submit = findViewById(R.id.btnThoughts);

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),2);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uploadText = inputInteraction.getText().toString();
                if(uploadText.length() > 0)
                {
                    finish();
                }
                else
                {
                    showDialogMessage("Error", "Please input some text", false);
                }
            }
        });


        ret.setOnClickListener(new View.OnClickListener() {
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
                        // TODO Auto-generated catch block
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    }
                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TAG, "Selecting picture cancelled");
                }
                break;
        }
    }

    private class extractThumbIcon extends AsyncTask<Bitmap, Integer, Void>{

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            int count = bitmaps.length;
            for (int i = 0; i < count; i++) {
                thumbImage = ThumbnailUtils.extractThumbnail(bitmaps[i], 30 , 30);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }
    }
}
