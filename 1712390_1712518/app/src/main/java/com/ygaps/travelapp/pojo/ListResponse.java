package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse {
    @SerializedName("total")
    private Integer total;
    @SerializedName("tours")
    private List<Tour> tours = null;
    @SerializedName("users")
    private List<UserInfoObj> users = null;
    @SerializedName("notiList")
    private List<TourComment> notifications = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    public List<Tour> getTours() {
        return tours;
    }

    public void setTours(List<Tour> tours) {
        this.tours = tours;
    }

    public List<UserInfoObj> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfoObj> users) {
        this.users = users;
    }

    public List<TourComment> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<TourComment> notifications) {
        this.notifications = notifications;
    }
}
