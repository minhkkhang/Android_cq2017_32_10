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
import com.ygaps.travelapp.pojo.CreateTourObj;
import com.ygaps.travelapp.pojo.ListReviewRequest;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.Tour;
import com.ygaps.travelapp.pojo.TourComment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourInfo extends AppCompatActivity {
    Menu mMenu;
    ImageView avatar;
    TextView nameTxt,minCostTxt,maxCostTxt,startDateTxt,endDateTxt,adultTxt,childrenTxt,tourIdTxt;
    TextView isPrivateTxt,statusTxt;
    EditText reviewTxt,commentTxt;
    RatingBar reviewBar;
    Button reviewBtn,commentBtn;
    RecyclerView comments,reviews,members;
    RecyclerView.LayoutManager commentlayoutManager, reviewlayoutManager,memberlayoutManager;
    Button viewReviewBtn,viewCommentBtn,viewMemberBtn;
    Boolean isShowingReviews=false,isShowingComments=false,isSystemMessage=false,isShowingMembers=false;
    SharedPreferences pref;
    UserService userService;
    ArrayList<TourComment> commentList,reviewList,memberList;
    String token;
    Intent intent;
    Integer tourId;
    ListReviewAdapter commentAdapter,reviewAdapter,memberAdapter;
    Tour thisTour;
    int userId;
    MenuItem edit,delete,starttour,endtour;
    String sourceActivity;
    public static int MAX_SIZE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            tourId=bundle.getInt("tourId",-1);
            sourceActivity=bundle.getString("sourceActivity","frontpage");
        }
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.tour_info_menu,menu);
        mMenu=menu;
        invalidateOptionsMenu();
        edit=menu.findItem(R.id.edittour);
        delete=menu.findItem(R.id.deletetour);
        starttour=menu.findItem(R.id.starttour);
        endtour=menu.findItem(R.id.endtour);
        SetContent();
        GetTourInfo(tourId);
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
            case  R.id.clonetour:{
                CloneTour();
                break;
            }
            case R.id.edittour:{
                Intent mIntent=new Intent(TourInfo.this,CreateTourActivity.class);
                mIntent.putExtra("action","EditTour");
                mIntent.putExtra("tourId",tourId);
                startActivityForResult(mIntent,21);
                break;
            }
            case R.id.tourmenu_done:{
                if(sourceActivity.compareTo("invitation")==0) {
                    Intent mIntent=new Intent(TourInfo.this,ViewInvitationActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                }
                else{
                    Intent mIntent=new Intent(TourInfo.this,FrontPage.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                }


                break;
            }
            case R.id.deletetour:{
                UpdateStatus(-1);
                break;
            }
            case R.id.starttour:{
                UpdateStatus(1);
                break;
            }
            case R.id.endtour:{
                UpdateStatus(2);
                break;
            }
            case  R.id.invite:{
                Intent intent=new Intent(TourInfo.this,UsersListActivity.class);
                intent.putExtra("tourId",tourId);
                intent.putExtra("action","InviteMember");
                startActivity(intent);
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
        reviews=findViewById(R.id.tourinfo_reviews);
        members=findViewById(R.id.tourinfo_members);
        tourIdTxt=findViewById(R.id.tourinfo_id);
        isPrivateTxt=findViewById(R.id.tourinfo_isPrivate);
        statusTxt=findViewById(R.id.tourinfo_status);
        viewReviewBtn=findViewById(R.id.tourinfo_showReviews);
        viewCommentBtn=findViewById(R.id.tourinfo_showComments);
        viewMemberBtn=findViewById(R.id.tourinfo_showMembers);


        commentList=new ArrayList<>();
        reviewList=new ArrayList<>();
        memberList=new ArrayList<>();

        commentlayoutManager=new LinearLayoutManager(this);
        reviewlayoutManager=new LinearLayoutManager(this);
        memberlayoutManager=new LinearLayoutManager(this);

        comments.setLayoutManager(commentlayoutManager);
        reviews.setLayoutManager(reviewlayoutManager);
        members.setLayoutManager(memberlayoutManager);

        commentAdapter=new ListReviewAdapter(commentList);
        reviewAdapter=new ListReviewAdapter(reviewList);
        memberAdapter=new ListReviewAdapter(memberList);

        comments.setAdapter(commentAdapter);
        reviews.setAdapter(reviewAdapter);
        members.setAdapter(memberAdapter);

        commentAdapter.notifyDataSetChanged();
        reviewAdapter.notifyDataSetChanged();
        memberAdapter.notifyDataSetChanged();

        reviewBar.setStepSize((float)1);
        pref=getSharedPreferences(LoginActivity.PREF_NAME,MODE_PRIVATE);
        token = pref.getString("token", "");
        userId=pref.getInt("userID",0);
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

        viewReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowingReviews){
                    reviews.setVisibility(View.GONE);
                    viewReviewBtn.setText(getString(R.string.show_review));
                    isShowingReviews=false;
                }
                else{
                    reviews.setVisibility(View.VISIBLE);
                    viewReviewBtn.setText(getString(R.string.hide));
                    isShowingReviews=true;
                }
            }
        });

        viewCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowingComments){
                    comments.setVisibility(View.GONE);
                    viewCommentBtn.setText(getString(R.string.show_comment));
                    isShowingComments=false;
                }
                else{
                    comments.setVisibility(View.VISIBLE);
                    viewCommentBtn.setText(getString(R.string.hide));
                    isShowingComments=true;
                }
            }
        });

        viewMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowingMembers){
                    members.setVisibility(View.GONE);
                    viewMemberBtn.setText(R.string.member);
                    isShowingMembers=false;
                }
                else{
                    members.setVisibility(View.VISIBLE);
                    viewMemberBtn.setText(R.string.hide);
                    isShowingMembers=true;
                }
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
                    if(userId!=Integer.parseInt(response.body().getHostId())){
                        edit.setVisible(false);
                        delete.setVisible(false);
                        starttour.setVisible(false);
                        endtour.setVisible(false);
                    }
                    if(response.body().getStatus()>0)starttour.setVisible(false);
                    if(response.body().getStatus()>1)endtour.setVisible(false);
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
        if(tour.getIsPrivate())isPrivateTxt.setText(getString(R.string.private_tour));
        else isPrivateTxt.setText(getString(R.string.public_tour));

        switch (tour.getStatus()){
            case 0:{
                statusTxt.setText("Status: Open");
                break;
            }
            case 1:{
                statusTxt.setText("Status: Started");
                break;
            }
            case 2:{
                statusTxt.setText("Status: Closed");
                break;
            }
            default:{
                statusTxt.setText("Status: Deleted");
                break;}
        }

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
        memberList.clear();
        memberList.addAll(tour.getMembers());
        memberAdapter.notifyDataSetChanged();
        RefreshComments();
        refreshReviews();
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
        Call<ListReviewRequest> call=userService.getTourComments(token,tourId,1,MAX_SIZE);
        call.enqueue(new Callback<ListReviewRequest>() {
            @Override
            public void onResponse(Call<ListReviewRequest> call, Response<ListReviewRequest> response) {
                if(response.isSuccessful()){
                    List<TourComment> tempList=response.body().getComments();
                    for(int i=0;i<tempList.size()/2;i++){
                        TourComment temp=new TourComment();
                        temp.setInfoComment(tempList.get(i));
                        tempList.get(i).setInfoComment(tempList.get(tempList.size()-1-i));
                        tempList.get(tempList.size()-1-i).setInfoComment(temp);
                    }
                    commentList.clear();
                    commentList.addAll(tempList);
                    commentAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ListReviewRequest> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void refreshReviews(){
        Call<ListReviewRequest> call=userService.getTourReviews(token,tourId,1,MAX_SIZE);
        call.enqueue(new Callback<ListReviewRequest>() {
            @Override
            public void onResponse(Call<ListReviewRequest> call, Response<ListReviewRequest> response) {
                if(response.isSuccessful()){
                    List<TourComment> tempList=response.body().getReviews();
                    for(int i=0;i<tempList.size()/2;i++){
                        TourComment temp=new TourComment();
                        temp.setInfoComment(tempList.get(i));
                        tempList.get(i).setInfoComment(tempList.get(tempList.size()-1-i));
                        tempList.get(tempList.size()-1-i).setInfoComment(temp);
                    }
                    reviewList.clear();
                    reviewList.addAll(tempList);
                    reviewAdapter.notifyDataSetChanged();
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
            public void onFailure(Call<ListReviewRequest> call, Throwable t) {
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
                    refreshReviews();
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
                t.printStackTrace();
                call.cancel();
            }
        });
    }
    private void CloneTour(){
        ReviewRequest request=new ReviewRequest();
        request.setTourId(tourId);
        Call<Tour> call=userService.cloneTour(token,request);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    tourId=response.body().getId();
                    GetTourInfo(tourId);
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

    private void UpdateStatus(final int status){
        CreateTourObj createTourObj=new CreateTourObj();
        createTourObj.setId(tourId);
        createTourObj.setStatus(status);
        Call<CreateTourObj> call=userService.updateTour(token,createTourObj);
        call.enqueue(new Callback<CreateTourObj>() {
            @Override
            public void onResponse(Call<CreateTourObj> call, Response<CreateTourObj> response) {
                if(response.isSuccessful()){
                    if(status==-1){
                        Intent intent=new Intent(TourInfo.this,FrontPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    StringBuilder builder=new StringBuilder();
                    if(status==1){
                        builder.append("Tour ");
                        builder.append(nameTxt.getText().toString());
                        builder.append(" has started!");
                        isSystemMessage=true;
                        SendNoti(builder.toString());
                    }
                    if(status==2){
                        builder.append("Tour ");
                        builder.append(nameTxt.getText().toString());
                        builder.append(" has ended!");
                        isSystemMessage=true;
                        SendNoti(builder.toString());
                    }

                }
                else {
                    Gson gson = new Gson();
                    CreateTourObj message=
                            gson.fromJson(response.errorBody().charStream(), CreateTourObj.class);
                    List<Message> error=message.getMessage();
                    StringBuilder errorMsg = new StringBuilder();
                    errorMsg.append("Errors:");
                    errorMsg.append(System.getProperty("line.separator"));
                    for(int i=0;i<error.size();i++){
                        errorMsg.append(error.get(i).getMsg());
                        errorMsg.append(System.getProperty("line.separator"));
                    }
                    Toast.makeText(TourInfo.this,
                            errorMsg.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTourObj> call, Throwable t) {
                Toast.makeText(TourInfo.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void SendNoti(final String notification){
        ReviewRequest request=new ReviewRequest();
        request.setNoti(notification);
        request.setTourId(tourId);
        Call<Message>call=userService.sendTourNotification(token,request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    if(isSystemMessage){
                        GetTourInfo(tourId);
                        invalidateOptionsMenu();
                        if(notification.contains("started")){
                            starttour.setVisible(false);
                        }
                        else endtour.setVisible(false);
                    }
                    isSystemMessage=false;
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
                t.printStackTrace();
                call.cancel();
            }
        });
    }
}
