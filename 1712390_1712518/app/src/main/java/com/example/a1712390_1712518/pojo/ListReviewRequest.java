package com.example.a1712390_1712518.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListReviewRequest {

    @SerializedName("total")
    private Integer total;
    @SerializedName("feedbackList")
    private List<TourComment> feedbacks = null;
    @SerializedName("reviewList")
    private List<TourComment> reviews = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<TourComment> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<TourComment> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<TourComment> getReviews() {
        return reviews;
    }

    public void setReviews(List<TourComment> reviews) {
        this.reviews = reviews;
    }

}
