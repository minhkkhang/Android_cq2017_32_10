package com.example.a1712390_1712518.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class StopPointListRequest {

    @SerializedName("hasOneCoordinate")
    private Boolean hasOneCoordinate;
    @SerializedName("coordList")
    private List<CoordList> coordList = null;

    public Boolean getHasOneCoordinate() {
        return hasOneCoordinate;
    }

    public void setHasOneCoordinate(Boolean hasOneCoordinate) {
        this.hasOneCoordinate = hasOneCoordinate;
    }

    public List<CoordList> getCoordList() {
        return coordList;
    }

    public void setCoordList(List<CoordList> coordList) {
        this.coordList = coordList;
    }

}
