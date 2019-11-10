package com.example.demomainpage;

public class LoginResponse {
    String message;
    String userID;
    String token;

    public String getMessage(){
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID(){
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getToken(){
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

