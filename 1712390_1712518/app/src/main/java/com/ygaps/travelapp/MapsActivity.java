package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.ygaps.travelapp.pojo.AddStopPointRequest;
import com.ygaps.travelapp.pojo.CoordList;
import com.ygaps.travelapp.pojo.CoordinateSet;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.StopPointListRequest;
import com.ygaps.travelapp.pojo.StopPointListResponse;
import com.ygaps.travelapp.pojo.StopPointObj;
import com.ygaps.travelapp.pojo.StopPointViewObject;
import com.ygaps.travelapp.pojo.Tour;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    Button hideButton;
    ProgressBar progressBar;
    private Boolean isHiding=false;
    private Boolean hasChanges=false;
    private UiSettings uiSettings;
    private String actionType;
    private int tourId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    private Marker Source,Destination;
    private ArrayList<Marker> SuggestedStopPoints;
    private ArrayList<Marker> TourStopPoints;
    private ArrayList<Marker> UnrelatedPoints;
    private ArrayList<Marker> UpdatedTourStopPoints;
    private ArrayList<StopPointViewObject> AllStopPoints;
    private List<Integer> deletedIds;
    private ArrayList<StopPointObj> StopPointsToAdd;
    private ArrayList<Integer>TourStopPointsId;
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
            tourId=-1;
            actionType = bundle.getString("action");
            if(actionType.compareTo("StopPoint")==0 || actionType.compareTo("View")==0
            || actionType.compareTo("EditTour")==0){
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
        UnrelatedPoints=new ArrayList<>();
        deletedIds=new ArrayList<>();
        TourStopPointsId=new ArrayList<>();
        UpdatedTourStopPoints=new ArrayList<>();
        df=new SimpleDateFormat("dd/MM/yyyy");
        progressBar=findViewById(R.id.map_progressBar);

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
                    ConvertMarkersToStopPoints(TourStopPoints);
                    AddStopPointsToTour();
                }
                if(actionType.compareTo("View")==0){
                    Intent mIntent=new Intent();
                    setResult(RESULT_OK,mIntent);
                    finish();
                }
                if(actionType.compareTo("ViewStopPoint")==0){
                    Intent mIntent=new Intent();
                    setResult(RESULT_OK,mIntent);
                    finish();
                }
                if(actionType.compareTo("EditTour")==0){
                    ConvertMarkersToStopPoints(UpdatedTourStopPoints);
                    AddStopPointsToTour();
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
        enableMyLocation();
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
                    if(actionType.compareTo("StopPoint")==0 || actionType.compareTo("EditTour")==0){
                        if(TourStopPoints.contains(marker))ViewClickedStopPoint((int)marker.getTag(),
                                "included","edit",marker.getTitle());
                        else ViewClickedStopPoint((int)marker.getTag(),
                                "not included","edit","");
                    }
                    if(actionType.compareTo("View")==0)ViewClickedStopPoint((int)marker.getTag(),
                            "","view",marker.getTitle());
                    if(actionType.compareTo("ViewStopPoint")==0)ViewClickedStopPoint((int)marker.getTag(),
                            "","stoppoint","");
                }
                return false;
            }
        });
        progressBar.setVisibility(View.GONE);
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
        if((actionType.compareTo("StopPoint")==0 || actionType.compareTo("EditTour")==0)&& isClickOnMarker){
            StopPointObj obj=new StopPointObj();
            obj.setAddress("");
            obj.setName("");
            obj.setArrivalAt(Long.parseLong("0"));
            obj.setLeaveAt(Long.parseLong("0"));
            obj.setLat(latLng.latitude);
            obj.setLong(latLng.longitude);
            obj.setServiceTypeId(4);
            obj.setProvinceId(1);
            obj.setMinCost(-1);
            obj.setMaxCost(-1);
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
            Source=mMap.addMarker(new MarkerOptions().position(sourcePos).title("Source")
                    .icon(getIcon(6))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sourcePos));
        }
        if(desExisted){
            Destination=mMap.addMarker(new MarkerOptions().position(desPos).title("Destination")
            .icon(getIcon(6)));
            if(!sourceExisted)mMap.moveCamera(CameraUpdateFactory.newLatLng(desPos));
        }
        if(!sourceExisted && !desExisted && (actionType.compareTo("StartPoint")==0
        || actionType.compareTo("EndPoint")==0)){
            LatLng loc=new LatLng((double)20,(double)110);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            return;
        }
        if(actionType.compareTo("StartPoint")==0
                || actionType.compareTo("EndPoint")==0)return;
        builder=new LatLngBounds.Builder();
        if(actionType.compareTo("StopPoint")==0){
            builder.include(sourcePos);
            builder.include(desPos);
            bounds=builder.build();
            CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds,0);
            mMap.animateCamera(cu);
            RequestStopPointList();
            ConvertSourceDesToStopPoints();
        }
        if(tourId!=-1){
            GetTourStopPoints(tourId);
        }
        if(actionType.compareTo("EditTour")==0 || actionType.compareTo("StopPoint")==0){
            hideButton=findViewById(R.id.map_marker_hide_btn);
            hideButton.setVisibility(View.VISIBLE);
            hideButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    if(hideButton.getText().toString().compareTo(getString(R.string.hide))==0){
                        hideButton.setText(getString(R.string.show));
                        isHiding=true;
                        for(int i=0;i<UnrelatedPoints.size();i++){
                            UnrelatedPoints.get(i).setVisible(false);
                        }
                    }
                    else{
                        hideButton.setText(getString(R.string.hide));
                        isHiding=false;
                        for(int i=0;i<UnrelatedPoints.size();i++){
                            UnrelatedPoints.get(i).setVisible(true);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        if(actionType.compareTo("ViewStopPoint")==0){
            builder.include(new LatLng(0,75));
            builder.include(new LatLng(60,150));
            bounds=builder.build();
            CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds,0);
            mMap.animateCamera(cu);
            RequestStopPointList();
        }
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
                    List<StopPointViewObject> list=response.body().getStopPoints();
                    List<StopPointViewObject> filteredList=new ArrayList<>();
                    for(int i=0;i<list.size();i++){
                        if(!TourStopPointsId.contains(list.get(i).getId())){
                            filteredList.add(list.get(i));
                        }
                    }
                    SetStopPoints(filteredList);
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
            MarkerOptions markerOptions=new MarkerOptions().position(new LatLng(lat,_long)).title("")
                    .icon(getIcon(list.get(i).getServiceTypeId()));
            Marker marker=mMap.addMarker(markerOptions);
            marker.setTag(list.get(i).getId());
            SuggestedStopPoints.add(marker);
            StopPointViewObject obj=list.get(i);
            obj.setServiceId(list.get(i).getId());
            obj.setId(null);
            AllStopPoints.add(obj);
            UnrelatedPoints.add(marker);
        }
    }

    private void GetTourStopPoints(int tourId){
        if(tourId==-1)return;
        String token = sharedPreferences.getString("token","");
        Call<Tour> call=userService.getTourInfo(token,tourId);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    List<StopPointViewObject> list=response.body().getStopPoints();
                    for(int i=0;i<list.size();i++){
                        double lat=Double.parseDouble(list.get(i).getLat());
                        double _long=Double.parseDouble(list.get(i).getLong());

                        StringBuilder sbuilder=new StringBuilder();
                        Calendar calendar=Calendar.getInstance();
                        try{
                            calendar.setTimeInMillis(Long.parseLong(list.get(i).getArrivalAt()));
                            sbuilder.append(df.format(calendar.getTime()));
                            sbuilder.append("-");
                            calendar.setTimeInMillis(Long.parseLong(list.get(i).getLeaveAt()));
                            sbuilder.append(df.format(calendar.getTime()));
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        MarkerOptions markerOptions=new MarkerOptions()
                                .position(new LatLng(lat,_long)).title(sbuilder.toString())
                                .icon(getIcon(list.get(i).getServiceTypeId()));
                        Marker marker=mMap.addMarker(markerOptions);
                        marker.setTag(list.get(i).getServiceId());
                        TourStopPointsId.add((int)marker.getTag());
                        TourStopPoints.add(marker);
                        AllStopPoints.add(list.get(i));
                        if(actionType.compareTo("View")==0 || actionType.compareTo("EditTour")==0){
                            builder.include(marker.getPosition());
                        }
                    }
                    if((actionType.compareTo("View")==0 || actionType.compareTo("EditTour")==0)){
                        if(list.size()==0){
                            builder.include(new LatLng((double)20,(double)110));
                            builder.include(new LatLng((double)25,(double)120));
                        }
                        bounds=builder.build();
                        CameraUpdate cu=CameraUpdateFactory.newLatLngBounds(bounds,0);
                        mMap.animateCamera(cu);
                        if(actionType.compareTo("EditTour")==0)RequestStopPointList();
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
        markerOptions.icon(getIcon(5));

        SearchedPoint=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
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
                            int tag=(int)TourStopPoints.get(i).getTag();
                            TourStopPoints.get(i).setTitle("");
                            if(actionType.compareTo("EditTour")==0 && isHiding){
                                TourStopPoints.get(i).setVisible(false);
                            }
                            UnrelatedPoints.add(TourStopPoints.get(i));
                            UpdatedTourStopPoints.remove(TourStopPoints.get(i));
                            TourStopPoints.remove(i);
                            for(int j=0;j<AllStopPoints.size();j++){
                                if(AllStopPoints.get(j).getServiceId()!=null){
                                    if(AllStopPoints.get(j).getServiceId()==tag)
                                        deletedIds.add(AllStopPoints.get(j).getId());
                                }
                            }
                            hasChanges=true;
                            break;
                        }
                }
                else{
                    boolean check=false;
                    hasChanges=true;
                    for(int i=0;i<TourStopPoints.size();i++)
                        if((int)TourStopPoints.get(i).getTag()==service){
                            if(!UpdatedTourStopPoints.contains(TourStopPoints.get(i))){
                                TourStopPoints.get(i).setTitle(date);
                                UpdatedTourStopPoints.add(TourStopPoints.get(i));
                            }
                            else {
                                UpdatedTourStopPoints
                                        .get(UpdatedTourStopPoints.indexOf(TourStopPoints.get(i)))
                                        .setTitle(date);
                                TourStopPoints.get(i).setTitle(date);
                            }
                            check=true;
                            break;
                        }
                    if(!check)
                        for(int i=0;i<SuggestedStopPoints.size();i++)
                            if((int)SuggestedStopPoints.get(i).getTag()==service){
                                SuggestedStopPoints.get(i).setTitle(date);
                                UpdatedTourStopPoints.add(SuggestedStopPoints.get(i));
                                TourStopPoints.add(SuggestedStopPoints.get(i));
                                UnrelatedPoints.remove(SuggestedStopPoints.get(i));
                                break;
                            }
                }
            }
        }
        if (requestCode == STOP_POINT_EDIT_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                hasChanges=true;
                int index=-1;
                if (bundle != null) {
                    index=bundle.getInt("index",-1);
                    if(index!=-1){
                        StopPointsToAdd.get(index).setName(bundle.getString("name",""));
                        StopPointsToAdd.get(index).setArrivalAt(bundle.getLong("startDate",0));
                        StopPointsToAdd.get(index).setLeaveAt(bundle.getLong("endDate",0));
                        StopPointsToAdd.get(index).setAddress(bundle.getString("address",""));
                        StopPointsToAdd.get(index).setMinCost(bundle.getInt("minCost",-1));
                        StopPointsToAdd.get(index).setMaxCost(bundle.getInt("maxCost",-1));
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
                        obj.setLat(bundle.getDouble("lat",0));
                        obj.setLong(bundle.getDouble("long",0));
                        StopPointsToAdd.add(obj);
                        SearchedPoint.remove();
                        Marker marker=mMap.addMarker(new MarkerOptions()
                                .title(obj.getName())
                                .position(new LatLng(obj.getLat(),obj.getLong()))
                                .icon(getIcon(obj.getServiceTypeId()))
                        );

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
        des.setLeaveAt(des.getArrivalAt());
        des.setLat((double)sharedPreferences.getFloat("desLat",0));
        des.setLong((double)sharedPreferences.getFloat("desLong",0));
        des.setServiceTypeId(4);
        des.setProvinceId(1);
        des.setMinCost(0);
        des.setMaxCost(0);
        StopPointsToAdd.add(des);
        hasChanges=true;
    }

    private void ConvertMarkersToStopPoints(List<Marker> list){
        for(int i=0;i<list.size();i++){
            int ID=(int)list.get(i).getTag();
            String[]tokens=list.get(i).getTitle().split("-");
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
                if(AllStopPoints.get(j).getServiceId()==ID){
                    StopPointObj tempObj=new StopPointObj();
                    if(AllStopPoints.get(j).getId()!=null){
                        tempObj.setId(AllStopPoints.get(j).getId());
                    }
                    tempObj.setName(AllStopPoints.get(j).getName());
                    tempObj.setAddress(AllStopPoints.get(j).getAddress());
                    tempObj.setLat(Double.parseDouble(AllStopPoints.get(j).getLat()));
                    tempObj.setLong(Double.parseDouble(AllStopPoints.get(j).getLong()));
                    tempObj.setServiceId(ID);
                    tempObj.setArrivalAt(startDate);
                    tempObj.setLeaveAt(endDate);
                    tempObj.setMinCost(parseInt(AllStopPoints.get(j).getMinCost()));
                    tempObj.setMaxCost(parseInt(AllStopPoints.get(j).getMaxCost()));
                    tempObj.setServiceTypeId(AllStopPoints.get(j).getServiceTypeId());
                    tempObj.setProvinceId(AllStopPoints.get(j).getProvinceId());
                    StopPointsToAdd.add(tempObj);
                    j=AllStopPoints.size()-1;
                }
            }
        }
    }
    private void AddStopPointsToTour(){
        if(!hasChanges){
            Intent myIntent;
            myIntent = new Intent(MapsActivity.this, TourInfo.class);
            myIntent.putExtra("tourId",tourId);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(myIntent);
            return;
        }
        String token = sharedPreferences.getString("token","");
        AddStopPointRequest mRequest=new AddStopPointRequest();
        mRequest.setTourId(tourId);
        if(deletedIds.size()==0)mRequest.setDeleteIds(null);
        else mRequest.setDeleteIds(deletedIds);
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
                    Intent myIntent;
                    myIntent = new Intent(MapsActivity.this, TourInfo.class);
                    myIntent.putExtra("tourId",tourId);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(myIntent);
                    return;
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

    private BitmapDescriptor getIcon(int serviceTypeId){
        switch (serviceTypeId){
            case 1:{
                return BitmapDescriptorFactory.fromResource(R.drawable.fast_food_placeholder);
            }
            case 2:{
                return BitmapDescriptorFactory.fromResource(R.drawable.location__1_);
            }
            case 3:{
                return BitmapDescriptorFactory.fromResource(R.drawable.coffee_shop_marker);
            }
            case 4:{
                return BitmapDescriptorFactory.fromResource(R.drawable.location__2_);
            }
            case 5:{
                return BitmapDescriptorFactory.fromResource(R.drawable.favorite);
            }
            default:{
                return BitmapDescriptorFactory.fromResource(R.drawable.location);
            }
        }
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
