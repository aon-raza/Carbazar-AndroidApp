package com.example.carbazar.Models;

public class writeReviewModel {
    private String make,
    model,
    version,
    title,
    description,
    priceReview,
    modelReview,
    brandReview,
    mileageReview,
    familiarity,
    user_id,
    username;

    public writeReviewModel() {
    }

    public writeReviewModel(String make, String model, String version, String title, String description, String priceReview, String modelReview, String brandReview, String mileageReview, String familiarity, String user_id, String username) {
        this.make = make;
        this.model = model;
        this.version = version;
        this.title = title;
        this.description = description;
        this.priceReview = priceReview;
        this.modelReview = modelReview;
        this.brandReview = brandReview;
        this.mileageReview = mileageReview;
        this.familiarity = familiarity;
        this.user_id = user_id;
        this.username = username;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceReview() {
        return priceReview;
    }

    public void setPriceReview(String priceReview) {
        this.priceReview = priceReview;
    }

    public String getModelReview() {
        return modelReview;
    }

    public void setModelReview(String modelReview) {
        this.modelReview = modelReview;
    }

    public String getBrandReview() {
        return brandReview;
    }

    public void setBrandReview(String brandReview) {
        this.brandReview = brandReview;
    }

    public String getMileageReview() {
        return mileageReview;
    }

    public void setMileageReview(String mileageReview) {
        this.mileageReview = mileageReview;
    }

    public String getFamiliarity() {
        return familiarity;
    }

    public void setFamiliarity(String familiarity) {
        this.familiarity = familiarity;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
