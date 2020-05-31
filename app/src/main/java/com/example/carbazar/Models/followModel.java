package com.example.carbazar.Models;

public class followModel {
    private String userid;
    private String followId;

    public followModel() {
    }

    public followModel(String userid, String followId) {
        this.userid = userid;
        this.followId = followId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }
}
