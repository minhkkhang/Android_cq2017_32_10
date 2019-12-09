package com.example.a1712390_1712518.pojo;

import com.google.gson.annotations.SerializedName;

public class StopPointObj {
    @SerializedName("tourId")
    private Integer tourId;
    @SerializedName("name")
    private String name;
    @SerializedName("lat")
    private Float lat;
    @SerializedName("long")
    private Float _long;
    @SerializedName("arrivalAt")
    private Long arrivalAt;
    @SerializedName("leaveAt")
    private Long leaveAt;
    @SerializedName("minCost")
    private Integer minCost;
    @SerializedName("maxCost")
    private Integer maxCost;
    @SerializedName("serviceTypeId")
    private Integer serviceTypeId;
    @SerializedName("id")
    private Integer id;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("index")
    private Integer index;
    @SerializedName("landingTimes")
    private Integer landingTimes;
    @SerializedName("address")
    private String address;
    @SerializedName("contact")
    private String contact;
    @SerializedName("selfStarRatings")
    private Integer selfStarRatings;

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Long getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(Long arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public Long getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(Long leaveAt) {
        this.leaveAt = leaveAt;
    }

    public Integer getMinCost() {
        return minCost;
    }

    public void setMinCost(Integer minCost) {
        this.minCost = minCost;
    }

    public Integer getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Integer maxCost) {
        this.maxCost = maxCost;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
    public Integer getLandingTimes() {
        return landingTimes;
    }

    public void setLandingTimes(Integer landingTimes) {
        this.landingTimes = landingTimes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getSelfStarRatings() {
        return selfStarRatings;
    }

    public void setSelfStarRatings(Integer selfStarRatings) {
        this.selfStarRatings = selfStarRatings;
    }
}
