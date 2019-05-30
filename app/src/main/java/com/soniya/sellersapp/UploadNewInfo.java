package com.soniya.sellersapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

import java.util.HashMap;

//import com.google.android.libraries.places.compat.Place;

public class UploadNewInfo extends AppCompatActivity implements View.OnClickListener {

    TextView descriptionView;
    Button saveButton;
    TextView titleText;
    TextView sellingpriceView;
    HashMap<String, Object> infoHashmap = new HashMap<>();
    TextView vehicleNum;
    //TextView locationText;
    Spinner fueltypeDropdown;
    TextView colorEdit;
    TextView yearEdit;

    String [] fueltypesArr;


    LocationManager locationManager;
    LocationListener listener;
    Location oldLocation;

    TextView locationEditText;


    //PlacesClient placesClient;
    String query;

//    AutocompleteSessionToken token;
//    RectangularBounds bounds;
//
//    FindAutocompletePredictionsRequest.Builder requestBuilder;
//    FindAutocompletePredictionsRequest request;

    int REQUEST_CODE_AUTOCOMPLETE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(oldLocation !=null) {
                    locationEditText.setText(new LocationAdapter(getApplicationContext(), oldLocation).getAddress());
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_info);
        setTitle("Upload New Information");

        infoHashmap.clear();

        titleText = findViewById(R.id.titleTextView);

        descriptionView = findViewById(R.id.descriptionView);
        descriptionView.setOnClickListener(this);

        sellingpriceView = findViewById(R.id.sellingpriceView);

        vehicleNum = findViewById(R.id.numberText);
        vehicleNum.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //locationText = findViewById(R.id.locationText);

        locationEditText = findViewById(R.id.locationEditText);

        colorEdit = findViewById(R.id.colorEditText);
        yearEdit = findViewById(R.id.yearManuf);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        fueltypeDropdown = findViewById(R.id.fueldropdown);
        //fueltype.setOnClickListener(this);
        fueltypesArr = new String[]{"Petrol", "Diesel", "Electric", "LPG"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fueltypesArr);
        fueltypeDropdown.setAdapter(spinnerAdapter);
        //

        //get current location and autofill in location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (oldLocation == null || (oldLocation != null &&
                        (location.getLatitude() != oldLocation.getLatitude() || location.getLongitude() != oldLocation.getLongitude()))) {
                    oldLocation = location;
                    Log.i("soni-", "in onLocationChanged");
                    locationEditText.setText(new LocationAdapter(getApplicationContext(), location).getAddress());
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

        getLocationPermission();


        /*locationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                query = "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                *//*query = locationText.getText().toString();
                //Log.i("soni-query=", query);
                // Use the builder to create a FindAutocompletePredictionsRequest.

                request = requestBuilder.setQuery(query).build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i("soni-", prediction.getPlaceId());
                        Log.i("soni-", prediction.getPrimaryText(null).toString());
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.i("soni-", "Place not found: " + apiException.getStatusCode());
                    }
                });
                *//*
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(oldLocation !=null) {
                locationEditText.setText(new LocationAdapter(this, oldLocation).getAddress());
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveButton:

                //save editDescriptionView.gettext().toString()  to database

                if (titleText.getText().length() == 0 || titleText.getText().toString().isEmpty()) {
                    titleText.setError("This field cannot be blank!");
                }/*else if(descriptionView.getText().length() == 0 || descriptionView.getText().toString().isEmpty()){
                        descriptionView.setError("This field cannot be blank!");
                    }*/ else if (sellingpriceView.getText().length() == 0 || sellingpriceView.getText().toString().isEmpty()) {
                    sellingpriceView.setError("This field cannot be blank!");
                } else if (vehicleNum.getText().length() == 0) {
                    vehicleNum.setError("This field cannot be blank!");
                }/*else if(locationText.getText().length() == 0){
                        locationText.setError("This field cannot be blank!");
                    }*/ else {
                    //saveInfo(titleText.getText().toString(), img, descriptionView.getText().toString(), sellingpriceView.getText().toString());

                    CarInfoSerial carinfoObject = buildCarInfoSerialObject();

                    Intent nextInfo = new Intent(getApplicationContext(), UploadNewInfo2.class);
                    nextInfo.putExtra("newcarinfo", carinfoObject);
                    startActivity(nextInfo);

                }
                break;

            case R.id.backgoundLayout:
            case R.id.titleView:
            case R.id.locationView:
            case R.id.rupeeimg:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;

            case R.id.locationEditText:

                Log.i("soni-", "clicked on locationEditText");
               /*PlacesOptions placeOptions = new PlacesOptions.Builder()
                        .toolbarColor(Color.parseColor("#EEEEEE"))
                        .limit(7)
                        .country("IN")
                        .build();

                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken("pk.eyJ1Ijoic29uaXlhc2FjaGluIiwiYSI6ImNqdnpkZjB1bzBuYXg0NG1xaHBnNGFnZjgifQ.8lnZh4KFRMN9M2VAZOjhZQ")
                        .placeOptions(placeOptions)
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);*/

                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

                //showAutocomplete();

                break;

            default:
                break;
        }

    }
/*
*//**
     * Shows the autocomplete activity.
     *//*

    private void showAutocomplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.i("soni-", " uploadinfo - e1 - " +  String.valueOf(e.getConnectionStatusCode()) + e.getMessage());
            //GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i("soni-", " uploadinfo - e2 - " +  e.getMessage());
            //showResponse(getString(R.string.google_play_services_error));
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == Activity.RESULT_OK) {

            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            locationEditText.setText(feature.text());
        }
    }


    //vehicle_no	model_name	availability description	location	sellingprice
    private CarInfoSerial buildCarInfoSerialObject() {

        //there are total 15 features to be set to CarInfo/CarInfoSerial object
        //out of which 14 to be set here (image uri on next page)

        CarInfoSerial carInfoSerial = new CarInfoSerial();

        carInfoSerial.setVehicle_no(vehicleNum.getText().toString());
        carInfoSerial.setModel_name(titleText.getText().toString());
        carInfoSerial.setSellingprice(sellingpriceView.getText().toString());
        carInfoSerial.setAvailability("Available");
        carInfoSerial.setLocation(locationEditText.getText().toString());
        carInfoSerial.setDescription(descriptionView.getText().toString());
        carInfoSerial.setFuelType(fueltypeDropdown.getSelectedItem().toString());
        carInfoSerial.setColor(colorEdit.getText().toString());
        carInfoSerial.setYearManufacturing(yearEdit.getText().toString());

        return carInfoSerial;

    }
}

