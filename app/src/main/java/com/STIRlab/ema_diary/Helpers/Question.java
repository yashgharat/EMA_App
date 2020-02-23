package com.STIRlab.ema_diary.Helpers;

public class Question {

    private String question, answer;
    private boolean isCollapsed;

    public Question(String question, String answer){
        isCollapsed = true;
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
    }
}
