package com.example.a1712390_1712518.pojo;

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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

}
