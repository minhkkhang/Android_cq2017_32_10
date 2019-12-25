package com.example.a1712390_1712518.pojo;

import com.google.gson.annotations.SerializedName;

public class StopPointViewObject {

    @SerializedName("id")
    private Integer id;
    @SerializedName("serviceId")
    private Integer serviceId;
    @SerializedName("name")
    private String name;
    @SerializedName("lat")
    private String lat;
    @SerializedName("long")
    private String _long;
    @SerializedName("arrivalAt")
    private String arrivalAt;
    @SerializedName("leaveAt")
    private String leaveAt;
    @SerializedName("minCost")
    private String minCost;
    @SerializedName("maxCost")
    private String maxCost;
    @SerializedName("serviceTypeId")
    private Integer serviceTypeId;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("landingTimes")
    private Integer landingTimes;
    @SerializedName("address")
    private String address;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("contact")
    private String contact;

    @SerializedName("provinceId")
    private Integer provinceId;
    @SerializedName("landingTimesOfUser")
    private String landingTimesOfUser;
    @SerializedName("selfStarRatings")
    private Integer selfStarRatings;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return _long;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

    public String getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(String arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public String getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(String leaveAt) {
        this.leaveAt = leaveAt;
    }

    public String getMinCost() {
        return minCost;
    }

    public void setMinCost(String minCost) {
        this.minCost = minCost;
    }

    public String getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(String maxCost) {
        this.maxCost = maxCost;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getLandingTimesOfUser() {
        return landingTimesOfUser;
    }

    public void setLandingTimesOfUser(String landingTimesOfUser) {
        this.landingTimesOfUser = landingTimesOfUser;
    }
    public Integer getSelfStarRatings() {
        return selfStarRatings;
    }

    public void setSelfStarRatings(Integer selfStarRatings) {
        this.selfStarRatings = selfStarRatings;
    }

}
