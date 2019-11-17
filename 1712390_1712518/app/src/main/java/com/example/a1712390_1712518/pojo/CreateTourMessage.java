package com.example.a1712390_1712518.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTourMessage {

    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("param")
    @Expose
    private String param;
    @SerializedName("msg")
    @Expose
    private String msg;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}