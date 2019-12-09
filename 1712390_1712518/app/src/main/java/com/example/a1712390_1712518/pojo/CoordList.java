package com.example.a1712390_1712518.pojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CoordList {

    @SerializedName("coordinateSet")
    private List<CoordinateSet> coordinateSet = null;

    public List<CoordinateSet> getCoordinateSet() {
        return coordinateSet;
    }

    public void setCoordinateSet(List<CoordinateSet> coordinateSet) {
        this.coordinateSet = coordinateSet;
    }

}
