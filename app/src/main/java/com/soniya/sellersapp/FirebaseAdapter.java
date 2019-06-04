package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseAdapter {

    private FirebaseAuth mAuth;

    boolean signinUserSuccessful=false;
    boolean signupUserSuccessful = false;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage = "";


    public FirebaseAdapter(){
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    signinUserSuccessful = true;
                }else{
                    signinUserSuccessful = false;
                    setErrorMessage(task.getException().getMessage());
                }
            }
        });

        return signinUserSuccessful;
    }

    /*
    returns true if current user exists (i.e. logged in)
     */
    public boolean checkCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null && user.isEmailVerified()) {
            return true;
        }
        return false;
    }

    /*
    return current user (String)
     */
    public String getCurrentUser()  {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null) {
            if(user.getDisplayName()!=null && !user.getDisplayName().isEmpty()) {
                return user.getDisplayName();
            }else{
                return user.getEmail().split("@")[0];
            }
        }
        return "Not known";
    }

    public FirebaseUser getFirebaseUser()
    {
        return mAuth.getCurrentUser();
    }

    public boolean signupUser(Activity context, String email, String password)  {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    signupUserSuccessful = true;
                    Log.i("soni-adapterclass", "Signup done!");
                }else{
                    signupUserSuccessful = false;
                    setErrorMessage(task.getException().getMessage());
                   // Log.i("soni-signup error", getErrorMessage());
                }

            }
        });

        return signupUserSuccessful;
    }

    public void logoutUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null) {
            mAuth.signOut();
        }
    }

    /*
    convert CarInfo object List to CarInfoSerial Object List
     */
    public ArrayList<CarInfoSerial> buildInfoSerializable(ArrayList<CarInfo> data) {
        //there are total 15 features to be set to CarInfo/CarInfoSerial object

        ArrayList<CarInfoSerial> carsArrayList = new ArrayList<>();
        CarInfoSerial infoSerial;
        for(CarInfo info:data){
            infoSerial = new CarInfoSerial(info.getBrand_name(), info.getVehicle_no(), info.getModel_name(),
                    info.getAvailability(), info.getLocation(), info.getSellingprice(),
                    info.getImage_uri_list());

            infoSerial.setColor(info.getColor());
            infoSerial.setFuelType(info.getFuelType());
            infoSerial.setYearManufacturing(info.getYear());
            infoSerial.setInsurance(info.getInsurance());
            infoSerial.setKmsDriven(info.getKmsDriven());
            infoSerial.setOwner(info.getOwner());
            infoSerial.setTransmission(info.getTransmission());
            infoSerial.setDescription(info.getDescription());
            infoSerial.setThumbnailUriString(info.getThumbnailUriString());

            carsArrayList.add(infoSerial);
        }
        return carsArrayList;
    }

    /*
    convert CarInfoSerial obj list to CarInfo obj list
     */

    public ArrayList<CarInfo> buildCarInfoList(ArrayList<CarInfoSerial> data)   {
        ArrayList<CarInfo> carsArrayList = new ArrayList<>();
        CarInfo info;
        for(CarInfoSerial infoserial:data){

            //there are total 15 features to be set to CarInfo/CarInfoSerial object
            info = new CarInfo(infoserial.getBrand_name(), infoserial.getVehicle_no(), infoserial.getModel_name(),
                    infoserial.getAvailability(), infoserial.getLocation(), infoserial.getSellingprice(), infoserial.getImage_uri_list());

            info.setFuelType(infoserial.getFuelType());
            info.setColor(infoserial.getColor());
            info.setYear(infoserial.getYearManufacturing());
            info.setInsurance(infoserial.getInsurance());
            info.setKmsDriven(infoserial.getKmsDriven());
            info.setOwner(infoserial.getOwner());
            info.setTransmission(infoserial.getTransmission());
            info.setDescription(infoserial.getDescription());
            info.setThumbnailUriString(infoserial.getThumbnailUriString());
            carsArrayList.add(info);
        }
        return carsArrayList;
    }

}
