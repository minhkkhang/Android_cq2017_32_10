package com.ygaps.travelapp.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListTourResponse {

    @SerializedName("total")
    private Integer total;
    @SerializedName("tours")
    private List<Tour> tours = null;
    @SerializedName("message")
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
