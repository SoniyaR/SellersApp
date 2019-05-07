package com.soniya.sellersapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UploadNewInfo extends AppCompatActivity implements View.OnClickListener {

    TextView descriptionView;
    Button saveButton;
    TextView titleText;
    TextView sellingpriceView;
    HashMap<String, Object> infoHashmap = new HashMap<>();
    TextView vehicleNum;
    TextView locationText;


    LocationManager locationManager;
    LocationListener listener;
    Location oldLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationText.setText(new LocationAdapter(getApplicationContext(), oldLocation).getAddress());
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_info);
        setTitle("Upload New Information");

        infoHashmap.clear();

        titleText =findViewById(R.id.titleTextView);

        descriptionView = findViewById(R.id.descriptionView);
        descriptionView.setOnClickListener(this);

        sellingpriceView = findViewById(R.id.sellingpriceView);

        vehicleNum = findViewById(R.id.numberText);
        vehicleNum.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        locationText = findViewById(R.id.locationText);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        //get current location and autofill in location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(oldLocation == null || (oldLocation !=null &&
                        (location.getLatitude() != oldLocation.getLatitude() || location.getLongitude() != oldLocation.getLongitude()))) {
                    oldLocation = location;
                    Log.i("soni-", "in onLocationChanged");
                    locationText.setText(new LocationAdapter(getApplicationContext(), location).getAddress());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else  {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationText.setText(new LocationAdapter(this, oldLocation).getAddress());
        }


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.saveButton:

                //save editDescriptionView.gettext().toString()  to database

                    if(titleText.getText().length() == 0 || titleText.getText().toString().isEmpty()){
                        titleText.setError("This field cannot be blank!");
                    }/*else if(descriptionView.getText().length() == 0 || descriptionView.getText().toString().isEmpty()){
                        descriptionView.setError("This field cannot be blank!");
                    }*/else if(sellingpriceView.getText().length() == 0 || sellingpriceView.getText().toString().isEmpty()){
                        sellingpriceView.setError("This field cannot be blank!");
                    }else if(vehicleNum.getText().length() == 0 ){
                        vehicleNum.setError("This field cannot be blank!");
                    }else if(locationText.getText().length() == 0){
                        locationText.setError("This field cannot be blank!");
                    }else {
                            //saveInfo(titleText.getText().toString(), img, descriptionView.getText().toString(), sellingpriceView.getText().toString());
                            prepareHashmapData(infoHashmap);

                            Intent nextInfo = new Intent(getApplicationContext(), UploadNewInfo2.class);
                            nextInfo.putExtra("infoHashmap", infoHashmap);
                            startActivity(nextInfo);

                    }
                break;

            case R.id.backgoundLayout:
            case R.id.titleView:
            case R.id.locationView:
            case R.id.vehiclenum:
            case R.id.rupeeimg:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;


            default:
                break;
        }

    }

    //vehicle_no	model_name	availability description	location	sellingprice
    private void prepareHashmapData(HashMap<String, Object> infoHashmap) {

        infoHashmap.put("vehicle_no", vehicleNum.getText().toString());
        infoHashmap.put("model_name", titleText.getText().toString());
        infoHashmap.put("availability", "Available");
        infoHashmap.put("description", descriptionView.getText().toString());
        infoHashmap.put("location", locationText.getText().toString());
        infoHashmap.put("sellingprice", sellingpriceView.getText().toString());

    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       *//* if(item.getItemId() == android.R.id.home)   {
            Log.i("soni-back", " arrow pressed");
            return true;
        }*//*
        return super.onOptionsItemSelected(item);
    }*/
}
