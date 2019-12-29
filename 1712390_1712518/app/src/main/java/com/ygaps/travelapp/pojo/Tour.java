package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tour {
    @SerializedName("id")
    private Integer id;
    @SerializedName("status")
    private Integer status;
    @SerializedName("name")
    private String name;
    @SerializedName("minCost")
    private String minCost;
    @SerializedName("maxCost")
    private String maxCost;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;
    @SerializedName("adults")
    private Integer adults;
    @SerializedName("childs")
    private Integer childs;
    @SerializedName("isPrivate")
    private Boolean isPrivate;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("message")
    private String message;
    @SerializedName("hostId")
    private String hostId;
    @SerializedName("isHost")
    private Boolean isHost;

    @SerializedName("hostName")
    private String hostName;
    @SerializedName("hostPhone")
    private String hostPhone;
    @SerializedName("hostEmail")
    private String hostEmail;
    @SerializedName("hostAvatar")
    private String hostAvatar;
    @SerializedName("createdOn")
    private String createdOn;
    @SerializedName("type")
    private String type;


    @SerializedName("stopPoints")
    private List<StopPointViewObject> stopPoints = null;
    @SerializedName("comments")
    private List<TourComment> comments = null;
    @SerializedName("members")
    private List<TourComment> members = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getAdults() {
        return adults;
    }

    public void setAdults(Integer adults) {
        this.adults = adults;
    }

    public Integer getChilds() {
        return childs;
    }

    public void setChilds(Integer childs) {
        this.childs = childs;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public List<StopPointViewObject> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(List<StopPointViewObject> stopPoints) {
        this.stopPoints = stopPoints;
    }
    public List<TourComment> getComments() {
        return comments;
    }

    public void setComments(List<TourComment> comments) {
        this.comments = comments;
    }

    public List<TourComment> getMembers() {
        return members;
    }

    public void setMembers(List<TourComment> members) {
        this.members = members;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostPhone() {
        return hostPhone;
    }

    public void setHostPhone(String hostPhone) {
        this.hostPhone = hostPhone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type =type;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getHostAvatar() {
        return hostAvatar;
    }

    public void setHostAvatar(String hostAvatar) {
        this.hostAvatar = hostAvatar;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getIsHost() {
        return isHost;
    }

    public void setIsHost(Boolean isHost) {
        this.isHost = isHost;
    }

    public void setTourInfo(Tour tour){
        this.setId(tour.getId());
        this.setName(tour.getName());
        this.setAdults(tour.getAdults());
        this.setChilds(tour.getChilds());
        this.setMinCost(tour.getMinCost());
        this.setMaxCost(tour.getMaxCost());
        this.setIsPrivate(tour.getIsPrivate());
        this.setStatus(tour.getStatus());
        this.setStartDate(tour.getStartDate());
        this.setEndDate(tour.getEndDate());
    }
}
