package com.ygaps.travelapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.pojo.CreateTourObj;
import com.ygaps.travelapp.pojo.Message;
import com.google.gson.Gson;
import com.ygaps.travelapp.pojo.Tour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTourActivity extends AppCompatActivity {
    UserService userService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText CreateTour_Name;
    EditText CreateTour_StartDate;
    EditText CreateTour_EndDate;
    Button CreateTour_StartPointBtn;
    Button CreateTour_EndPointBtn;
    TextView CreateTour_StartPointDetail;
    TextView CreateTour_EndPointDetail;
    TextView CreateTour_Title;
    EditText CreateTour_Adult;
    EditText CreateTour_Children;
    EditText CreateTour_MinCost;
    EditText CreateTour_MaxCost;
    EditText CreateTour_Avatar;
    CheckBox CreateTour_isPrivate;
    Button CreateTour_SubmitBtn;
    LinearLayout linearLayout;
    DatePickerDialog picker;
    String action;
    Integer tourId;
    String token;
    private static final int MAP_ACTIVITY_REQUEST_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            action=bundle.getString("action","CreateTour");
            tourId=bundle.getInt("tourId",-1);
        }

        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SetContent();
        if(action.compareTo("EditTour")==0)ExtractTourInfo(tourId);

        CreateTour_StartDate.setInputType(InputType.TYPE_NULL);
        CreateTour_EndDate.setInputType(InputType.TYPE_NULL);
        CreateTour_StartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(CreateTourActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    CreateTour_StartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                    CreateTour_StartDate.clearFocus();
                }
            }
        });
        CreateTour_EndDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(CreateTourActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    CreateTour_EndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                    CreateTour_EndDate.clearFocus();
                }
            }
        });

        CreateTour_StartPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMapActivity("StartPoint");
            }
        });
        CreateTour_EndPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMapActivity("EndPoint");
            }
        });
        CreateTour_SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitBtnClicked();
            }
        });
    }

    private void SetContent(){
        CreateTour_Name=findViewById(R.id.CreateTour_Name);
        CreateTour_StartDate=findViewById(R.id.CreateTour_StartDate);
        CreateTour_EndDate=findViewById(R.id.CreateTour_EndDate);
        CreateTour_StartPointBtn=findViewById(R.id.CreateTour_StartPoint);
        CreateTour_EndPointBtn=findViewById(R.id.CreateTour_EndPoint);
        CreateTour_StartPointDetail=findViewById(R.id.CreateTour_StartPointDetail);
        CreateTour_EndPointDetail=findViewById(R.id.CreateTour_EndPointDetail);
        CreateTour_Adult=findViewById(R.id.CreateTour_Adult);
        CreateTour_Children=findViewById(R.id.CreateTour_Children);
        CreateTour_MinCost=findViewById(R.id.CreateTour_MinCost);
        CreateTour_MaxCost=findViewById(R.id.CreateTour_MaxCost);
        CreateTour_Avatar=findViewById(R.id.CreateTour_Avatar);
        CreateTour_isPrivate=findViewById(R.id.CreateTour_isPrivate);
        CreateTour_SubmitBtn=findViewById(R.id.CreateTour_ContinueBtn);
        CreateTour_Title=findViewById(R.id.createTour_Title);
        linearLayout=findViewById(R.id.CreateTour_pickPoint);
        if(action.compareTo("EditTour")==0)linearLayout.setVisibility(View.GONE);
        sharedPreferences=getApplicationContext().getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        editor=sharedPreferences.edit();
        token = sharedPreferences.getString("token","");
        editor.remove("startDate");
        editor.remove("endDate");
        editor.remove("sourceLat");
        editor.remove("sourceLong");
        editor.remove("sourceExisted");

        editor.remove("desLat");
        editor.remove("desLong");
        editor.remove("desExisted");
        editor.remove("sourceAddress");
        editor.remove("desAddress");
        editor.apply();
    }
    private void SubmitBtnClicked(){
        CreateTourObj createTourObj=new CreateTourObj();

        createTourObj.setName(CreateTour_Name.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date StartDate = new Date(),EndDate = new Date();
        try {
            StartDate = sdf.parse(CreateTour_StartDate.getText().toString());
            EndDate = sdf.parse(CreateTour_EndDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Date Format", Toast.LENGTH_SHORT).show();
            return;
        }
        createTourObj.setStartDate(StartDate.getTime());
        editor.putLong("startDate",StartDate.getTime());
        createTourObj.setEndDate(EndDate.getTime());
        editor.putLong("endDate",EndDate.getTime());
        editor.apply();

        createTourObj.setAdults(parseInt(CreateTour_Adult.getText().toString()));
        createTourObj.setChilds(parseInt(CreateTour_Children.getText().toString()));
        createTourObj.setMinCost(parseInt(CreateTour_MinCost.getText().toString()));
        createTourObj.setMaxCost(parseInt(CreateTour_MaxCost.getText().toString()));
        createTourObj.setIsPrivate(CreateTour_isPrivate.isChecked());

        if((!sharedPreferences.getBoolean("sourceExisted",false) ||
                !sharedPreferences.getBoolean("desExisted",false))
        && action.compareTo("CreateTour")==0){
            Toast.makeText(this,
                    "Please choose a Start point and a destination for your tour",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        createTourObj.setSourceLat(sharedPreferences.getFloat("sourceLat",0));
        createTourObj.setSourceLong(sharedPreferences.getFloat("sourceLong",0));

        createTourObj.setDesLat(sharedPreferences.getFloat("desLat",0));
        createTourObj.setDesLong(sharedPreferences.getFloat("desLong",0));

        if(action.compareTo("CreateTour")==0)CreateTour(createTourObj);
        else UpdateTour(createTourObj);
    }

    private void OpenMapActivity(String action){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("action",action);
        startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                Bundle bundle = data.getExtras();
                String action="";
                if (bundle != null) {
                    action = bundle.getString("action");
                }
                else return;
                if(action.compareTo("StartPoint")==0){
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append("Lat: ");
                    stringBuilder.append(sharedPreferences.getFloat("sourceLat",0));
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append("Long: ");
                    stringBuilder.append(sharedPreferences.getFloat("sourceLong",0));
                    CreateTour_StartPointDetail.setText(stringBuilder.toString());
                }
                if(action.compareTo("EndPoint")==0){
                    StringBuilder stringBuilder=new StringBuilder();
                    stringBuilder.append("Lat: ");
                    stringBuilder.append(sharedPreferences.getFloat("desLat",0));
                    stringBuilder.append(System.getProperty("line.separator"));
                    stringBuilder.append("Long: ");
                    stringBuilder.append(sharedPreferences.getFloat("desLong",0));
                    CreateTour_EndPointDetail.setText(stringBuilder.toString());
                }
            }
        }
    }

    private void ExtractTourInfo(int tourId){
        Call<Tour> call=userService.getTourInfo(token,tourId);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    Tour tour=response.body();
                    CreateTour_Title.setText(tour.getName());
                    CreateTour_Name.setText(tour.getName());
                    CreateTour_Adult.setText(tour.getAdults().toString());
                    CreateTour_Children.setText(tour.getChilds().toString());
                    CreateTour_MinCost.setText(tour.getMinCost());
                    CreateTour_MaxCost.setText(tour.getMaxCost());
                    CreateTour_isPrivate.setChecked(tour.getIsPrivate());
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar=Calendar.getInstance();
                    try{
                        calendar.setTimeInMillis(Long.parseLong(tour.getStartDate()));
                        CreateTour_StartDate.setText(format.format(calendar.getTime()));

                        calendar.setTimeInMillis(Long.parseLong(tour.getEndDate()));
                        CreateTour_EndDate.setText(format.format(calendar.getTime()));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(CreateTourActivity.this, message.getMessage()
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

    private void UpdateTour(CreateTourObj createTourObj){
        createTourObj.setId(tourId);
        Call<CreateTourObj> call=userService.updateTour(token,createTourObj);
        call.enqueue(new Callback<CreateTourObj>() {
            @Override
            public void onResponse(Call<CreateTourObj> call, Response<CreateTourObj> response) {
                if(response.isSuccessful()){
                    Intent intent=new Intent(CreateTourActivity.this,MapsActivity.class);
                    intent.putExtra("action","EditTour");
                    intent.putExtra("tourId",tourId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
                    Toast.makeText(CreateTourActivity.this,
                            errorMsg.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTourObj> call, Throwable t) {
                Toast.makeText(CreateTourActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }
    private void CreateTour(CreateTourObj createTourObj){
        Call<CreateTourObj> call=userService.CreateTour(token,createTourObj);
        call.enqueue(new Callback<CreateTourObj>() {
            @Override
            public void onResponse(Call<CreateTourObj> call, Response<CreateTourObj> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Tour created!",
                            Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(CreateTourActivity.this, MapsActivity.class);
                    myIntent.putExtra("action","StopPoint");
                    myIntent.putExtra("tourId",response.body().getId());
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(myIntent);
                }
                else{
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
                    Toast.makeText(CreateTourActivity.this,
                            errorMsg.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTourObj> call, Throwable t) {
                Toast.makeText(CreateTourActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }
    private Integer parseInt(String num){
        Integer res;
        try{
            res=Integer.parseInt(num);
        }
        catch (Exception e){
            res=1;
        }
        return res;
    }
}
