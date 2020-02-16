package com.STIRlab.ema_diary.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.STIRlab.ema_diary.Helpers.CognitoSettings;
import com.STIRlab.ema_diary.Helpers.APIHelper;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import de.mustafagercek.library.LoadingButton;

public class ScreenshotActivity extends AppCompatActivity {

    final int THUMBSIZE = 128;
    private final String TAG = "screenshots";
    private LoadingButton submit;

    private FloatingActionButton ret;

    private TextView replaceScreenshot;
    private ImageView thumbnail;

    private EditText inputInteraction;

    private APIHelper client;

    private AlertDialog userDialog;
    private Bitmap bitmap, thumbImage;
    private Uri image;

    private Thread thread;

    private SharedPreferences SP;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots);
        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        String username = SP.getString("username", "null");
        String email = SP.getString("email", "null");

        client = new APIHelper(username, email);

        ret = findViewById(R.id.screenshots_previous);
        inputInteraction = findViewById(R.id.screenshots_upload);
        submit = findViewById(R.id.button_screenshots);
        thumbnail = findViewById(R.id.thumb_view);
        replaceScreenshot = findViewById(R.id.replace_screenshot);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        image = Uri.parse(getIntent().getStringExtra("imagePath"));

        submit.setEnabled(false);
        submit.setButtonColor(getColor(R.color.disabled));
        submit.setTextColor(getColor(R.color.apparent));
        
        init();

        replaceScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ScreenshotActivity.this);
            }
        });

        inputInteraction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    submit.setEnabled(false);
                    submit.setButtonColor(getColor(R.color.disabled));
                    submit.setTextColor(getColor(R.color.apparent));
                } else {
                    submit.setEnabled(true);
                    submit.setButtonColor(getColor(R.color.primaryDark));
                    submit.setTextColor(getColor(R.color.themeBackground));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submit.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uploadText = inputInteraction.getText().toString();
                String userid = SP.getString("username", "null");

                if (bitmap != null) {

                    submit(uploadText);

                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    inputInteraction.setText("");
                    thumbnail.setImageBitmap(null);
                    finish();
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

    private void submit(String uploadText) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File newFile = codec(bitmap, Bitmap.CompressFormat.PNG, 50, ScreenshotActivity.this);

                    client.uploadInteractionWithPicture(uploadText, "", newFile);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                ScreenshotActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        submit.onStopLoading();
                        submit.setButtonColor(getColor(R.color.primaryDark));
                        submit.setTextColor(getColor(R.color.themeBackground));
                        Toast.makeText(ScreenshotActivity.this, "Submission successful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        submit.setButtonColor(getColor(R.color.disabled));
        submit.setTextColor(getColor(R.color.apparent));
        submit.onStartLoading();
        thread.start();
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
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    image = resultUri;
                    init();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.i(TAG, error.toString());
                }
                break;
        }
    }

    private void init() {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
        } catch (FileNotFoundException e) {
            showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
        } catch (IOException e) {
            showDialogMessage("EXCEPTION", CognitoSettings.formatException(e), false);
        }

        thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
        thumbnail.setImageBitmap(thumbImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SP.getString("Pin", null) != null)
            startActivity(new Intent(this, PinActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (inputInteraction.getText().length() > 0) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Discard Screenshot?")
                    .setCancelable(false)
                    .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("Keep Editing", null)
                    .show();
        }
    }

}
