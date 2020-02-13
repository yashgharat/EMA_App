package com.STIRlab.ema_diary.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        SP = this.getSharedPreferences("com.STIRlab.ema_diary", Context.MODE_PRIVATE);


        cardHotline = findViewById(R.id.card_hotline);

        ret = findViewById(R.id.help_previous);

        recyclerView = findViewById(R.id.faq_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
        }});

//        swipeRefreshLayout = findViewById(R.id.faqSwipe);

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

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                init();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    private void init() {
        questionList = getQuestions();
        adapter = new QuestionAdapter(HelpActivity.this, questionList);
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

    @Override
    public void onResume(){
        super.onResume();
        if(SP.getString("Pin", null) != null)
            startActivity(new Intent(this, PinActivity.class));

    }
}
