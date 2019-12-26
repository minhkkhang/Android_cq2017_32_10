package com.ygaps.travelapp.pojo;

import com.google.gson.annotations.SerializedName;

public class FireBaseTokenRequest {

    @SerializedName("fcmToken")
    private String fcmToken;
    @SerializedName("deviceId")
    private String deviceId;
    @SerializedName("platform")
    private Integer platform;
    @SerializedName("appVersion")
    private String appVersion;

    public FireBaseTokenRequest(){
        fcmToken="";
        deviceId="";
        platform=1;
        appVersion="";
    }
    public FireBaseTokenRequest(String fcm,String deviceid,Integer plat,String appver){
        fcmToken=fcm;
        deviceId=deviceid;
        platform=plat;
        appVersion=appver;
    }
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

}
