package com.example.todolist.Model;

public class CardSubListModel {

    String task, operation, taskKey, subTasksListkey;

    public CardSubListModel(String task, String operation, String taskKey, String subTasksListkey) {
        this.task = task;
        this.operation = operation;
        this.taskKey = taskKey;
        this.subTasksListkey = subTasksListkey;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getSubTasksListkey() {
        return subTasksListkey;
    }

    public void setSubTasksListkey(String subTasksListkey) {
        this.subTasksListkey = subTasksListkey;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
