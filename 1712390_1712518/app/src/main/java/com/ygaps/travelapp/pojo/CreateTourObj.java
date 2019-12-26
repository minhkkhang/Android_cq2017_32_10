package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateTourObj {
    @SerializedName("hostId")
    private String hostId;
    @SerializedName("status")
    private Integer status;
    @SerializedName("name")
    private String name;
    @SerializedName("minCost")
    private Integer minCost;
    @SerializedName("maxCost")
    private Integer maxCost;
    @SerializedName("startDate")
    private Long startDate;
    @SerializedName("endDate")
    private Long endDate;
    @SerializedName("adults")
    private Integer adults;
    @SerializedName("childs")
    private Integer childs;
    @SerializedName("sourceLat")
    private Float sourceLat;
    @SerializedName("sourceLong")
    private Float sourceLong;
    @SerializedName("desLat")
    private Float desLat;
    @SerializedName("desLong")
    private Float desLong;
    @SerializedName("id")
    private Integer id;
    @SerializedName("isPrivate")
    private Boolean isPrivate;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("message")
    private List<Message> message = null;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
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

    public Float getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(Float sourceLat) {
        this.sourceLat = sourceLat;
    }

    public Float getSourceLong() {
        return sourceLong;
    }

    public void setSourceLong(Float sourceLong) {
        this.sourceLong = sourceLong;
    }

    public Float getDesLat() {
        return desLat;
    }

    public void setDesLat(Float desLat) {
        this.desLat = desLat;
    }

    public Float getDesLong() {
        return desLong;
    }

    public void setDesLong(Float desLong) {
        this.desLong = desLong;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Message> getMessage() {
        return message;
    }
}
