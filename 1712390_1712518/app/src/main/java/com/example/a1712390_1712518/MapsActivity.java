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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

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
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    Geocoder geo;
    AutoCompleteTextView searchAutoComplete;
    Button submitBtn;
    Button searchBtn;
    private UiSettings uiSettings;
    private String actionType;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent intent;
    private Marker Source,Destination;
    private ArrayList<Marker> StopPoints;
    private Marker SearchedPoint;
    ArrayList<String>Hints;
    String[] _hints;
    InputMethodManager imm;

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
        }
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionType.compareTo("StartPoint")==0
                        || actionType.compareTo("EndPoint")==0){
                    intent=new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
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
                OnReceiveLatLng(latLng);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTitle().compareTo("Selected Location")==0){
                    LatLng latLng=marker.getPosition();
                    OnReceiveLatLng(latLng);
                }
                return false;
            }
        });
        enableMyLocation();
    }

    private void OnReceiveLatLng(LatLng latLng){
        if(actionType.compareTo("StartPoint")==0){
            editor.putFloat("sourceLat",(float)latLng.latitude);
            editor.putFloat("sourceLong",(float)latLng.longitude);
            editor.putBoolean("sourceExisted",true);
            editor.apply();
            ReturnToCreateTourActivity();
        }
        if(actionType.compareTo("EndPoint")==0){
            editor.putFloat("desLat",(float)latLng.latitude);
            editor.putFloat("desLong",(float)latLng.longitude);
            editor.putBoolean("desExisted",true);
            editor.apply();
            ReturnToCreateTourActivity();
        }
        addMarker(latLng);
    }
    private void InitMap(){
        Boolean sourceExisted=sharedPreferences.getBoolean("sourceExisted",false);
        Boolean desExisted=sharedPreferences.getBoolean("desExisted",false);
        Integer stopPointCount=sharedPreferences.getInt("stopPointCount",0);
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
    }
    private void SetStopPoints(Integer count){
        if(count<=0)return;
        StopPoints=new ArrayList<Marker>();
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
        OnReceiveLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
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

    public boolean onSearchSubmit(String query) {
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

            SetHintAutoComplete();
            searchAutoComplete.dismissDropDown();
            searchAutoComplete.clearFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
