package com.example.a1712390_1712518.pojo;

import com.google.gson.annotations.SerializedName;

public class CoordinateSet {

    @SerializedName("lat")
    private Float lat;
    @SerializedName("long")
    private Float _long;

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLong() {
        return _long;
    }

    public void setLong(Float _long) {
        this._long = _long;
    }

}
