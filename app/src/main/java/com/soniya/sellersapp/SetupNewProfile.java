package com.soniya.sellersapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SetupNewProfile extends AppCompatActivity implements View.OnClickListener {

    TextView newUsername;
    AutoCompleteTextView locationTextView;
    String currentEmailId = "";
    String currentPassword = "";
    TextView emailVerfi;
    TextView mobileText;
    Button nextButton;
    FirebaseAuth auth;

    LocationManager locationManager;
    LocationListener listener;
    Location oldLocation;

    UserInformation userInfo;
    /*PlacesClient placesClient;
    String query;

    AutocompleteSessionToken token;
    RectangularBounds bounds;

    FindAutocompletePredictionsRequest.Builder requestBuilder;
    FindAutocompletePredictionsRequest request;*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationTextView.setText(new LocationAdapter(getApplicationContext(), oldLocation).getAddress());
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_new_profile);

        setTitle("Set up your profile");

        auth = FirebaseAuth.getInstance();
        userInfo = new UserInformation();

        emailVerfi = findViewById(R.id.emailVerText);
        emailVerfi.setOnClickListener(this);

        nextButton = (Button)  findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        nextButton.setVisibility(View.INVISIBLE);

        mobileText = findViewById(R.id.editmobilenum);

        Intent intent = getIntent();
        if(intent.getExtras() !=null){
            if(intent.getStringExtra("emailId") !=null) {
                currentEmailId = intent.getStringExtra("emailId");
                userInfo.setEmailId(currentEmailId);
            }

            if(intent.getStringExtra("password") !=null)    {
                currentPassword = intent.getStringExtra("password");
            }
        }

        ConstraintLayout layout = findViewById(R.id.setprofLayout);
        layout.setOnClickListener(this);

        newUsername = findViewById(R.id.editUsername);
        locationTextView = findViewById(R.id.editLocation);

        //get current location and autofill in location

        /*locationTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //query = "";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                *//*query = locationTextView.getText().toString();
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
                });*//*

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        if(!currentEmailId.isEmpty() && currentEmailId.contains("@"))   {
            newUsername.setText(currentEmailId.split("@")[0]);
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(oldLocation == null || (oldLocation !=null &&
                        (location.getLatitude() != oldLocation.getLatitude() || location.getLongitude() != oldLocation.getLongitude()))) {

                    oldLocation = location;
                    String locinfo = new LocationAdapter(getApplicationContext(), location).getAddress();
                    Log.i("soni-", "in onLocationChanged - SetupProfile, " + locinfo);
                    locationTextView.setText(locinfo);
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
            locationTextView.setText(new LocationAdapter(getApplicationContext(), oldLocation).getAddress());
        }

        newUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newUsername.getText().toString().contains("@")) {
                    Toast.makeText(SetupNewProfile.this, "@ not allowed in username", Toast.LENGTH_SHORT).show();
                    newUsername.setError("Enter valid username");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void sendVerificationMail() {

        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //auth.signOut();
                            Toast.makeText(SetupNewProfile.this, "Email sent for verification.", Toast.LENGTH_SHORT).show();
                            Log.i("soni-", "Email sent for verification.");
                            nextButton.setVisibility(View.VISIBLE);
                            nextButton.setText("Proceed to Login");
                        }
                        else{
                            Log.i("soni-", "task not successful-  " +  task.getException().getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("soni-", "verification- " + e.getMessage());
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.nextButton:
                if(auth.getCurrentUser() !=null) {

                    String mobilenum = "";

                    if(mobilenum.length() != 10)    {
                        mobileText.setError("Enter valid mobile number");
                    }else{
                        mobilenum = mobileText.getText().toString();
                    }

                    if (newUsername.getText() != null && !newUsername.getText().toString().isEmpty() && newUsername.getError().toString().isEmpty()
                            && locationTextView.getText() != null && !locationTextView.getText().toString().isEmpty()
                            && !mobilenum.isEmpty() && mobileText.getError().toString().isEmpty() ){

                        if (!currentEmailId.equals("") && !currentEmailId.isEmpty()) {

                            //userInfo.setUsername(newUsername.getText().toString());
                            userInfo.setMobileNo(mobilenum);
                            userInfo.setLocation(locationTextView.getText().toString());

                            FirebaseDataFactory dataFactory = new FirebaseDataFactory();
                            dataFactory.addNewProfileInfo(newUsername.getText().toString(), userInfo);

                            if(auth.getCurrentUser().isEmailVerified()){
                                Intent i = new Intent(getApplicationContext(), HomePage.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else {
                                auth.signOut();
                                new AlertDialog.Builder(this)
                                        .setMessage("Login again after email verification.")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(i);
                                            }
                                        }).show();
                            }

                        } else {
                            Log.i("soni-", "Something went wrong! (In SetupNewProfile)");
                        }
                    }else if(locationTextView.getText().toString().isEmpty()){
                        locationTextView.setError("Location is mandatory.");
                    }
                    else if(newUsername.getText().toString().isEmpty()){
                        newUsername.setError("Username is mandatory.");
                    }
                }else{
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    //Toast.makeText(this, "Please Login Again!", Toast.LENGTH_SHORT).show();
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                break;

            case R.id.location:
                //find location suggestions




                break;

            case R.id.setprofLayout:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;

            case R.id.emailVerText:
                //signup call, which will change the auth state
                auth.createUserWithEmailAndPassword(currentEmailId, currentPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            Intent intent = new Intent(getApplicationContext(), SetupNewProfile.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.putExtra("emailId", currentEmailId);
//                            startActivity(intent);
                            sendVerificationMail();
                            Log.i("soni-setupnewprof", "Signup done!");
                        }else{
                            Log.i("soni-signup error", task.getException().getMessage());
                        }

                    }
                });

                break;

            default:
                break;
        }

    }
}
