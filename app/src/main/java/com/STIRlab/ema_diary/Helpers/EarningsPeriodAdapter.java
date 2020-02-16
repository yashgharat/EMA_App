package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.STIRlab.ema_diary.Activities.Earnings.DateEarningsActivity;
import com.STIRlab.ema_diary.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class EarningsPeriodAdapter extends RecyclerView.Adapter<EarningsPeriodAdapter.EarningsPeriodViewHolder> {

    private Context context;
    private List<EarningsPeriod> earnings;

    public EarningsPeriodAdapter(Context context, List<EarningsPeriod> earnings) {
        this.context = context;
        this.earnings = earnings;
    }

    @NonNull
    @Override
    public EarningsPeriodAdapter.EarningsPeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.all_earnings_card, parent, false);
        return new EarningsPeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningsPeriodAdapter.EarningsPeriodViewHolder holder, int position) {
        EarningsPeriod item = earnings.get(position);

        try {
            holder.date.setText(item.getFormattedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.earningsSoFar.setText(currencyFormat(item.getEarnings()));
        if(item.getEarnings() == 0)
                holder.earningsSoFar.setTextColor(context.getColor(R.color.disabled));
        holder.earningsAdded.setText("+" + currencyFormat(item.getIncrement()));

        Log.i("InAdapter", item.getThoughtsBonus());

        if(item.getSurveysBonus().equals("open"))
        {
            holder.surveyCount.setText("");
            holder.surveyCount.setTextColor(context.getColor(R.color.neutral));
            holder.surveyCountIcon.setImageResource(0);
            holder.journal.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getThoughtsBonus().equals("open")){
            holder.thoughtCount.setText(item.getThoughts());
            holder.thoughtCount.setTextColor(context.getColor(R.color.neutral));
            holder.thoughtCountIcon.setImageResource(0);
            holder.upload.setColorFilter(context.getColor(R.color.neutral));
        }
        if(item.getSurveysBonus().equals("missed"))
        {
            holder.surveyCount.setText("");
            holder.surveyCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_black_20dp));
            holder.surveyCountIcon.setColorFilter(context.getResources().getColor(R.color.disabled));
            holder.journal.setColorFilter(context.getColor(R.color.disabled));
        }
        if(item.getThoughtsBonus().equals("missed")){
            holder.thoughtCount.setText("");
            holder.thoughtCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_black_20dp));
            holder.thoughtCountIcon.setColorFilter(context.getResources().getColor(R.color.disabled));
            holder.upload.setColorFilter(context.getColor(R.color.disabled));
        }
        if(item.getSurveysBonus().equals("approved"))
        {
            holder.surveyCount.setText("");
            holder.thoughtCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black_20dp));
            holder.thoughtCountIcon.setColorFilter(context.getResources().getColor(R.color.positive));
            holder.journal.setColorFilter(context.getColor(R.color.positive));
        }
        if(item.getThoughtsBonus().equals("approved")){
            holder.thoughtCount.setText("");
            holder.thoughtCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black_20dp));
            holder.thoughtCountIcon.setColorFilter(context.getResources().getColor(R.color.positive));
            holder.upload.setColorFilter(context.getColor(R.color.positive));
        }
        if(item.getSurveysBonus().equals("submitted"))
        {
            holder.surveyCount.setText("");
            holder.thoughtCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black_20dp));
            holder.thoughtCountIcon.setColorFilter(context.getResources().getColor(R.color.primaryDark));
            holder.journal.setColorFilter(context.getColor(R.color.primaryDark));
        }
        if(item.getThoughtsBonus().equals("submitted")){
            holder.thoughtCount.setText("");
            holder.thoughtCountIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black_20dp));
            holder.thoughtCountIcon.setColorFilter(context.getResources().getColor(R.color.primaryDark));
            holder.upload.setColorFilter(context.getColor(R.color.primaryDark));
        }



        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(context, DateEarningsActivity.class);
                    intent.putExtra("period", item);
                    context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return earnings.size();
    }

    class EarningsPeriodViewHolder extends RecyclerView.ViewHolder {

        TextView date, surveyCount, thoughtCount, earningsAdded;
        TextView earningsSoFar;
        ImageView journal, upload, surveyCountIcon, thoughtCountIcon;
        CardView card;

        public EarningsPeriodViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.card_earnings_title);
            surveyCount = itemView.findViewById(R.id.card_surveys_submit_count);
            thoughtCount = itemView.findViewById(R.id.card_screenshots_submit_count);

            earningsSoFar = itemView.findViewById(R.id.card_earnings);
            earningsAdded = itemView.findViewById(R.id.card_increment_earnings);

            surveyCountIcon = itemView.findViewById(R.id.card_surveys_submit_count_icon);
            thoughtCountIcon = itemView.findViewById(R.id.card_screenshots_submit_count_icon);

            journal = itemView.findViewById(R.id.card_journal_icon);
            upload = itemView.findViewById(R.id.card_upload_icon);

            card = itemView.findViewById(R.id.all_earnings_card);

        }
    }

    private String currencyFormat(double amount){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(Locale.getDefault()));

        return format.format(amount);
    }

}

