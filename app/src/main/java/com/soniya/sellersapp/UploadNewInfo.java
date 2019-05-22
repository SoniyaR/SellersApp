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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
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


    PlacesClient placesClient;
    String query;

    AutocompleteSessionToken token;
    RectangularBounds bounds;

    FindAutocompletePredictionsRequest.Builder requestBuilder;
    FindAutocompletePredictionsRequest request;

    int AUTOCOMPLETE_REQUEST_CODE = 1;

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

        // Initialize Places.
       // Places.initialize(getApplicationContext(), "AIzaSyAoaTpL3mpT9gBtJB1DlUF9NYoAR90ssB4");

// Create a new Places client instance.
        /*placesClient = Places.createClient(this);

        query = "";

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        bounds = RectangularBounds.newInstance(
                new LatLng(9.0000, 69.150373),
                new LatLng(9.651869, 78.786150));

        requestBuilder = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                //.setCountry("in")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token);*/


        locationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                query = "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*query = locationText.getText().toString();
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
*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

           /* case R.id.autoCompleteTextView:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                break;
*/
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
