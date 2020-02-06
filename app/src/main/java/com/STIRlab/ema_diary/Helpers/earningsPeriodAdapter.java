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

import java.text.ParseException;
import java.util.List;

public class earningsPeriodAdapter extends RecyclerView.Adapter<earningsPeriodAdapter.EarningsPeriodViewHolder> {

    private Context context;
    private List<earningsPeriod> earnings;

    public earningsPeriodAdapter(Context context, List<earningsPeriod> earnings) {
        this.context = context;
        this.earnings = earnings;
    }

    @NonNull
    @Override
    public earningsPeriodAdapter.EarningsPeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.all_earnings_card, null);
        return new EarningsPeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull earningsPeriodAdapter.EarningsPeriodViewHolder holder, int position) {
        earningsPeriod item = earnings.get(position);

        try {
            holder.date.setText(item.getFormattedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.earningsSoFar.setAmount((float)item.getEarnings());
        if(item.getEarnings() == 0)
                holder.earningsSoFar.setBaseColor(context.getColor(R.color.disabled));
        holder.earningsAdded.setText("+$" + item.getIncrement());


        if(item.getSurveys_bonus().equals("open"))
        {
            holder.surveyCount.setText(item.getSurveys());
            holder.surveyCount.setTextColor(context.getColor(R.color.neutral));
            holder.upload.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getThoughts_bonus().equals("open")){
            holder.thoughtCount.setText(item.getThoughts());
            holder.thoughtCount.setTextColor(context.getColor(R.color.neutral));
            holder.journal.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getSurveys_bonus().equals("missed"))
        {
            holder.surveyCount.setText("");
            holder.surveyCount.setBackground(context.getDrawable(R.drawable.ic_close_black_20dp));
            holder.surveyCount.setBackgroundColor(context.getColor(R.color.disabled));
            holder.upload.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getThoughts_bonus().equals("missed")){
            holder.thoughtCount.setText("");
            holder.thoughtCount.setBackground(context.getDrawable(R.drawable.ic_close_black_20dp));
            holder.thoughtCount.setBackgroundColor(context.getColor(R.color.disabled));
            holder.journal.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getSurveys_bonus().equals("approved"))
        {
            holder.surveyCount.setText("");
            holder.surveyCount.setBackground(context.getDrawable(R.drawable.ic_check_black_24dp));
            holder.surveyCount.setBackgroundColor(context.getColor(R.color.positive));
            holder.upload.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getThoughts_bonus().equals("approved")){
            holder.thoughtCount.setText("");
            holder.thoughtCount.setBackground(context.getDrawable(R.drawable.ic_check_black_20dp));
            holder.thoughtCount.setBackgroundColor(context.getColor(R.color.positive));
            holder.journal.setColorFilter(context.getColor(R.color.neutral));
        }


        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return earnings.size();
    }

    class EarningsPeriodViewHolder extends RecyclerView.ViewHolder {

        TextView date, surveyCount, thoughtCount, earningsAdded;
        org.fabiomsr.moneytextview.MoneyTextView earningsSoFar;
        ImageView next, journal, upload;

        public EarningsPeriodViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.card_earnings_title);
            surveyCount = itemView.findViewById(R.id.card_surveys_submit_count);
            thoughtCount = itemView.findViewById(R.id.card_screenshots_submit_count);

            earningsSoFar = itemView.findViewById(R.id.card_earnings);
            earningsAdded = itemView.findViewById(R.id.card_increment_earnings);

            journal = itemView.findViewById(R.id.card_journal_icon);
            upload = itemView.findViewById(R.id.card_upload_icon);
            next = itemView.findViewById(R.id.card_earnings_next);

        }
    }

}

