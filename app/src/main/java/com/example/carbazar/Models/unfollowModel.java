package com.example.carbazar.Models;

public class unfollowModel {
    private String userid;
    private String UnfollowId;

    public unfollowModel() {
    }

    public unfollowModel(String userid, String unfollowId) {
        this.userid = userid;
        UnfollowId = unfollowId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUnfollowId() {
        return UnfollowId;
    }

    public void setUnfollowId(String unfollowId) {
        UnfollowId = unfollowId;
    }
}
