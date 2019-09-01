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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.soniya.sellersapp.adapters.LocationAdapter;
import com.soniya.sellersapp.pojo.CarInfoSerial;


public class UploadNewInfo extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    TextView titleText;
    TextView sellingpriceView;
    TextView vehicleNum;
    Spinner fueltypeDropdown;
    Spinner ownerDropdown;
    Spinner transmDropdown;
    TextView colorEdit;
    TextView yearEdit;
    TextView brandName;
    RadioGroup insuranceCheck;
    TextView kmsDriven;
    TextView locationText;

    LocationManager locationManager;
    LocationListener listener;
    Location oldLocation;

    int REQUEST_CODE_AUTOCOMPLETE = 1;

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

        titleText = findViewById(R.id.titleTextView);
        sellingpriceView = findViewById(R.id.sellingpriceView);

        vehicleNum = findViewById(R.id.numberText);
        vehicleNum.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        locationText = findViewById(R.id.locationEditText);
        colorEdit = findViewById(R.id.colorEditText);
        yearEdit = findViewById(R.id.yearManuf);
        insuranceCheck = findViewById(R.id.insuranceRadioGrp);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        brandName = findViewById(R.id.brandTextEdit);
        kmsDriven = findViewById(R.id.kmsnumber);

        fueltypeDropdown = findViewById(R.id.fueldropdown);
        ArrayAdapter<String> fuelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Petrol", "Diesel", "Electric", "LPG"});
        fueltypeDropdown.setAdapter(fuelAdapter);

        ownerDropdown = findViewById(R.id.ownerdropdown);
        ArrayAdapter ownerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"First", "Second", "Third", "Fourth", "Fifth"});
        ownerDropdown.setAdapter(ownerAdapter);

        transmDropdown = findViewById(R.id.transmsndropdown);
        transmDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Manual", "Automatic"}));

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
                locationText.setText(new LocationAdapter(this, oldLocation).getAddress());
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveButton:

                boolean infoReady = false;

                if(titleText.getText().toString().isEmpty()) {
                    titleText.setError("This field cannot be blank!");
                }
                if(sellingpriceView.getText().toString().isEmpty()) {
                    sellingpriceView.setError("This field cannot be blank!");
                }
                if(vehicleNum.getText().toString().isEmpty()) {
                    vehicleNum.setError("This field cannot be blank!");
                }
                if(locationText.getText().toString().isEmpty()){
                    locationText.setError("This field cannot be blank!");
                }
                if(colorEdit.getText().toString().isEmpty())    {
                    colorEdit.setError("This field cannot be blank!");
                }
                if(yearEdit.getText().toString().isEmpty()) {
                    yearEdit.setError("This field cannot be blank!");
                }
                if(brandName.getText().toString().isEmpty())    {
                    brandName.setError("This field cannot be blank!");
                }
                if(kmsDriven.getText().toString().isEmpty())    {
                    kmsDriven.setError("This field cannot be blank!");
                }

                if(!titleText.getText().toString().isEmpty() && !sellingpriceView.getText().toString().isEmpty() && !vehicleNum.getText().toString().isEmpty()
                        && !locationText.getText().toString().isEmpty() && !colorEdit.getText().toString().isEmpty() && !yearEdit.getText().toString().isEmpty()
                        && !brandName.getText().toString().isEmpty() && !kmsDriven.getText().toString().isEmpty())  {
                    infoReady = true;
                }

                if(infoReady)   {

                    CarInfoSerial carinfoObject = buildCarInfoSerialObject();
                    Intent nextInfo = new Intent(getApplicationContext(), UploadNewInfo2.class);
                    nextInfo.putExtra("newcarinfo", carinfoObject);
                    startActivity(nextInfo);

                }
                else{
                    Toast.makeText(this, "Mandatory fields are empty!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.backgoundLayout:
            case R.id.titleView:
            case R.id.locationView:
            case R.id.rupeeimg:
            case R.id.insuranceRadioGrp:
            case R.id.insurancetxt:
            case R.id.transmsn:
            case R.id.kmstext:
            case R.id.ownertext:
            case R.id.scrollbackgoundLayout:
            case R.id.brandText:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;

            case R.id.locationEditText:

                Log.i("soni-", "clicked on locationEditText");

                /*Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);*/

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
//
//            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
//            locationText.setText(feature.text());
        }
    }


    private CarInfoSerial buildCarInfoSerialObject() {

        //there are total 15 features to be set to CarInfo/CarInfoSerial object
        //out of which 13 to be set here (image uri, description on next page)

        CarInfoSerial carInfoSerial = new CarInfoSerial();

        carInfoSerial.setBrand_name(brandName.getText().toString());
        carInfoSerial.setVehicle_no(vehicleNum.getText().toString());
        carInfoSerial.setModel_name(titleText.getText().toString());
        carInfoSerial.setSellingprice(sellingpriceView.getText().toString());
        carInfoSerial.setAvailability("Available");
        carInfoSerial.setLocation(locationText.getText().toString());
        carInfoSerial.setFuelType(fueltypeDropdown.getSelectedItem().toString());
        carInfoSerial.setColor(colorEdit.getText().toString());
        carInfoSerial.setYearManufacturing(yearEdit.getText().toString());
        carInfoSerial.setOwner(ownerDropdown.getSelectedItem().toString());
        carInfoSerial.setTransmission(transmDropdown.getSelectedItem().toString());
        carInfoSerial.setKmsDriven(kmsDriven.getText().toString());

        int selectedRadio = insuranceCheck.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadio);
        carInfoSerial.setInsurance(radioButton.getText().toString());

        return carInfoSerial;

    }
}

