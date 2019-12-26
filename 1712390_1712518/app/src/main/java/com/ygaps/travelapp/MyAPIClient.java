package com.ygaps.travelapp;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAPIClient {
    public static final String BASE_URL = "http://35.197.153.192:3000/";
    private static Retrofit retrofit=null;
    public static Retrofit buildHTTPClient() {
        if(retrofit!=null)return retrofit;
        Gson gson=new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    //Create OKHttp Client used by retrofit
    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .build();
    }


}
