package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.Tour;
import com.ygaps.travelapp.pojo.TourComment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourInfo extends AppCompatActivity {
    Menu mMenu;
    ImageView avatar;
    TextView nameTxt,minCostTxt,maxCostTxt,startDateTxt,endDateTxt,adultTxt,childrenTxt,tourIdTxt;
    EditText reviewTxt,commentTxt;
    RatingBar reviewBar;
    Button reviewBtn,commentBtn;
    RecyclerView comments;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences pref;
    UserService userService;
    ArrayList<TourComment> list;
    String token;
    Intent intent;
    Integer tourId;
    ListReviewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);

        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            tourId=bundle.getInt("tourId",-1);
        }

        SetContent();
        GetTourInfo(tourId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.tour_info_menu,menu);
        mMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view: {
                Intent mIntent=new Intent(TourInfo.this,MapsActivity.class);
                mIntent.putExtra("action","View");
                mIntent.putExtra("tourId",tourId);
                startActivityForResult(mIntent,20);
                break;
            }
            default:break;
        }
        return false;
    }

    private void SetContent(){
        avatar=findViewById(R.id.tourinfo_avatar);
        nameTxt=findViewById(R.id.tourinfo_name);
        minCostTxt=findViewById(R.id.tourinfo_mincost);
        maxCostTxt=findViewById(R.id.tourinfo_maxcost);
        adultTxt=findViewById(R.id.tourinfo_adult);
        childrenTxt=findViewById(R.id.tourinfo_children);
        startDateTxt=findViewById(R.id.tourinfo_startdate);
        endDateTxt=findViewById(R.id.tourinfo_enddate);
        reviewTxt=findViewById(R.id.tourinfo_reviewEditTxt);
        commentTxt=findViewById(R.id.tourinfo_commentEditTxt);
        reviewBtn=findViewById(R.id.tourinfo_reviewBtn);
        commentBtn=findViewById(R.id.tourinfo_commentBtn);
        reviewBar=findViewById(R.id.tourinfo_rate);
        comments=findViewById(R.id.tourinfo_comments);
        tourIdTxt=findViewById(R.id.tourinfo_id);

        list=new ArrayList<>();
        layoutManager=new LinearLayoutManager(this);
        comments.setLayoutManager(layoutManager);
        adapter=new ListReviewAdapter(list);
        comments.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        reviewBar.setStepSize((float)1);
        pref=getSharedPreferences(LoginActivity.PREF_NAME,MODE_PRIVATE);
        token = pref.getString("token", "");
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentTxt.getText().toString().compareTo("")==0)return;
                SendComment();
            }
        });
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendReview();
            }
        });
    }

    private void GetTourInfo(int id){
        Call<Tour> call=userService.getTourInfo(token,id);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    SetInfo(response.body());
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(TourInfo.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void SetInfo(Tour tour){
        StringBuilder builder=new StringBuilder();
        nameTxt.setText(tour.getName());
        builder.append("ID: ");
        builder.append(tour.getId().toString());
        tourIdTxt.setText(builder.toString());
        builder=new StringBuilder();
        builder.append("Adults: ");
        builder.append(tour.getAdults().toString());
        adultTxt.setText(builder.toString());
        builder=new StringBuilder();
        builder.append("Children: ");
        builder.append(tour.getChilds().toString());
        childrenTxt.setText(builder.toString());
        builder=new StringBuilder();
        builder.append("Min cost: ");
        builder.append(tour.getMinCost().toString());
        minCostTxt.setText(builder.toString());
        builder=new StringBuilder();
        builder.append("Max cost: ");
        builder.append(tour.getMaxCost().toString());
        maxCostTxt.setText(builder.toString());


        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm z");
        Calendar calendar=Calendar.getInstance();
        try{
            builder=new StringBuilder();
            calendar.setTimeInMillis(Long.parseLong(tour.getStartDate()));
            builder.append("Start Date: ");
            builder.append(format.format(calendar.getTime()));
            startDateTxt.setText(builder.toString());

            builder=new StringBuilder();
            calendar.setTimeInMillis(Long.parseLong(tour.getEndDate()));
            builder.append("End Date: ");
            builder.append(format.format(calendar.getTime()));
            endDateTxt.setText(builder.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        list.addAll(tour.getComments());
        adapter.notifyDataSetChanged();
    }

    private void SendComment(){
        ReviewRequest request=new ReviewRequest();
        request.setComment(commentTxt.getText().toString());
        request.setTourId(tourId);
        Call<Message>call=userService.sendTourComment(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    commentTxt.setText("");
                    RefreshComments();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(TourInfo.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void RefreshComments()
    {
        Call<Tour> call=userService.getTourInfo(token,tourId);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    list.clear();
                    list.addAll(response.body().getComments());
                    adapter.notifyDataSetChanged();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(TourInfo.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void SendReview(){
        ReviewRequest request=new ReviewRequest();
        request.setReview(reviewTxt.getText().toString());
        request.setPoint((int)reviewBar.getRating());
        request.setTourId(tourId);
        Call<Message>call=userService.sendTourReview(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    reviewTxt.setText("");
                    reviewBar.setNumStars(0);
                    reviewBar.setRating((float)0);
                    Toast.makeText(TourInfo.this, "Review sent!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(TourInfo.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                call.cancel();
            }
        });
    }
}
