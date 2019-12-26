package com.ygaps.travelapp.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StopPointListResponse {

    @SerializedName("stopPoints")
    private List<StopPointViewObject> stopPoints = null;

    public List<StopPointViewObject> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(List<StopPointViewObject> stopPoints) {
        this.stopPoints = stopPoints;
    }

}
