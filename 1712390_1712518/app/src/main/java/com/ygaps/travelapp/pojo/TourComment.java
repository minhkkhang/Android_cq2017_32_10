package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

public class TourComment {

    @SerializedName("review")
    private String review;
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("point")
    private Integer point;
    @SerializedName("feedback")
    private String feedback;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("comment")
    private String comment;
    @SerializedName("notification")
    private String notification;
    @SerializedName("isHost")
    private Boolean isHost=false;
    @SerializedName("userId")
    private Integer userId;
    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public void setInfoComment(TourComment tourComment){
        this.setAvatar(tourComment.getAvatar());
        this.setComment(tourComment.getComment());
        this.setId(tourComment.getId());
        this.setName(tourComment.getName());
        this.setCreatedOn(tourComment.getCreatedOn());
        this.setFeedback(tourComment.getFeedback());
        this.setReview(tourComment.getReview());
        this.setPoint(tourComment.getPoint());
    }
}
