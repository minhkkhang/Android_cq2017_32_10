package com.example.a1712390_1712518;

import com.example.a1712390_1712518.pojo.LoginRequest;
import com.example.a1712390_1712518.pojo.LoginResponse;
import com.example.a1712390_1712518.pojo.SignUpRequest;
import com.example.a1712390_1712518.pojo.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("user/register")
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);
}
