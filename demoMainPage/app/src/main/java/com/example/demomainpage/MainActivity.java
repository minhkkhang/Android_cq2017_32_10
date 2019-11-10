package com.example.demomainpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button logoutButton;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView rvTours=(RecyclerView)findViewById(R.id.rv_items);
        rvTours.setLayoutManager(new LinearLayoutManager(this));
        logoutButton=findViewById(R.id.logout_btn);

        pref = getSharedPreferences(LoginActivity.PREF_NAME,MODE_PRIVATE);
        String token=pref.getString("token","");

        OkHttpClient client=new OkHttpClient();
        Moshi moshi=new Moshi.Builder().build();
        Type userType= Types.newParameterizedType(List.class,Tours.class);
        final JsonAdapter<List<Tours>>jsonAdapter=moshi.adapter(userType);

        Request request=new Request.Builder()
                .url("http://35.197.153.192:3000/tour/history-user?pageIndex=1&pageSize=10")
                .header("Authorization",token)
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

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=pref.edit();
                editor.clear();
                editor.apply();
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(myIntent);
            }
        });
    }

}


