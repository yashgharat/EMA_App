package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.Activities.Earnings.DateEarningsActivity;
import com.STIRlab.ema_diary.Activities.IndividualJournalEntryActivity;
import com.STIRlab.ema_diary.R;

import java.text.ParseException;
import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder> {

    private Context context;
    private List<JournalEntry> history;

    public JournalEntryAdapter(Context context, List<JournalEntry> history) {
        this.context = context;
        this.history = history;
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
            holder.isComplete.setTextColor(context.getColor(R.color.secondary));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.secondary));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (entry.getStatus().equals("rejected")) {
            holder.isComplete.setText("Rejected");
            holder.isComplete.setTextColor(context.getColor(R.color.destructive));
            holder.isComplete.setTextColor(context.getResources().getColor(R.color.themeBackground));

            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.destructive));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


        } else if (entry.getStatus().equals("submitted")) {
            holder.isComplete.setText("Waiting for approval");
            holder.isComplete.setTextColor(context.getColor(R.color.primaryDark));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.primary));
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

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IndividualJournalEntryActivity.class);
                intent.putExtra("entry", entry);
                context.startActivity(intent);
            }
        });

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
}

