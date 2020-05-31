package com.example.carbazar.Models;

public class contactSellerModel {
    private String name;
    private String email;
    private String subject;
    private String message;
    private String posterEmail;

    public contactSellerModel() {
    }

    public contactSellerModel(String name, String email, String subject, String message, String posterEmail) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.posterEmail = posterEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPosterEmail() {
        return posterEmail;
    }

    public void setPosterEmail(String posterEmail) {
        this.posterEmail = posterEmail;
    }
}
