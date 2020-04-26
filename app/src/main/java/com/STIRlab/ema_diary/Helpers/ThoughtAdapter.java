package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.Activities.IndividualJournalEntryActivity;
import com.STIRlab.ema_diary.Activities.Screenshots.IndividualScreenshotActivity;
import com.STIRlab.ema_diary.R;

import java.text.ParseException;
import java.util.List;

public class ThoughtAdapter extends RecyclerView.Adapter<ThoughtAdapter.thoughtViewHolder> {


    private static final String TAG = "THOUGHT_ADAPTER";
    private Context context;
    private List<Thought> history;

    public ThoughtAdapter(Context context, List<Thought> history){
        this.context = context;
        this.history = history;
    }

    @NonNull
    @Override
    public thoughtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.thought_history_entry, parent, false);
        return new thoughtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull thoughtViewHolder holder, int position) {
        Thought entry = history.get(position);

        //binding the data with the viewholder views
        try {
            holder.time.setText(entry.getFormattedTime(context));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(entry.getStatus().equals("submitted")){
            Log.i(TAG, "in submitted");
            holder.thoughtLabel.setText("Waiting for approval");
            holder.thoughtLabel.setTextColor(context.getColor(R.color.primary));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.primary));
            holder.thoughtLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        else if(entry.getStatus().equals("rejected")) {
            holder.thoughtLabel.setText("Rejected");
            holder.thoughtLabel.setTextColor(context.getColor(R.color.destructive));

            Drawable drawable = context.getDrawable(R.drawable.ic_close_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.destructive));
            holder.thoughtLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

        else if(entry.getStatus().equals("approved")) {
            holder.thoughtLabel.setText("Approved");
            holder.thoughtLabel.setTextColor(context.getColor(R.color.positive));

            Drawable drawable = context.getDrawable(R.drawable.ic_check_black_20dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getColor(R.color.positive));
            holder.thoughtLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, IndividualScreenshotActivity.class);
                intent.putExtra("entry", entry);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    class thoughtViewHolder extends RecyclerView.ViewHolder {

        TextView time, thoughtLabel;
        CardView card;

        public thoughtViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.screenshots_time);
            thoughtLabel = itemView.findViewById(R.id.screenshot_label);

            card = itemView.findViewById(R.id.screenshot_entry_card);
        }
    }
}
