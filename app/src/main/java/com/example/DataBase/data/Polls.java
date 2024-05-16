package com.example.DataBase.data;

public class Polls {
    public int userType;
    int id;
    int userId;
    String question;

    public Polls(int id, int userId, int userType, String question) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
