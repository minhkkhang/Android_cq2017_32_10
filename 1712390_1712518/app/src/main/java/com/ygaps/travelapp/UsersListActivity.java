package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ygaps.travelapp.pojo.ListResponse;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.Tour;
import com.ygaps.travelapp.pojo.UserInfoObj;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersListActivity extends AppCompatActivity {
    SharedPreferences pref;
    ListView users;
    String token;
    UserService userService;
    ArrayList<UserInfoObj> userList;
    ListUserAdapter adapter;
    Menu mMenu;
    EditText searchTxt;
    Button searchBtn;
    Integer tourId=-1;
    String action;
    String key;
    ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Intent intent;
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            tourId=bundle.getInt("tourId",-1);
            action=bundle.getString("action","");
        }

        SetContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.default_menu,menu);
        mMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.default_done: {
                if(action.compareTo("InviteMember")==0){
                    Intent mIntent=new Intent(UsersListActivity.this,TourInfo.class);
                    mIntent.putExtra("tourId",tourId);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    break;
                }
                if(action.compareTo("ViewUser")==0){
                    Intent mIntent=new Intent(UsersListActivity.this,FrontPage.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    break;
                }
            }
            default:break;
        }
        return true;
    }

    private void SetContent(){
        userList = new ArrayList<>();

        users = findViewById(R.id.users_list);
        searchTxt=findViewById(R.id.users_searchTxt);
        searchBtn=findViewById(R.id.users_searchBtn);
        bar=findViewById(R.id.users_progressBar);

        pref = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        token = pref.getString("token", "");
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        adapter = new ListUserAdapter(this, userList);
        users.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        key="";
        DoSearch();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.Filter(searchTxt.getText().toString());
                searchTxt.clearFocus();
            }
        });
        users.setClickable(true);
        users.setFocusable(true);
        users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(action.compareTo("InviteMember")!=0)return;
                TextView textView =view.findViewById(R.id.user_info_invite);
                InviteUser(adapter.getUserId(position),textView);
            }
        });
    }

    private void DoSearch(){
        Call<ListResponse> call=userService.searchUser(token,key,1,1);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    ShowAllSearchResults(response.body().getTotal());
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(UsersListActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void ShowAllSearchResults(Integer total){
        if(total==null||total==0)total=1;
        Call<ListResponse> call=userService.searchUser(token,key,1,total);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    userList.clear();
                    userList.addAll(response.body().getUsers());
                    adapter.SetOrigin(response.body().getUsers());
                    adapter.notifyDataSetChanged();
                    bar.setVisibility(View.GONE);
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(UsersListActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void InviteUser(final Integer userId, final TextView button){
        if(adapter.isInvitedId(userId))return;
        ReviewRequest request=new ReviewRequest();
        request.setTourId(tourId);
        request.setInvitedUserId(userId);
        request.setIsInvited(true);
        Call<Message> call = userService.inviteMember(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    adapter.SetInvitedIds(userId);
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(UsersListActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(UsersListActivity.this, t.getMessage()
                        , Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
}
