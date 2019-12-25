package com.example.a1712390_1712518;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.a1712390_1712518.pojo.ListTourResponse;
import com.example.a1712390_1712518.pojo.Tour;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrontPage extends AppCompatActivity {
    SharedPreferences pref;
    ListView rvTours;
    String token;
    UserService userService;
    ArrayList<Tour> list;
    ListTourAdapter adapter;
    Menu mMenu;
    Integer pageIndex;
    int total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        list=new ArrayList<>();
        pageIndex=1;
        total=1;

        rvTours=findViewById(R.id.rv_items);

        pref = getSharedPreferences(LoginActivity.PREF_NAME,MODE_PRIVATE);
        token=pref.getString("token","");
        userService=MyAPIClient.buildHTTPClient().create(UserService.class);
        adapter=new ListTourAdapter(this,list);
        rvTours.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvTours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FrontPage.this,MapsActivity.class);
                intent.putExtra("tourId",adapter.getTourId(position));
                intent.putExtra("action","View");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        mMenu=menu;
        OpenListPage(pageIndex);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.userLogOut:{
                LogOut();
                break;
            }
            case R.id.createTour:{
                Intent intent=new Intent(FrontPage.this,CreateTourActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nextPage:{
                OpenNextPage();
                break;
            }
            case R.id.previousPage:{
                OpenPreviousPage();
                break;
            }
            default:break;
        }
        return true;
    }

    void LogOut(){
        SharedPreferences.Editor editor=pref.edit();
        editor.remove("userID");
        editor.remove("token");
        editor.apply();
        Intent myIntent = new Intent(FrontPage.this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
    void OpenPreviousPage(){
        if(pageIndex>1){
            pageIndex-=1;
        }
        else return;
        adapter.notifyDataSetChanged();
        OpenListPage(pageIndex);
    }
    void OpenNextPage(){
        if(total<=pageIndex*30){
            return;
        }
        pageIndex+=1;
        adapter.notifyDataSetChanged();
        OpenListPage(pageIndex);
    }

    void OpenListPage(Integer index){
        if(index<1)return;
        Call<ListTourResponse> call = userService.getListTour(token,30,index,
                "startDate",true);
        adapter.notifyDataSetChanged();
        call.enqueue(new Callback<ListTourResponse>() {
            @Override
            public void onResponse(Call<ListTourResponse> call, Response<ListTourResponse> response) {
                if(response.isSuccessful()){
                    List<Tour> tempList;
                    tempList = response.body().getTours();
                    list.clear();
                    list.addAll(tempList);
                    total=response.body().getTotal();
                    adapter.notifyDataSetChanged();
                    rvTours.setSelectionAfterHeaderView();
                    MenuItem item=mMenu.findItem(R.id.currentPage);
                    item.setTitle(pageIndex.toString());
                }else {
                    Gson gson = new Gson();
                    ListTourResponse message=
                            gson.fromJson(response.errorBody().charStream(), ListTourResponse.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListTourResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
}
