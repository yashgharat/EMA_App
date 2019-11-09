package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.R;

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
        holder.time.setText(entry.getFormattedTime());
        holder.isComplete.setText(String.valueOf(entry.isComplete()));
        holder.earnings.setText(String.valueOf(entry.gettotalEarnings()));
        holder.increment.setText(String.valueOf(entry.getIncrementEarnings()));
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

