package com.ygaps.travelapp.pojo;


import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("emailPhone")
    private String emailPhone;
    @SerializedName("password")
    private String password;

    public String getEmailPhone() {
        return emailPhone;
    }

    public void setEmailPhone(String emailPhone) {
        this.emailPhone = emailPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
