package com.example.a1712390_1712518.pojo;

import com.google.gson.annotations.SerializedName;

public class StopPointObj {
    @SerializedName("tourId")
    private Integer tourId;
    @SerializedName("name")
    private String name;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("long")
    private Double _long;
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
    @SerializedName("serviceId")
    private Integer serviceId;
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
    @SerializedName("provinceId")
    private Integer provinceId;

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
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

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer id) {
        this.serviceId = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
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
