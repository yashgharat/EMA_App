package com.STIRlab.ema_diary.Activities.Screenshots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.STIRlab.ema_diary.Helpers.JournalEntry;
import com.STIRlab.ema_diary.Helpers.Thought;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;

public class IndividualScreenshotActivity extends AppCompatActivity {

    private TextView submitTimeText, decisionText, decisionTimeText, researcherMessageText;
    private FloatingActionButton previous;

    private Thought entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_screenshot);
        entry = (Thought) getIntent().getSerializableExtra("entry");

        submitTimeText = findViewById(R.id.individual_screenshot_sub);
        decisionText = findViewById(R.id.individual_screenshot_decision);
        decisionTimeText = findViewById(R.id.individual_screenshot_decision_date);
        researcherMessageText = findViewById(R.id.individual_screenshot_message);
        previous = findViewById(R.id.individual_screenshot_previous);

        previous.setOnClickListener(v -> finish());

        try {
            init(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init(Context context) throws ParseException {
        submitTimeText.setText(String.format("Submitted %s", entry.getFormattedTime(context)));

        if (entry.getStatus().equals("submitted")) {
            decisionText.setText("Waiting for Approval");
            decisionText.setTextColor(getColor(R.color.primaryDark));
            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.primaryDark));
            decisionText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            decisionTimeText.setVisibility(View.GONE);
        } else if (entry.getStatus().equals("rejected")) {
            decisionText.setText("Rejected");
            decisionText.setTextColor(getColor(R.color.destructive));
            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.destructive));
            decisionText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            decisionTimeText.setText(entry.getFormattedDecisionTime(context));
        } else if (entry.getStatus().equals("approved")) {
            decisionText.setText("Approved");
            decisionText.setTextColor(getColor(R.color.positive));
            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.positive));
            decisionText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            decisionTimeText.setText(entry.getFormattedDecisionTime(context));
        }

        String message = entry.getResearcherMessage();

        if (entry.getStatus().equals("submitted")) {
            researcherMessageText.setText(String.format("%s %s", getString(R.string.default_message), getString(R.string.unapproved_message_addendum)));
        } else if (message != null && !message.equals("null")) {
            researcherMessageText.setText(entry.getResearcherMessage());
        }
    }
}
