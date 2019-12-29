package com.ygaps.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ygaps.travelapp.pojo.FireBaseTokenRequest;
import com.ygaps.travelapp.pojo.ListResponse;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.Tour;
import com.google.gson.Gson;
import com.ygaps.travelapp.service.MyFirebaseService;

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
    ProgressBar bar;
    LinearLayout searchLayout;
    EditText searchTxt;
    int total;
    String deviceId;
    String fcmToken;
    static boolean isHistory=false;
    public static MyFirebaseService firebaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_front_page);
        SetContent();
        if(firebaseService!=null)firebaseService=new MyFirebaseService();
        if(fcmToken.compareTo("")!=0)return;
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("firebase", "getInstanceIdFail", task.getException());
                    return;
                }
                final String firebaseToken = task.getResult().getToken();
                Call<Message> call=userService.registerFirebaseToken(token,
                        new FireBaseTokenRequest(firebaseToken,deviceId,1,"1.0"));
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){
                            fcmToken=firebaseToken;
                            Log.d("firebase",response.body().getMessage());

                        }else Log.d("firebase","failed");
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
                SharedPreferences.Editor edit=pref.edit();
                edit.putString("firebaseToken",firebaseToken);
                edit.apply();
            }
        });

    }
    private void SetContent(){
        list = new ArrayList<>();
        total = 1;

        rvTours = findViewById(R.id.rv_items);
        bar=findViewById(R.id.frontpage_progressBar);
        bar.setVisibility(View.VISIBLE);
        searchLayout=findViewById(R.id.frontpage_searchLayout);
        searchTxt=findViewById(R.id.frontpage_searchTxt);

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        token = pref.getString("token", "");
        fcmToken= pref.getString("firebaseToken", "");
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        adapter = new ListTourAdapter(this, list);
        rvTours.setAdapter(adapter);
        if(isHistory) GetUserHistoryTours();
        else OpenListPage(1);
        adapter.notifyDataSetChanged();
        rvTours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchLayout.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Intent intent = new Intent(FrontPage.this, TourInfo.class);
                intent.putExtra("tourId", adapter.getTourId(position));
                if(!isHistory)intent.putExtra("sourceActivity", "frontpage");
                else intent.putExtra("sourceActivity", "history");
                startActivity(intent);
            }
        });

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.Filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        mMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.userLogOut:{
                searchLayout.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                RemoveFireBaseToken();
                break;
            }
            case R.id.createTour:{
                searchLayout.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Intent intent=new Intent(FrontPage.this,CreateTourActivity.class);
                intent.putExtra("action","CreateTour");
                startActivity(intent);
                break;
            }
            case R.id.search_btn:{
                if(searchLayout.getVisibility()==View.GONE){
                    searchLayout.setVisibility(View.VISIBLE);
                    if(searchLayout.requestFocus())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                else {
                    searchLayout.clearFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    searchLayout.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.viewStopPoint:{
                searchLayout.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Intent intent=new Intent(FrontPage.this,MapsActivity.class);
                intent.putExtra("action","ViewStopPoint");
                startActivityForResult(intent,16);
                break;
            }
            case R.id.viewInvites:{
                Intent intent=new Intent(FrontPage.this,ViewInvitationActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.viewHistory:{
                if(isHistory)break;
                bar.setVisibility(View.VISIBLE);
                isHistory=true;
                GetUserHistoryTours();
                break;
            }
            case R.id.main_done:{
                if(!isHistory)break;
                bar.setVisibility(View.VISIBLE);
                isHistory=false;
                OpenListPage(1);
                break;
            }
            case R.id.viewUsers:{
                Intent intent=new Intent(FrontPage.this,UsersListActivity.class);
                intent.putExtra("action","ViewUser");
                startActivity(intent);
                break;
            }
            case R.id.userProfile:{
                Intent intent=new Intent(FrontPage.this,UserInfoEditActivity.class);
                startActivity(intent);
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
        editor.remove("firebaseToken");
        editor.apply();
        isHistory=false;
        Intent myIntent = new Intent(FrontPage.this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }
    void OpenListPage(Integer index){
        if(index<1)return;
        Call<ListResponse> call = userService.getListTour(token,5,index,
                "startDate",true);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    total=response.body().getTotal();
                    OpenAll();
                }else {
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    void OpenAll(){
        Call<ListResponse>call = userService.getListTour(token,total,1,
                "startDate",true);

        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    List<Tour> tempList;
                    tempList = response.body().getTours();
                    for(int i=0;i<tempList.size();i++)
                        if(tempList.get(i).getStatus()==-1){
                            tempList.remove(i);
                            i--;
                        }
                    for(int i=0;i<tempList.size()/2;i++){
                        Tour temp=new Tour();
                        temp.setTourInfo(tempList.get(i));
                        tempList.get(i).setTourInfo(tempList.get(tempList.size()-1-i));
                        tempList.get(tempList.size()-1-i).setTourInfo(temp);
                    }
                    adapter.addItems(tempList);

                    bar.setVisibility(View.GONE);
                }else {
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                bar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }
    private void RemoveFireBaseToken(){
        if(fcmToken==null){
            LogOut();
            return;
        }
        FireBaseTokenRequest request=new FireBaseTokenRequest();
        request.setFcmToken(fcmToken);
        request.setDeviceId(deviceId);
        Call<Message>call=userService.removeFireBaseToken(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful())LogOut();
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }
    private void GetUserHistoryTours(){
        Call<ListResponse>call=userService.getHistoryTours(token,1,1);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    total=response.body().getTotal();
                    OpenAllHistory();
                }else {
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    void OpenAllHistory(){
        Call<ListResponse>call=userService.getHistoryTours(token,1,total);

        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    List<Tour> tempList;
                    tempList = response.body().getTours();
                    for(int i=0;i<tempList.size();i++)
                        if(tempList.get(i).getStatus()==-1){
                            tempList.remove(i);
                            i--;
                        }
                    for(int i=0;i<tempList.size()/2;i++){
                        Tour temp=new Tour();
                        temp.setTourInfo(tempList.get(i));
                        tempList.get(i).setTourInfo(tempList.get(tempList.size()-1-i));
                        tempList.get(tempList.size()-1-i).setTourInfo(temp);
                    }
                    adapter.addItems(tempList);

                    bar.setVisibility(View.GONE);
                }else {
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(FrontPage.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                bar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }
}
