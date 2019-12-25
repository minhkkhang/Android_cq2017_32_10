package com.example.a1712390_1712518;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.a1712390_1712518.pojo.ListReviewRequest;
import com.example.a1712390_1712518.pojo.Message;
import com.example.a1712390_1712518.pojo.ReviewRequest;
import com.example.a1712390_1712518.pojo.StopPointViewObject;
import com.example.a1712390_1712518.pojo.TourComment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStopPointActivity extends AppCompatActivity {

    private Intent intent;
    private String status,action;
    private Integer Id;
    private UserService userService;
    TextView nameTxt,addressTxt,minCostTxt,maxCostTxt,contactTxt,ratingTxt;
    EditText startDate,endDate,review;
    LinearLayout editTourLayout;
    Button addBtn,removeBtn,reviewBtn;
    ListView reviewListView;
    RatingBar ratingBar;
    DatePickerDialog picker;
    SharedPreferences sharedPreferences;
    ImageView avatar;
    ArrayList<TourComment> reviews;
    ListReviewAdapter adapter;
    TextView statusTxt;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stop_point);
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action = bundle.getString("action","view");
            status = bundle.getString("status","");
            Id=bundle.getInt("Id",-1);
            date=bundle.getString("date","");
        }

        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SetContent();
    }

    private void SetContent(){
        statusTxt=findViewById(R.id.stoppoint_status);
        nameTxt=findViewById(R.id.stoppoint_name);
        addressTxt=findViewById(R.id.stoppoint_address);
        minCostTxt=findViewById(R.id.stoppoint_mincost);
        maxCostTxt=findViewById(R.id.stoppoint_maxcost);
        contactTxt=findViewById(R.id.stoppoint_contact);
        ratingTxt=findViewById(R.id.stoppoint_rating);
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
        sharedPreferences=getApplicationContext().getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        if(action.compareTo("view")==0)editTourLayout.setVisibility(View.GONE);
        else{
            if(status.compareTo("included")==0)addBtn.setText("Update");
            else {
                removeBtn.setVisibility(View.GONE);
                statusTxt.setVisibility(View.GONE);
            }
        }
        reviews=new ArrayList<>();
        adapter=new ListReviewAdapter(this,reviews);
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
                reviewRequest.setPoint(ratingBar.getNumStars());
                String token = sharedPreferences.getString("token","");
                Call<Message> call=userService.sendServiceReview(token,reviewRequest);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){
                            review.setText("");
                            ratingBar.setNumStars(0);
                            Toast.makeText(ViewStopPointActivity.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void ReturnToMapActivity(String userAction,String extra){
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
                avatar.setImageResource(R.drawable.ic_launcher_background);
            }
        }else{
            avatar.setImageResource(R.drawable.ic_launcher_background);
        }
        StringBuilder builder;
        nameTxt.setText(obj.getName());

        builder=new StringBuilder();
        builder.append("Rating: ");
        if(obj.getSelfStarRatings()!=null){
            builder.append(obj.getSelfStarRatings());
        }
        ratingTxt.setText(builder.toString());

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
    }
    private void GetReviewList(){
        String token = sharedPreferences.getString("token","");
        Call<ListReviewRequest> call=userService.getServiceReviews(token,Id,1,20);
        call.enqueue(new Callback<ListReviewRequest>() {
            @Override
            public void onResponse(Call<ListReviewRequest> call, Response<ListReviewRequest> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    reviews.addAll(response.body().getFeedbacks());
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
}
