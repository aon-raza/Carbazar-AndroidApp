package com.example.carbazar.Models;

public class chatMessages {
    private String userMsg;
    private String chatbotMsg;
    private String image;

    public chatMessages() {
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getChatbotMsg() {
        return chatbotMsg;
    }

    public void setChatbotMsg(String chatbotMsg) {
        this.chatbotMsg = chatbotMsg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
