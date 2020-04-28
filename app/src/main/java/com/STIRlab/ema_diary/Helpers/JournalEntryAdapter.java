package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.Activities.Earnings.DateEarningsActivity;
import com.STIRlab.ema_diary.Activities.IndividualJournalEntryActivity;
import com.STIRlab.ema_diary.Activities.MainActivity;
import com.STIRlab.ema_diary.R;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder> {

    private Context context;
    private List<JournalEntry> history;

    private SharedPreferences SP;
    private String username, email;

    private APIHelper client;

    public JournalEntryAdapter(Context context, List<JournalEntry> history) {
        this.context = context;
        this.history = history;

        SP = context.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);
        username = SP.getString("username", "null");
        email = SP.getString("email", "null");
    }

    @NonNull
    @Override
    public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.journal_history_entry, null);
        return new JournalEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalEntryViewHolder holder, int position) {
        JournalEntry entry = history.get(position);

        //binding the data with the viewholder views
        try {
            holder.time.setText(entry.getFormattedTime(context));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (entry.getStatus().equals("approved")) {
            holder.isComplete.setText("Approved");
            holder.isComplete.setTextColor(context.getColor(R.color.positive));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.positive));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (entry.getStatus().equals("rejected")) {
            holder.isComplete.setText("Rejected");
            holder.isComplete.setTextColor(context.getColor(R.color.destructive));
            holder.isComplete.setTextColor(context.getResources().getColor(R.color.destructive));

            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.destructive));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


        } else if (entry.getStatus().equals("submitted")) {
            holder.isComplete.setText("Waiting for approval");
            holder.isComplete.setTextColor(context.getColor(R.color.primaryDark));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.primaryDark));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


        } else if (entry.getStatus().equals("open") || entry.getStatus().equals("pending")) {
            holder.isComplete.setText("Ready to complete");
            holder.isComplete.setTextColor(context.getColor(R.color.neutral));

            Drawable drawable = context.getDrawable(R.drawable.ic_journal);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.neutral));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


        } else if (entry.getStatus().equals("missed")) {
            holder.isComplete.setText("Missed");
            holder.isComplete.setTextColor(context.getResources().getColor(R.color.disabled));

            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.disabled));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        }



        if (entry.getStatus().equals("open") || entry.getStatus().equals("pending")) {
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    client = new APIHelper(username, email, context);
                    client.makeCognitoSettings(context);
                    try {
                        client.getUserWithCallback(false, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                launchURL(client.getCurrentSurvey());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, IndividualJournalEntryActivity.class);
                    intent.putExtra("entry", entry);
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    class JournalEntryViewHolder extends RecyclerView.ViewHolder {

        TextView time, isComplete;
        CardView card;

        public JournalEntryViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.journal_time);
            isComplete = itemView.findViewById(R.id.is_complete);

            card = itemView.findViewById(R.id.journal_entry_card);
        }
    }

    private void launchURL(String curID) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.primaryDark));
        builder.setShowTitle(true);
        CustomTabsIntent viewSurvey = builder.build();
        String url = "http://ucf.qualtrics.com/jfe/form/SV_9z6wKsiRjfT6hJb?survey_id=" + curID;
        viewSurvey.launchUrl(context, Uri.parse(url));
    }
}

