package com.STIRlab.ema_diary.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.STIRlab.ema_diary.Helpers.Question;
import com.STIRlab.ema_diary.Helpers.QuestionAdapter;
import com.STIRlab.ema_diary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private CardView cardHotline;

    private FloatingActionButton ret;

    private QuestionAdapter adapter;
    private RecyclerView recyclerView;
    private List<Question> questionList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        cardHotline = findViewById(R.id.cardHotline);

        ret = findViewById(R.id.helpPrevious);

        recyclerView = findViewById(R.id.faq_recycler);

        init();

        cardHotline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18006564673"));
                startActivity(intent);
            }
        });

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void init() {
        questionList = getQuestions();
        adapter = new QuestionAdapter(this, questionList);
        recyclerView.setAdapter(adapter);

    }

    private ArrayList<Question> getQuestions() {
        ArrayList<Question> list = new ArrayList<Question>();

        String questions[] = HelpActivity.this.getResources().getStringArray(R.array.question_array);
        String answers[] = HelpActivity.this.getResources().getStringArray(R.array.ans_array);

        for (int i = 0; i < questions.length; i++) {
            list.add(new Question(questions[i], answers[i]));
        }
        return list;
    }
}
