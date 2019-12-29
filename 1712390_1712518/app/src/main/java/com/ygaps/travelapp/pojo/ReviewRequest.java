package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {

    @SerializedName("serviceId")
    private Integer serviceId;
    @SerializedName("tourId")
    private Integer tourId;
    @SerializedName("point")
    private Integer point;
    @SerializedName("review")
    private String review;
    @SerializedName("feedback")
    private String feedback;
    @SerializedName("comment")
    private String comment;
    @SerializedName("noti")
    private String noti;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("invitedUserId")
    private Integer invitedUserId;
    @SerializedName("isInvited")
    private Boolean isInvited;
    @SerializedName("isAccepted")
    private Boolean isAccepted;


    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNoti() {
        return noti;
    }

    public void setNoti(String noti) {
        this.noti = noti;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(Integer invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public Boolean getIsInvited() {
        return isInvited;
    }

    public void setIsInvited(Boolean isInvited) {
        this.isInvited = isInvited;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
