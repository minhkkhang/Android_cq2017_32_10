package com.example.demomainpage;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    List<Tours>tours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView rvTours=(RecyclerView)findViewById(R.id.rv_items);
        rvTours.setLayoutManager(new LinearLayoutManager(this));


        OkHttpClient client=new OkHttpClient();
        Moshi moshi=new Moshi.Builder().build();
        Type userType= Types.newParameterizedType(List.class,Tours.class);
        final JsonAdapter<List<Tours>>jsonAdapter=moshi.adapter(userType);

        Request request=new Request.Builder()
                .url("http://35.197.153.192:3000/tour/history-user?pageIndex=1&pageSize=10")
                .header("Authorization","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjIsInVzZXJuYW1lIjoidGhpbmg5NyIsImVtYWlsIjoibmd1eWVubWluaHRoaW5oOTdAZ21haWwuY29tIiwiZXhwIjoxNTUzODczNDU1NTY2LCJpYXQiOjE1NTEyODE0NTV9.lQ-RkLSwD3UyRXWvSRaTIsn1f_3ZRMWd-nfRutcwXFw")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("Error","Network Error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json=response.body().string();
                final List<Tours>tours=jsonAdapter.fromJson(json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvTours.setAdapter(new RecyclerDataAdapter(tours,MainActivity.this));
                    }
                });
            }
        });
    }

}


