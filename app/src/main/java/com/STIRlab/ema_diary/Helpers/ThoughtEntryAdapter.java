package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.R;

import java.util.List;

public class ThoughtEntryAdapter extends RecyclerView.Adapter<ThoughtEntryAdapter.ThoughtEntryViewHolder> {


    private Context context;
    private List<ThoughtEntry> history;

    public ThoughtEntryAdapter(Context context, List<ThoughtEntry> history){
        this.context = context;
        this.history = history;
    }

    @NonNull
    @Override
    public ThoughtEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.thoughts_history_entry, null);
        return new ThoughtEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThoughtEntryViewHolder holder, int position) {
        ThoughtEntry entry = history.get(position);

        //binding the data with the viewholder views
        holder.time.setText(entry.getFormattedTime());
        holder.screenshotCount.setText(String.valueOf(entry.getScreenshotCount()));
        holder.screenshotLabel.setText(String.valueOf(entry.getScreenshotLabel()));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ThoughtEntryViewHolder extends RecyclerView.ViewHolder {

        TextView time, screenshotCount, screenshotLabel;

        public ThoughtEntryViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.thoughts_time);
            screenshotCount= itemView.findViewById(R.id.screenshot_count);
            screenshotLabel = itemView.findViewById(R.id.screenshot_label);
        }
    }
}
