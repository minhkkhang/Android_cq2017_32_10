package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.ygaps.travelapp.pojo.AddStopPointRequest;
import com.ygaps.travelapp.pojo.CreateTourObj;
import com.ygaps.travelapp.pojo.ListReviewRequest;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.StopPointObj;
import com.ygaps.travelapp.pojo.StopPointViewObject;
import com.ygaps.travelapp.pojo.TourComment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStopPointActivity extends AppCompatActivity {

    private Intent intent;
    private String status,action;
    private Integer Id,tour_related_id,tourId;
    private UserService userService;
    TextView nameTxt,addressTxt,minCostTxt,maxCostTxt,contactTxt,ratingTxt,serviceTypeTxt;
    TextView viewDateTxt;
    EditText startDate,endDate,review;
    LinearLayout editTourLayout;
    Button addBtn,removeBtn,reviewBtn;
    RecyclerView reviewListView;
    RecyclerView.LayoutManager layoutManager;
    RatingBar ratingBar;
    DatePickerDialog picker;
    SharedPreferences sharedPreferences;
    ImageView avatar;
    ArrayList<TourComment> reviews;
    ListReviewAdapter adapter;
    TextView statusTxt;
    String date;
    Menu mMenu;
    StopPointViewObject mTour;
    StopPointObj obj;
    private static int MAX_SIZE=50;
    private static int STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE=24;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stop_point);

    }

    private void SetContent(){
        statusTxt=findViewById(R.id.stoppoint_status);
        nameTxt=findViewById(R.id.stoppoint_name);
        addressTxt=findViewById(R.id.stoppoint_address);
        minCostTxt=findViewById(R.id.stoppoint_mincost);
        maxCostTxt=findViewById(R.id.stoppoint_maxcost);
        contactTxt=findViewById(R.id.stoppoint_contact);
        ratingTxt=findViewById(R.id.stoppoint_rating);
        serviceTypeTxt=findViewById(R.id.stoppoint_serviceType);
        ratingBar=findViewById(R.id.stoppoint_rate);
        startDate=findViewById(R.id.stoppoint_StartDate);
        endDate=findViewById(R.id.stoppoint_EndDate);
        review=findViewById(R.id.stoppoint_reviewEditTxt);
        editTourLayout=findViewById(R.id.stoppoint_TourEditLayout);
        addBtn=findViewById(R.id.stoppoint_addBtn);
        removeBtn=findViewById(R.id.stoppoint_removeBtn);
        reviewBtn=findViewById(R.id.stoppoint_reviewBtn);
        reviewListView=findViewById(R.id.stoppoint_reviews);
        avatar=findViewById(R.id.stoppoint_avatar);
        viewDateTxt=findViewById(R.id.stoppoint_viewDate);
        sharedPreferences=getApplicationContext().getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        if(action.compareTo("view")==0 || action.compareTo("stoppoint")==0)editTourLayout.setVisibility(View.GONE);
        else{
            if(status.compareTo("included")==0)addBtn.setText(getString(R.string.update));
            else {
                removeBtn.setVisibility(View.GONE);
                statusTxt.setVisibility(View.GONE);
            }
        }
        if(action.compareTo("view")==0){
            viewDateTxt.setVisibility(View.VISIBLE);
            viewDateTxt.setText(date);
        }
        ratingBar.setStepSize((float)1);

        reviews=new ArrayList<>();
        layoutManager=new LinearLayoutManager(this);
        reviewListView.setLayoutManager(layoutManager);
        adapter=new ListReviewAdapter(reviews);
        reviewListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        LoadStopPointInfo(Id);
        if(date.compareTo("")!=0){
            String[] tokens=date.split("-");
            startDate.setText(tokens[0]);
            endDate.setText(tokens[1]);
        }

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(ViewStopPointActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    startDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                    startDate.clearFocus();
                }
            }
        });
        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(ViewStopPointActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    endDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                    endDate.clearFocus();
                }
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewRequest reviewRequest=new ReviewRequest();
                reviewRequest.setServiceId(Id);
                reviewRequest.setFeedback(review.getText().toString());
                reviewRequest.setPoint((int)ratingBar.getRating());
                String token = sharedPreferences.getString("token","");
                Call<Message> call=userService.sendServiceReview(token,reviewRequest);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){
                            review.setText("");
                            ratingBar.setNumStars(0);
                            ratingBar.setRating((float)0);
                            reviews.clear();
                            GetReviewList();
                        }
                        else{
                            Gson gson = new Gson();
                            Message message=
                                    gson.fromJson(response.errorBody().charStream(), Message.class);
                            Toast.makeText(ViewStopPointActivity.this, message.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(ViewStopPointActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        call.cancel();
                    }
                });
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder=new StringBuilder();
                if(startDate.getText().toString().compareTo("")==0 ||
                endDate.getText().toString().compareTo("")==0){
                    Toast.makeText(ViewStopPointActivity.this, "Please enter arrival and leaving time",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.append(startDate.getText());
                builder.append("-");
                builder.append(endDate.getText());
                ReturnToMapActivity("included",builder.toString());
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnToMapActivity("removed","");
            }
        });

        GetReviewList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action = bundle.getString("action","view");
            if(action.compareTo("edits")==0){
                obj=new StopPointObj();
                tour_related_id=bundle.getInt("tour_related_id",0);
                tourId=bundle.getInt("tourId",0);
                obj.setArrivalAt(bundle.getLong("startDate",0));
                obj.setLeaveAt(bundle.getLong("endDate",0));
                obj.setLat(bundle.getDouble("latitude",1000));
                obj.setLong(bundle.getDouble("longitude",1000));
            }
            status = bundle.getString("status","");
            Id=bundle.getInt("Id",-1);
            date=bundle.getString("date","");
        }


        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.stop_point_menu,menu);
        mMenu=menu;
        invalidateOptionsMenu();
        MenuItem item=mMenu.findItem(R.id.stop_point_edit);
        if(action.compareTo("edits")!=0)item.setVisible(false);
        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SetContent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_point_done: {
                intent=new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
                break;
            }
            case R.id.stop_point_edit:{
                intent = new Intent(this, EditStopPointActivity.class);
                intent.putExtra("name",mTour.getName());
                intent.putExtra("action",action);
                intent.putExtra("minCost",Integer.parseInt(mTour.getMinCost()));
                intent.putExtra("maxCost",Integer.parseInt(mTour.getMaxCost()));
                intent.putExtra("serviceType",mTour.getServiceTypeId());
                intent.putExtra("address",mTour.getAddress());
                intent.putExtra("index",-2);

                startActivityForResult(intent, STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE);
                break;
            }
            default:break;
        }
        return false;
    }

    private void ReturnToMapActivity(String userAction, String extra){
        intent=new Intent();
        intent.putExtra("Id", Id);
        intent.putExtra("action",userAction);
        if(extra.compareTo("")!=0)intent.putExtra("date",extra);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void LoadStopPointInfo(int Id){
        String token = sharedPreferences.getString("token","");
        Call<StopPointViewObject> call = userService.getServiceDetail(token,Id);
        call.enqueue(new Callback<StopPointViewObject>() {
            @Override
            public void onResponse(Call<StopPointViewObject> call, Response<StopPointViewObject> response) {
                if(response.isSuccessful()){
                    ExtractStopPointInfo(response.body());
                    mTour=response.body();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(ViewStopPointActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StopPointViewObject> call, Throwable t) {
                Toast.makeText(ViewStopPointActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void ExtractStopPointInfo(StopPointViewObject obj){
        if(obj.getAvatar()!=null){
            if(!obj.getAvatar().isEmpty()){
                Picasso.with(this).load(obj.getAvatar())
                        .centerCrop()
                        .fit()
                        .into(avatar);
            }
            else{
                SetDefaultAvatar(obj.getServiceTypeId());
            }
        }else{
            SetDefaultAvatar(obj.getServiceTypeId());
        }
        StringBuilder builder;
        nameTxt.setText(obj.getName());

        builder=new StringBuilder();
        builder.append("Rating: ");
        if(obj.getSelfStarRatings()!=null){
            builder.append(obj.getSelfStarRatings());
        }else builder.append("?");
        ratingTxt.setText(builder.toString());

        builder=new StringBuilder();
        builder.append("Service type: ");
        switch(obj.getServiceTypeId()){
            case 1:{
                builder.append("Restaurant");
                break;
            }
            case 2:{
                builder.append("Hotel");
                break;
            }
            case 3:{
                builder.append("Rest Station");
                break;
            }
            default:{
                builder.append("Other");
                break;
            }
        }
        serviceTypeTxt.setText(builder.toString());

        builder=new StringBuilder();
        builder.append("Address: ");
        if(obj.getAddress()!=null){
            builder.append(obj.getAddress());
        }
        addressTxt.setText(builder.toString());

        builder=new StringBuilder();
        builder.append("Contact: ");
        if(obj.getContact()!=null){
            builder.append(obj.getContact());
        }
        contactTxt.setText(builder.toString());

        builder=new StringBuilder();
        builder.append("Mincost: ");
        if(obj.getMinCost()!=null){
            builder.append(obj.getMinCost());
        }
        minCostTxt.setText(builder.toString());

        builder=new StringBuilder();
        builder.append("Maxcost: ");
        if(obj.getMinCost()!=null){
            builder.append(obj.getMaxCost());
        }
        maxCostTxt.setText(builder.toString());
    }
    private void GetReviewList(){
        String token = sharedPreferences.getString("token","");
        Call<ListReviewRequest> call=userService.getServiceReviews(token,Id,1,MAX_SIZE);
        call.enqueue(new Callback<ListReviewRequest>() {
            @Override
            public void onResponse(Call<ListReviewRequest> call, Response<ListReviewRequest> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    List<TourComment> tempList=response.body().getFeedbacks();
                    for(int i=0;i<tempList.size()/2;i++){
                        TourComment temp=new TourComment();
                        temp.setInfoComment(tempList.get(i));
                        tempList.get(i).setInfoComment(tempList.get(tempList.size()-1-i));
                        tempList.get(tempList.size()-1-i).setInfoComment(temp);
                    }
                    reviews.addAll(tempList);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(ViewStopPointActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListReviewRequest> call, Throwable t) {
                Toast.makeText(ViewStopPointActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }

    private void SetDefaultAvatar(int serviceType){
        switch (serviceType){
            case 1:{
                avatar.setImageResource(R.drawable.restaurant);
                return;
            }
            case 2:{
                avatar.setImageResource(R.drawable.hotel);
                return;
            }
            case 3:{
                avatar.setImageResource(R.drawable.reststation);
                return;
            }
            default:{
                avatar.setImageResource(R.drawable.wallpaper);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Bundle bundle=data.getExtras();
                if(bundle==null)return;
                List<StopPointObj> objs=new ArrayList<>();
                String token = sharedPreferences.getString("token","");
                AddStopPointRequest request=new AddStopPointRequest();

                obj.setId(tour_related_id);
                obj.setServiceId(Id);
                obj.setAddress(bundle.getString("address"));
                obj.setName(bundle.getString("name"));
                obj.setMinCost(bundle.getInt("minCost"));
                obj.setMaxCost(bundle.getInt("maxCost"));
                obj.setServiceTypeId(bundle.getInt("serviceType"));
                if(obj.getLat()==1000 || obj.getLong()==1000 || obj.getArrivalAt()==0
                || obj.getLeaveAt()==0){
                    Toast.makeText(ViewStopPointActivity.this, "Failed to update stop point"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                objs.add(obj);
                request.setTourId(tourId);
                request.setStopPoints(objs);
                request.setDeleteIds(null);
                Call<Message>call=userService.addStopPointsToTour(token,request);

                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){
                            LoadStopPointInfo(Id);
                        }
                        else{
                            Toast.makeText(ViewStopPointActivity.this, "Failed to update stop point"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(ViewStopPointActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        call.cancel();
                    }
                });

            }
        }
    }
}