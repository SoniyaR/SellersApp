package com.soniya.sellersapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;

import java.util.HashMap;

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

    TextView locationEditText;


    //PlacesClient placesClient;
    String query;

//    AutocompleteSessionToken token;
//    RectangularBounds bounds;
//
//    FindAutocompletePredictionsRequest.Builder requestBuilder;
//    FindAutocompletePredictionsRequest request;

    int AUTOCOMPLETE_REQUEST = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(oldLocation !=null) {
                    locationText.setText(new LocationAdapter(getApplicationContext(), oldLocation).getAddress());
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
        locationText = findViewById(R.id.locationText);

        locationEditText = findViewById(R.id.locationEditText);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        //get current location and autofill in location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (oldLocation == null || (oldLocation != null &&
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(oldLocation !=null) {
                locationText.setText(new LocationAdapter(this, oldLocation).getAddress());
            }
        }

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

            case R.id.locationEditText:

                Log.i("soni-", "clicked on locationEditText");
               /* PlaceOptions placeOptions = new PlaceOptions.Builder()
                        .toolbarColor(Color.parseColor("#EEEEEE"))
                        .limit(7)
                        .country("IN")
                        .build();

                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken("pk.eyJ1Ijoic29uaXlhc2FjaGluIiwiYSI6ImNqdnpkZjB1bzBuYXg0NG1xaHBnNGFnZjgifQ.8lnZh4KFRMN9M2VAZOjhZQ")
                        .placeOptions(placeOptions)
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);*/

                //showAutocomplete();

                break;

            default:
                break;
        }

    }
/*

    */
/**
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST && resultCode == Activity.RESULT_OK) {
            */
/*CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();*//*


            Place place = PlaceAutocomplete.getPlace(this, data);
            locationEditText.setText(place.getName());
        }
    }
*/

    //vehicle_no	model_name	availability description	location	sellingprice
    private void prepareHashmapData(HashMap<String, Object> infoHashmap) {

        infoHashmap.put("vehicle_no", vehicleNum.getText().toString());
        infoHashmap.put("model_name", titleText.getText().toString());
        infoHashmap.put("availability", "Available");
        infoHashmap.put("description", descriptionView.getText().toString());
        infoHashmap.put("location", locationText.getText().toString());
        infoHashmap.put("sellingprice", sellingpriceView.getText().toString());

    }
}

