package com.example.a1712390_1712518;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Telephony;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1712390_1712518.pojo.AddStopPointRequest;
import com.example.a1712390_1712518.pojo.CoordList;
import com.example.a1712390_1712518.pojo.CoordinateSet;
import com.example.a1712390_1712518.pojo.Message;
import com.example.a1712390_1712518.pojo.StopPointListRequest;
import com.example.a1712390_1712518.pojo.StopPointListResponse;
import com.example.a1712390_1712518.pojo.StopPointObj;
import com.example.a1712390_1712518.pojo.StopPointViewObject;
import com.example.a1712390_1712518.pojo.Tour;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private static final int STOP_POINT_ACTIVITY_REQUEST_CODE = 11;
    private static final int STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE = 12;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    Geocoder geo;
    AutoCompleteTextView searchAutoComplete;
    Button submitBtn;
    Button searchBtn;
    private UiSettings uiSettings;
    private String actionType;
    private int tourId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    private Marker Source,Destination;
    private ArrayList<Marker> SuggestedStopPoints;
    private ArrayList<Marker> TourStopPoints;
    private ArrayList<StopPointViewObject> AllStopPoints;
    private ArrayList<StopPointObj> StopPointsToAdd;
    private Marker SearchedPoint;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    UserService userService;
    ArrayList<String>Hints;
    String[] _hints;
    InputMethodManager imm;
    String Address;
    SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            actionType = bundle.getString("action");
            if(actionType.compareTo("StopPoint")==0 || actionType.compareTo("View")==0){
                tourId=bundle.getInt("tourId",-1);
            }
        }
        //Builds HTTP Client for API Calls
        userService = MyAPIClient.buildHTTPClient().create(UserService.class);
        SetContent();
    }
    private void SetContent(){
        sharedPreferences=getApplicationContext().getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE);
        editor=sharedPreferences.edit();
        geo = new Geocoder(getBaseContext());
        searchAutoComplete=findViewById(R.id.map_hintautocomplete);
        submitBtn=findViewById(R.id.map_submit_btn);
        searchBtn=findViewById(R.id.map_searchsubmitbtn);
        imm=(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        Hints=new ArrayList<String>();
        SetHintAutoComplete();
        SuggestedStopPoints=new ArrayList<>();
        TourStopPoints=new ArrayList<>();
        AllStopPoints=new ArrayList<>();
        StopPointsToAdd=new ArrayList<>();
        df=new SimpleDateFormat("dd/MM/yyyy");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionType.compareTo("StartPoint")==0
                        || actionType.compareTo("EndPoint")==0){
                    intent=new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                }
                if(actionType.compareTo("StopPoint")==0){
                    ConvertPickedMarkersToStopPoints();
                    AddStopPointsToTour();
                }
                if(actionType.compareTo("View")==0){
                    Intent mIntent=new Intent(MapsActivity.this,FrontPage.class);
                    startActivity(mIntent);
                }
            }
        });
        searchAutoComplete.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH||
                actionId==EditorInfo.IME_ACTION_DONE||
                event!=null&&
                event.getAction()==KeyEvent.ACTION_DOWN&&
                event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                    if(event==null||!event.isShiftPressed()){
                        searchAutoComplete.clearFocus();
                        onSearchSubmit(searchAutoComplete.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchSubmit(searchAutoComplete.getText().toString());
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        SetUISettings();
        InitMap();
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                OnReceiveLatLng(latLng,false);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().compareTo("Selected Location")==0){
                    LatLng latLng=marker.getPosition();
                    OnReceiveLatLng(latLng,true);
                }
                for(int i=0;i<StopPointsToAdd.size();i++)
                    if(StopPointsToAdd.get(i).getName().compareTo(marker.getTitle())==0){
                        EditOrAddNewStopPoint(StopPointsToAdd.get(i),"update",i);
                        return false;
                    }
                if(SuggestedStopPoints.contains(marker) || TourStopPoints.contains(marker)){
                    if(TourStopPoints.contains(marker))ViewClickedStopPoint((int)marker.getTag(),
                            "included","edit",marker.getTitle());
                    else ViewClickedStopPoint((int)marker.getTag(),
                            "not included","edit","");
                }
                return false;
            }
        });
        enableMyLocation();
    }

    private void OnReceiveLatLng(LatLng latLng,boolean isClickOnMarker){
        if(actionType.compareTo("StartPoint")==0){
            editor.putFloat("sourceLat",(float)latLng.latitude);
            editor.putFloat("sourceLong",(float)latLng.longitude);
            editor.putBoolean("sourceExisted",true);
            editor.putString("sourceAddress",Address);
            editor.apply();
            ReturnToCreateTourActivity();
        }
        if(actionType.compareTo("EndPoint")==0){
            editor.putFloat("desLat",(float)latLng.latitude);
            editor.putFloat("desLong",(float)latLng.longitude);
            editor.putBoolean("desExisted",true);
            editor.putString("desAddress",Address);
            editor.apply();
            ReturnToCreateTourActivity();
        }
        if(actionType.compareTo("StopPoint")==0 && isClickOnMarker){
            StopPointObj obj=new StopPointObj();
            obj.setAddress("");
            obj.setName("");
            obj.setArrivalAt(Long.parseLong("0"));
            obj.setLeaveAt(Long.parseLong("0"));
            obj.setLat(latLng.latitude);
            obj.setLong(latLng.longitude);
            obj.setServiceTypeId(4);
            obj.setProvinceId(1);
            obj.setMinCost(0);
            obj.setMaxCost(0);
            EditOrAddNewStopPoint(obj,"add",-1);
            return;
        }
        addMarker(latLng);
    }
    private void InitMap(){
        Boolean sourceExisted=sharedPreferences.getBoolean("sourceExisted",false);
        Boolean desExisted=sharedPreferences.getBoolean("desExisted",false);
        LatLng sourcePos=new LatLng(sharedPreferences.getFloat("sourceLat",0),
                sharedPreferences.getFloat("sourceLong",0));
        LatLng desPos=new LatLng(sharedPreferences.getFloat("desLat",0),
                sharedPreferences.getFloat("desLong",0));
        Source=null;
        Destination=null;
        if(sourceExisted){
            Source=mMap.addMarker(new MarkerOptions().position(sourcePos).title("Source"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sourcePos));
        }
        if(desExisted){
            Destination=mMap.addMarker(new MarkerOptions().position(desPos).title("Destination"));
        }
        if(actionType.compareTo("StopPoint")==0){
            builder=new LatLngBounds.Builder();
            builder.include(sourcePos);
            builder.include(desPos);
            bounds=builder.build();
            CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds,0);
            mMap.animateCamera(cu);
            RequestStopPointList();
            ConvertSourceDesToStopPoints();
        }
        if(tourId!=-1 && actionType.compareTo("StopPoint")!=0)GetTourStopPoints(tourId);
    }

    private void RequestStopPointList(){
        ArrayList<CoordinateSet>coordinateSets=new ArrayList<>();
        CoordinateSet source=new CoordinateSet();
        source.setLat((float)bounds.southwest.latitude);
        source.setLong((float)bounds.southwest.longitude);
        CoordinateSet des=new CoordinateSet();
        des.setLat((float)bounds.northeast.latitude);
        des.setLong((float)bounds.northeast.longitude);
        coordinateSets.add(source);
        coordinateSets.add(des);

        CoordList coordList=new CoordList();
        coordList.setCoordinateSet(coordinateSets);
        ArrayList<CoordList>coordLists=new ArrayList<>();
        coordLists.add(coordList);

        String token = sharedPreferences.getString("token","");
        StopPointListRequest request=new StopPointListRequest();
        request.setHasOneCoordinate(false);
        request.setCoordList(coordLists);
        Call<StopPointListResponse> call = userService.getStopPointsInArea(token,request);
        call.enqueue(new Callback<StopPointListResponse>() {
            @Override
            public void onResponse(Call<StopPointListResponse> call, Response<StopPointListResponse> response) {
                if(response.isSuccessful()){
                    AllStopPoints.addAll(response.body().getStopPoints());
                    SetStopPoints(response.body().getStopPoints());
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(MapsActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StopPointListResponse> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
            }
        });
    }
    private void SetStopPoints(List<StopPointViewObject> list){
        if(list==null)return;
        for(int i=0;i<list.size();i++){
            float lat=Float.parseFloat(list.get(i).getLat());
            float _long=Float.parseFloat(list.get(i).getLong());
            MarkerOptions markerOptions=new MarkerOptions().position(new LatLng(lat,_long)).title("");
            Marker marker=mMap.addMarker(markerOptions);
            marker.setTag(list.get(i).getId());
            SuggestedStopPoints.add(marker);
        }
    }

    private void GetTourStopPoints(int tourId){
        String token = sharedPreferences.getString("token","");
        Call<Tour> call=userService.getTourInfo(token,tourId);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    List<StopPointViewObject> list=response.body().getStopPoints();
                    for(int i=0;i<list.size();i++){
                        boolean check=false;
                        for(int j=0;j<SuggestedStopPoints.size();j++){
                            if((int)SuggestedStopPoints.get(j).getTag()==list.get(i).getServiceId()
                            && !check){
                                TourStopPoints.add(SuggestedStopPoints.get(j));
                                check=true;
                            }
                        }
                        if(!check){
                            float lat=Float.parseFloat(list.get(i).getLat());
                            float _long=Float.parseFloat(list.get(i).getLong());

                            StringBuilder builder=new StringBuilder();
                            Calendar calendar=Calendar.getInstance();
                            try{
                                calendar.setTimeInMillis(Long.parseLong(list.get(i).getArrivalAt()));
                                builder.append(df.format(calendar.getTime()));
                                builder.append("-");
                                calendar.setTimeInMillis(Long.parseLong(list.get(i).getLeaveAt()));
                                builder.append(df.format(calendar.getTime()));
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            MarkerOptions markerOptions=new MarkerOptions()
                                    .position(new LatLng(lat,_long)).title(builder.toString());
                            Marker marker=mMap.addMarker(markerOptions);
                            marker.setTag(list.get(i).getServiceId());
                            TourStopPoints.add(marker);
                            AllStopPoints.add(list.get(i));
                        }
                    }
                }
                else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(MapsActivity.this, message.getMessage()
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
    private void SetUISettings(){
        uiSettings=mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        OnReceiveLatLng(new LatLng(location.getLatitude(),location.getLongitude()),true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void ReturnToCreateTourActivity(){
        intent=new Intent();
        intent.putExtra("action", actionType);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void ViewClickedStopPoint(int id,String status,String action,String date){
        Intent intent = new Intent(this, ViewStopPointActivity.class);
        intent.putExtra("status",status);
        intent.putExtra("Id",id);
        intent.putExtra("action",action);
        intent.putExtra("date",date);
        startActivityForResult(intent, STOP_POINT_ACTIVITY_REQUEST_CODE);
    }
    private void EditOrAddNewStopPoint(StopPointObj obj,String action,int index){
        Intent intent = new Intent(this, EditStopPointActivity.class);
        intent.putExtra("name",obj.getName());
        intent.putExtra("action",action);
        intent.putExtra("startDate",obj.getArrivalAt());
        intent.putExtra("endDate",obj.getLeaveAt());
        intent.putExtra("minCost",obj.getMinCost());
        intent.putExtra("maxCost",obj.getMaxCost());
        intent.putExtra("serviceType",obj.getServiceTypeId());
        intent.putExtra("address",obj.getAddress());
        intent.putExtra("province",obj.getProvinceId());
        intent.putExtra("lat",obj.getLat());
        intent.putExtra("long",obj.getLong());
        intent.putExtra("index",index);

        startActivityForResult(intent, STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE);
    }

    public void addMarker(LatLng p){

        if(SearchedPoint!=null)SearchedPoint.remove();
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(p);
        markerOptions.title("Selected Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        SearchedPoint=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void SetHintAutoComplete(){
        Integer size=sharedPreferences.getInt("HintSize",0);
        Hints.clear();
        _hints=new String[size];
        for(Integer i=0;i<size;i++){
            _hints[i]=sharedPreferences.getString(i.toString()+"Hint","");
            Hints.add(_hints[i]);
        }
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,
                R.layout.map_search_hint,_hints);
        searchAutoComplete.setAdapter(adapter);
    }

    public void onSearchSubmit(String query) {
        List<Address> gotAddresses = null;
        try {
            gotAddresses = geo.getFromLocationName(query, 1);
            Address address = (Address) gotAddresses.get(0);
            addMarker(new LatLng(address.getLatitude(), address.getLongitude()));
            imm.hideSoftInputFromWindow(searchAutoComplete.getWindowToken(),0);
            if(!Hints.contains(query)){
                Hints.add(0,query);
                for(Integer i=0;i<Hints.size();i++){
                    if(Hints.size()>1){
                        editor.remove(i.toString()+"Hint");
                        editor.apply();
                    }
                    editor.putString(i.toString()+"Hint",Hints.get(i));
                    editor.apply();
                }
                editor.putInt("HintSize",Hints.size()>20?20:Hints.size());
                editor.apply();
            }
            Address=query;
            SetHintAutoComplete();
            searchAutoComplete.dismissDropDown();
            searchAutoComplete.clearFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STOP_POINT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                Bundle bundle = data.getExtras();
                String userAction="";
                int service=-1;
                String date="";
                if (bundle != null) {
                    userAction = bundle.getString("action");
                    service=bundle.getInt("Id",-1);
                    date=bundle.getString("date","");
                }
                if(userAction.compareTo("removed")==0){
                    for(int i=0;i<TourStopPoints.size();i++)
                        if((int)TourStopPoints.get(i).getTag()==service){
                            TourStopPoints.get(i).setTitle("");
                            TourStopPoints.remove(i);
                            break;
                        }
                }
                else{
                    boolean check=false;
                    for(int i=0;i<TourStopPoints.size();i++)
                        if((int)TourStopPoints.get(i).getTag()==service){
                            TourStopPoints.get(i).setTitle(date);
                            check=true;
                            break;
                        }
                    if(!check)
                        for(int i=0;i<SuggestedStopPoints.size();i++)
                            if((int)SuggestedStopPoints.get(i).getTag()==service){
                                SuggestedStopPoints.get(i).setTitle(date);
                                TourStopPoints.add(SuggestedStopPoints.get(i));
                                break;
                            }
                }
            }
        }
        if (requestCode == STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                int index=-1;
                if (bundle != null) {
                    index=bundle.getInt("index",-1);
                    if(index!=-1){
                        StopPointsToAdd.get(index).setName(bundle.getString("name",""));
                        StopPointsToAdd.get(index).setArrivalAt(bundle.getLong("startDate",0));
                        StopPointsToAdd.get(index).setLeaveAt(bundle.getLong("endDate",0));
                        StopPointsToAdd.get(index).setAddress(bundle.getString("address",""));
                        StopPointsToAdd.get(index).setMinCost(bundle.getInt("minCost",0));
                        StopPointsToAdd.get(index).setMaxCost(bundle.getInt("maxCost",0));
                        StopPointsToAdd.get(index).setServiceTypeId(bundle.getInt("serviceType",4));
                        StopPointsToAdd.get(index).setProvinceId(bundle.getInt("province",1));
                    }
                    else{
                        StopPointObj obj=new StopPointObj();
                        obj.setName(bundle.getString("name",""));
                        obj.setArrivalAt(bundle.getLong("startDate",0));
                        obj.setLeaveAt(bundle.getLong("endDate",0));
                        obj.setAddress(bundle.getString("address",""));
                        obj.setMinCost(bundle.getInt("minCost",0));
                        obj.setMaxCost(bundle.getInt("maxCost",0));
                        obj.setServiceTypeId(bundle.getInt("serviceType",4));
                        obj.setProvinceId(bundle.getInt("province",1));
                        obj.setLat((double)bundle.getFloat("lat",0));
                        obj.setLong((double)bundle.getFloat("long",0));
                        StopPointsToAdd.add(obj);
                        SearchedPoint.remove();
                        mMap.addMarker(new MarkerOptions()
                                .title(obj.getName())
                                .position(new LatLng(bundle.getFloat("lat",0),bundle.getFloat("long",0))));

                    }
                }
            }
        }
    }

    private void ConvertSourceDesToStopPoints(){
        StopPointObj source=new StopPointObj();
        source.setAddress(sharedPreferences.getString("sourceAddress",""));
        source.setName("Source Point");
        source.setArrivalAt(sharedPreferences.getLong("startDate",0));
        source.setLeaveAt(source.getArrivalAt());
        source.setLat((double)sharedPreferences.getFloat("sourceLat",0));
        source.setLong((double)sharedPreferences.getFloat("sourceLong",0));
        source.setServiceTypeId(4);
        source.setProvinceId(1);
        source.setMinCost(0);
        source.setMaxCost(0);
        StopPointsToAdd.add(source);
        StopPointObj des=new StopPointObj();
        des.setAddress(sharedPreferences.getString("desAddress",""));
        des.setName("Destinationp");
        des.setArrivalAt(sharedPreferences.getLong("endDate",0));
        des.setLeaveAt(source.getArrivalAt());
        des.setLat((double)sharedPreferences.getFloat("desLat",0));
        des.setLong((double)sharedPreferences.getFloat("desLong",0));
        des.setServiceTypeId(4);
        des.setProvinceId(1);
        des.setMinCost(0);
        des.setMaxCost(0);
        StopPointsToAdd.add(des);
    }

    private void ConvertPickedMarkersToStopPoints(){
        for(int i=0;i<TourStopPoints.size();i++){
            int ID=(int)TourStopPoints.get(i).getTag();
            String[]tokens=TourStopPoints.get(i).getTitle().split("-");
            df=new SimpleDateFormat("dd/MM/yyyy");
            long startDate=0,endDate=0;
            try {
                Date d=df.parse(tokens[0]);
                startDate=d.getTime();
                d=df.parse(tokens[1]);
                endDate=d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int j=0;j<AllStopPoints.size();j++){
                if(AllStopPoints.get(j).getId()==ID){
                    StopPointObj tempObj=new StopPointObj();
                    tempObj.setName(AllStopPoints.get(j).getName());
                    tempObj.setAddress(AllStopPoints.get(j).getAddress());
                    tempObj.setLat(Double.parseDouble(AllStopPoints.get(j).getLat()));
                    tempObj.setLong(Double.parseDouble(AllStopPoints.get(j).getLong()));
                    tempObj.setServiceId(ID);
                    tempObj.setArrivalAt(startDate);
                    tempObj.setLeaveAt(endDate);
                    tempObj.setMinCost(Integer.parseInt(AllStopPoints.get(j).getMinCost()));
                    tempObj.setMaxCost(Integer.parseInt(AllStopPoints.get(j).getMaxCost()));
                    tempObj.setServiceTypeId(AllStopPoints.get(j).getServiceTypeId());
                    tempObj.setProvinceId(AllStopPoints.get(j).getProvinceId());
                    StopPointsToAdd.add(tempObj);
                }
            }
        }
    }
    private void AddStopPointsToTour(){
        String token = sharedPreferences.getString("token","");
        AddStopPointRequest mRequest=new AddStopPointRequest();
        mRequest.setTourId(tourId);
        mRequest.setDeleteIds(null);
        mRequest.setStopPoints(StopPointsToAdd);
        Call<Message> call=userService.addStopPointsToTour(token,mRequest);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    editor=sharedPreferences.edit();
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
                    Intent myIntent = new Intent(MapsActivity.this, FrontPage.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(myIntent);
                }else{
                    Gson gson = new Gson();
                    Message message=
                            gson.fromJson(response.errorBody().charStream(), Message.class);
                    Toast.makeText(MapsActivity.this, message.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(MapsActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                call.cancel();
            }
        });
    }
}
