package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.R;

import java.text.ParseException;
import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder> {

    private Context context;
    private List<JournalEntry> history;

    public JournalEntryAdapter(Context context, List<JournalEntry> history){
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
        if(entry.isComplete()) {
            holder.isComplete.setText("Complete");

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.secondary));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        else{
            holder.isComplete.setText("Missed");
            holder.isComplete.setTextColor(context.getResources().getColor(R.color.destructive));

            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.destructive));
            holder.isComplete.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);


            holder.earnings.setTextColor(context.getResources().getColor(R.color.normal));
        }
        holder.earnings.setText("$" + entry.gettotalEarnings());
        holder.increment.setText("+$" + entry.getIncrementEarnings());
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    class JournalEntryViewHolder extends RecyclerView.ViewHolder {

        TextView time, isComplete, earnings, increment;

        public JournalEntryViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.journal_time);
            isComplete= itemView.findViewById(R.id.is_complete);
            earnings = itemView.findViewById(R.id.card_earnings);
            increment = itemView.findViewById(R.id.card_increment);
        }
    }
}

