package com.example.todolist.Model;

public class CardListModel {
    String date;
    String taskKey;

    public CardListModel(String date, String taskKey) {
        this.date = date;
        this.taskKey = taskKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }
}
