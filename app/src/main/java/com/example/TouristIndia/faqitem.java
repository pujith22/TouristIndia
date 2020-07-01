package com.example.TouristIndia;

public class faqitem {

    private String question;
    private String answer;

    public faqitem(String ques, String ans) {
        this.question = ques;
        this.answer = ans;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
