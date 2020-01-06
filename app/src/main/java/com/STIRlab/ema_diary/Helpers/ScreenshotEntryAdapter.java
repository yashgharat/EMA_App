package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.R;

import java.text.ParseException;
import java.util.List;

public class ScreenshotEntryAdapter extends RecyclerView.Adapter<ScreenshotEntryAdapter.ThoughtEntryViewHolder> {


    private Context context;
    private List<ScreenshotEntry> history;

    public ScreenshotEntryAdapter(Context context, List<ScreenshotEntry> history){
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
        ScreenshotEntry entry = history.get(position);

        //binding the data with the viewholder views
        try {
            holder.time.setText(entry.getFormattedTime(context));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(entry.getScreenshotCount() > 0) {
            holder.screenshotLabel.setText("Includes screenshot");
            holder.screenshotLabel.setTextColor(context.getColor(R.color.secondary));
        }
        else
        {
            holder.screenshotLabel.setText("No screenshot");
            holder.screenshotLabel.setTextColor(context.getColor(R.color.apparent));
        }
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    class ThoughtEntryViewHolder extends RecyclerView.ViewHolder {

        TextView time, screenshotCount, screenshotLabel;

        public ThoughtEntryViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.thoughts_time);
            screenshotLabel = itemView.findViewById(R.id.screenshot_label);
        }
    }
}
