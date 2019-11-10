package com.example.demomainpage;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("/user/login")
    Call<LoginResponse> login(@Query("emailPhone") String emailPhone, @Query("password") String password);
}

