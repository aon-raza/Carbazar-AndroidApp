package com.example.carbazar.Models;

public class reportModel {
    private String user_id;
    private String Reported_user_id;
    private String username;
    private String text;

    public reportModel() {
    }

    public reportModel(String user_id, String reported_user_id, String username, String text) {
        this.user_id = user_id;
        Reported_user_id = reported_user_id;
        this.username = username;
        this.text = text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReported_user_id() {
        return Reported_user_id;
    }

    public void setReported_user_id(String reported_user_id) {
        Reported_user_id = reported_user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
