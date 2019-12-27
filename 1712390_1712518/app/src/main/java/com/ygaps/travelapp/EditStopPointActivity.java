package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditStopPointActivity extends AppCompatActivity {
    EditText nameTxt,addressTxt,minCostTxt,maxCostTxt,startDate,endDate;
    RadioGroup serviceType;
    Button submitBtn;
    DatePickerDialog picker;
    Intent intent;
    SimpleDateFormat df;
    int index;
    Double lat,_long;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stop_point);

        SetContent();
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            nameTxt.setText(bundle.getString("name",""));
            addressTxt.setText(bundle.getString("address",""));
            Integer mincost=bundle.getInt("minCost",-1);
            Integer maxcost=bundle.getInt("maxCost",-1);
            if(mincost>-1)minCostTxt.setText(mincost.toString());
            if(maxcost>-1)maxCostTxt.setText(maxcost.toString());
            String date;
            Calendar calendar=Calendar.getInstance();
            try{
                calendar.setTimeInMillis(bundle.getLong("startDate",0));
            }catch(Exception e){
                e.printStackTrace();
            }
            startDate.setText(df.format(calendar.getTime()));
            try{
                calendar.setTimeInMillis(bundle.getLong("endDate",0));
            }catch(Exception e){
                e.printStackTrace();
            }
            endDate.setText(df.format(calendar.getTime()));
            int servicetype=bundle.getInt("serviceType",4);
            switch(servicetype){
                case 1:{
                    serviceType.check(R.id.stoppoint_edit_type1);
                    break;
                }
                case 2:{
                    serviceType.check(R.id.stoppoint_edit_type2);
                    break;
                }
                case 3:{
                    serviceType.check(R.id.stoppoint_edit_type3);
                    break;
                }
                default:{
                    serviceType.check(R.id.stoppoint_edit_type4);
                    break;
                }
            }
            index=bundle.getInt("index",-1);
            lat=bundle.getDouble("lat",0);
            _long=bundle.getDouble("long",0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.stop_point_menu,menu);
        mMenu=menu;
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
            default:break;
        }
        return false;
    }

    private void SetContent(){
        nameTxt=findViewById(R.id.stoppoint_edit_name);
        addressTxt=findViewById(R.id.stoppoint_edit_address);
        minCostTxt=findViewById(R.id.stoppoint_edit_MinCost);
        maxCostTxt=findViewById(R.id.stoppoint_edit_MaxCost);
        startDate=findViewById(R.id.stoppoint_edit_StartDate);
        endDate=findViewById(R.id.stoppoint_edit_EndDate);
        serviceType=findViewById(R.id.stoppoint_edit_serviceType);
        submitBtn=findViewById(R.id.stoppoint_edit_submitBtn);
        df=new SimpleDateFormat("dd/MM/yyyy");

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(EditStopPointActivity.this,
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
                    picker = new DatePickerDialog(EditStopPointActivity.this,
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
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameTxt.getText().toString().compareTo("")==0 ||
                addressTxt.getText().toString().compareTo("")==0 ||
                startDate.getText().toString().compareTo("")==0 ||
                endDate.getText().toString().compareTo("")==0)return;

                long startdate=0,enddate=0;
                try {
                    Date d=df.parse(startDate.getText().toString());
                    startdate=d.getTime();
                    d=df.parse(endDate.getText().toString());
                    enddate=d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                intent=new Intent();
                intent.putExtra("name",nameTxt.getText().toString());
                intent.putExtra("startDate",startdate);
                intent.putExtra("endDate",enddate);
                Integer minCost,maxCost;
                try{
                    minCost=Integer.parseInt(minCostTxt.getText().toString());
                }
                catch (Exception e){minCost=-1;}
                try{
                    maxCost=Integer.parseInt(maxCostTxt.getText().toString());
                }
                catch (Exception e){maxCost=-1;}
                intent.putExtra("minCost",minCost);
                intent.putExtra("maxCost",maxCost);
                intent.putExtra("address",addressTxt.getText().toString());
                intent.putExtra("province",1);
                intent.putExtra("lat",lat);
                intent.putExtra("long",_long);
                intent.putExtra("index",index);
                int servicetype=4;
                switch (serviceType.getCheckedRadioButtonId()){
                    case R.id.stoppoint_edit_type1:{
                        servicetype=1;
                        break;
                    }
                    case R.id.stoppoint_edit_type2:{
                        servicetype=2;
                        break;
                    }
                    case R.id.stoppoint_edit_type3:{
                        servicetype=3;
                        break;
                    }
                    default:{
                        servicetype=4;
                        break;
                    }
                }
                intent.putExtra("serviceType",servicetype);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
