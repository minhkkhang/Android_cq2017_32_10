package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.ygaps.travelapp.pojo.ListResponse;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.Tour;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewInvitationActivity extends AppCompatActivity {
    SharedPreferences pref;
    ListView invites;
    String token;
    UserService userService;
    ArrayList<Tour> list;
    ListTourAdapter adapter;
    Menu mMenu;
    ProgressBar bar;
    LinearLayout toolsLayout;
    Integer tourIdSelected=-1;
    Button acceptBtn,declineBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitation);

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
                Intent mIntent=new Intent(ViewInvitationActivity.this,FrontPage.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                break;
            }
            default:break;
        }
        return true;
    }

    private void SetContent(){
        list = new ArrayList<>();

        invites = findViewById(R.id.invite_list);
        bar=findViewById(R.id.invite_progressBar);
        toolsLayout=findViewById(R.id.invite_toolLayout);
        acceptBtn=findViewById(R.id.invite_accept);
        declineBtn=findViewById(R.id.invite_decline);
        Button showBtn=findViewById(R.id.invite_view);

        pref = getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        token = pref.getString("token", "");
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        adapter = new ListTourAdapter(this, list);
        invites.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        GetListInvitations();

        invites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tourIdSelected==adapter.getTourId(position)){
                    if(toolsLayout.getVisibility()==View.VISIBLE)toolsLayout.setVisibility(View.GONE);
                    tourIdSelected=-1;
                }
                tourIdSelected=adapter.getTourId(position);
                if(toolsLayout.getVisibility()==View.GONE)toolsLayout.setVisibility(View.VISIBLE);
                invites.setSelection(-1);
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourIdSelected==-1)return;
                ResponseInvitation(true);
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tourIdSelected==-1)return;
                ResponseInvitation(false);
            }
        });
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewInvite();
            }
        });
    }

    private void GetListInvitations(){
        Call<ListResponse>call=userService.getInvitations(token,1,1);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    GetAll(response.body().getTotal());
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(ViewInvitationActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void GetAll(Integer total){
        if(total==0){
            bar.setVisibility(View.GONE);
            invites.setVisibility(View.VISIBLE);
            return;
        }
        Call<ListResponse>call=userService.getInvitations(token,1,total);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if(response.isSuccessful()){
                    List<Tour> temp=response.body().getTours();

                    for(int i=0;i<temp.size()-1;i++){
                        Long max=Long.parseLong(temp.get(i).getCreatedOn());
                        int maxIndex=i;
                        for(int j=i+1;j<temp.size();j++){
                            if(Long.parseLong(temp.get(j).getCreatedOn())>max){
                                max=Long.parseLong(temp.get(j).getCreatedOn());
                                maxIndex=j;
                            }
                        }
                        Tour tempTour=temp.get(i);
                        temp.set(i,temp.get(maxIndex));
                        temp.set(maxIndex,tempTour);
                    }
                    list.clear();
                    list.addAll(temp);
                    adapter.notifyDataSetChanged();
                    bar.setVisibility(View.GONE);
                    invites.setVisibility(View.VISIBLE);
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(ViewInvitationActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void ResponseInvitation(final boolean isAccepted){
        ReviewRequest request=new ReviewRequest();
        request.setTourId(tourIdSelected);
        request.setIsAccepted(isAccepted);
        Call<Message>call=userService.responseInvitation(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    invites.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    GetListInvitations();
                    toolsLayout.setVisibility(View.GONE);
                    if(isAccepted)ViewInvite();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    if(message.getMessage()==null)return;
                    Toast.makeText(ViewInvitationActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                call.cancel();
            }
        });
    }
    private void ViewInvite(){
        Intent intent = new Intent(ViewInvitationActivity.this, TourInfo.class);
        intent.putExtra("tourId", tourIdSelected);
        intent.putExtra("sourceActivity", "invitation");
        startActivity(intent);
    }
}
