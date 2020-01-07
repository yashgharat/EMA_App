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

public class ScreenshotEntryAdapter extends RecyclerView.Adapter<ScreenshotEntryAdapter.screenshotEntryViewHolder> {


    private Context context;
    private List<ScreenshotEntry> history;

    public ScreenshotEntryAdapter(Context context, List<ScreenshotEntry> history){
        this.context = context;
        this.history = history;
    }

    @NonNull
    @Override
    public screenshotEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.screenshots_history_entry, null);
        return new screenshotEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull screenshotEntryViewHolder holder, int position) {
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

    class screenshotEntryViewHolder extends RecyclerView.ViewHolder {

        TextView time, screenshotCount, screenshotLabel;

        public screenshotEntryViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.screenshots_time);
            screenshotLabel = itemView.findViewById(R.id.screenshot_label);
        }
    }
}
