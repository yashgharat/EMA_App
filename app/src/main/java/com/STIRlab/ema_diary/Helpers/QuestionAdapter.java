package com.STIRlab.ema_diary.Helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.STIRlab.ema_diary.R;

import org.w3c.dom.Text;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private static final String TAG = "QUESTION_ADAPTER";
    private Context context;
    private List<Question> questionList;

    private TextView previousAnswer = null;
    private ImageView previousArrow = null;

    public QuestionAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.faq_card, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question entry = questionList.get(position);

        holder.question.setText(entry.getQuestion());
        holder.answer.setText(entry.getAnswer());

        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(previousAnswer != null && !previousAnswer.equals(holder.answer)) {
                    previousAnswer.setVisibility(View.GONE);
                    previousArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }

                if (holder.answer.getVisibility() == View.GONE) {
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_up_blue_30dp);
                    holder.answer.setVisibility(View.VISIBLE);
                } else {
                    holder.iv.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    holder.answer.setVisibility(View.GONE);
                }

                previousAnswer = holder.answer;
                previousArrow = holder.iv;
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView question, answer;
        CardView frame;
        ImageView iv;

        public QuestionViewHolder(View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.faq_question);
            answer = itemView.findViewById(R.id.faq_answer);
            frame = itemView.findViewById(R.id.faq_frame);
            iv = itemView.findViewById(R.id.faq_arrow);

        }
    }
}
