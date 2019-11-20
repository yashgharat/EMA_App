package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.RDS_Connect;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ThoughtsActivity extends AppCompatActivity {

    private final String TAG = "THOUGHTS";
    final int THUMBSIZE = 128;

    private FloatingActionButton ret;
    private CircularProgressButton submit;

    private TextView addPic;
    private ImageView thumbnail;

    private EditText inputInteraction;
    private EditText caption;

    private RDS_Connect client;

    private AlertDialog userDialog;
    private Bitmap bitmap, thumbImage;

    private SharedPreferences SP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);

        client = new RDS_Connect();

        ret = findViewById(R.id.thoughtsPrevious);
        addPic = findViewById(R.id.screenshotLink);
        inputInteraction = findViewById(R.id.thoughts_upload);
        submit = findViewById(R.id.btnThoughts);
        thumbnail = findViewById(R.id.thumbView);
        caption = findViewById(R.id.caption);

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
                submit.startMorphAnimation();
                String uploadText = inputInteraction.getText().toString();
                if(uploadText.length() > 0)
                {
                    String userid = SP.getString("username", "null");

                    if(bitmap != null) {
                        try {
                            File newFile = codec(bitmap, Bitmap.CompressFormat.PNG, 50, ThoughtsActivity.this);

                            client.uploadInteractionWithPicture(userid, uploadText, "", newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Log.i(TAG, "No screenshot");
                        try {
                            client.uploadInteraction(userid, uploadText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    showDialogMessage("Error", "Please input some text", false);
                }
                inputInteraction.setText("");
                thumbnail.setImageBitmap(null);
                caption.setText("");
                caption.setVisibility(View.GONE);
                submit.startMorphRevertAnimation();
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
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    } catch (IOException e) {
                        showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
                    }
                    thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 200 , 200);
                    thumbnail.setImageBitmap(thumbImage);
                    caption.setVisibility(View.VISIBLE);
                    addPic.setText("Upload Different Screenshot");
                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TAG, "Selecting picture cancelled");
                }
                break;
        }
    }

    private static File codec(Bitmap src, Bitmap.CompressFormat format,
                              int quality, Context context) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        File f = new File(context.getCacheDir(), "img");

        src.compress(format, quality, os);

        byte[] array = os.toByteArray();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(array);
        fos.flush();
        fos.close();

        return f;
    }

}
