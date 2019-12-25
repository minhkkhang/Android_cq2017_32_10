package com.example.a1712390_1712518.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AddStopPointRequest {

    @SerializedName("tourId")
    private Integer tourId;
    @SerializedName("stopPoints")
    private List<StopPointObj> stopPoints = null;
    @SerializedName("deleteIds")
    private List<Integer> deleteIds=null;

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public List<StopPointObj> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(List<StopPointObj> stopPoints) {
        this.stopPoints = stopPoints;
    }

    public List<Integer> getDeleteIds() {
        return deleteIds;
    }

    public void setDeleteIds(List<Integer> deleteIds) {
        this.deleteIds = deleteIds;
    }

}