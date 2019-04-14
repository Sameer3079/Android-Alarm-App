package lk.sliit.androidalarmsystem.domain;

import java.util.HashMap;

public class Question {

    private long id;
    private String question;
    private HashMap<Long, String> choices;
    private long answer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public HashMap<Long, String> getChoices() {
        return choices;
    }

    public void setChoices(HashMap<Long, String> choices) {
        this.choices = choices;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(long answer) {
        this.answer = answer;
    }
}
