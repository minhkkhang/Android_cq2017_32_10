package com.example.a1712390_1712518;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1712390_1712518.pojo.CreateTourObj;
import com.example.a1712390_1712518.pojo.Message;
import com.google.gson.Gson;

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
    EditText CreateTour_Adult;
    EditText CreateTour_Children;
    EditText CreateTour_MinCost;
    EditText CreateTour_MaxCost;
    EditText CreateTour_Avatar;
    CheckBox CreateTour_isPrivate;
    Button CreateTour_SubmitBtn;
    DatePickerDialog picker;
    private static final int MAP_ACTIVITY_REQUEST_CODE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SetContent();


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
                CreateTourRequest();
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
        sharedPreferences=getApplicationContext().getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        editor=sharedPreferences.edit();

        editor.remove("sourceLat");
        editor.remove("sourceLong");
        editor.remove("sourceExisted");

        editor.remove("desLat");
        editor.remove("desLong");
        editor.remove("desExisted");
        editor.apply();
    }
    private void CreateTourRequest(){
        String token = sharedPreferences.getString("token","");
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
        createTourObj.setEndDate(EndDate.getTime());

        createTourObj.setAdults(Integer.parseInt(CreateTour_Adult.getText().toString()));
        createTourObj.setChilds(Integer.parseInt(CreateTour_Children.getText().toString()));
        createTourObj.setMinCost(Integer.parseInt(CreateTour_MinCost.getText().toString()));
        createTourObj.setMaxCost(Integer.parseInt(CreateTour_MaxCost.getText().toString()));
        createTourObj.setIsPrivate(CreateTour_isPrivate.isChecked());

        if(!sharedPreferences.getBoolean("sourceExisted",false) ||
                !sharedPreferences.getBoolean("desExisted",false)){
            Toast.makeText(this,
                    "Please choose a Start point and a destination for your tour",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        createTourObj.setSourceLat(sharedPreferences.getFloat("sourceLat",0));
        createTourObj.setSourceLong(sharedPreferences.getFloat("sourceLong",0));
        editor.remove("sourceLat");
        editor.remove("sourceLong");
        editor.remove("sourceExisted");

        createTourObj.setDesLat(sharedPreferences.getFloat("desLat",0));
        createTourObj.setDesLong(sharedPreferences.getFloat("desLong",0));
        editor.remove("desLat");
        editor.remove("desLong");
        editor.remove("desExisted");
        editor.apply();

        Call<CreateTourObj> call=userService.CreateTour(token,createTourObj);
        call.enqueue(new Callback<CreateTourObj>() {
            @Override
            public void onResponse(Call<CreateTourObj> call, Response<CreateTourObj> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Tour created!",
                            Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(CreateTourActivity.this, MapsActivity.class);
                    myIntent.putExtra("action","StopPoint");
                    myIntent.putExtra("tourID",response.body().getId());
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

}
