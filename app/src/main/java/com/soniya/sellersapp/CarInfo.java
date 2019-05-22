package com.soniya.sellersapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarInfo implements Serializable {

    //vehicle_no	model_name	availability description	location	sellingprice
    private String vehicle_no;
    private String model_name;
    private String availability;
    private String description;
    private String location;
    private String sellingprice;
    private String imgUrl;
    private ArrayList<String> image_uri_list;
    private DatabaseReference carInfoReference;
    FirebaseAdapter fbadapter;
    ArrayList<String> activeOrders;
    String uname = "";

   // List<String> myVehicleNumbers;

    public interface CarInfoListener    {
        public void onDataRetrieved(ArrayList<CarInfo> data);
        public void onProgress();
    }

    public interface CarNumbersListener {
        public void onRetrieve(ArrayList<String> data);
        public void onProgress();
    }

    public CarInfoListener carInfoListener;
    public CarNumbersListener carNumbersListener;

    public  CarInfo() {
        carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
        fbadapter = new FirebaseAdapter();
        uname = fbadapter.getCurrentUser();
       // this.myVehicleNumbers = retrieveMyVehicleNumbers();
        this.carInfoListener = null;
        this.carNumbersListener = null;
    }
    public void setCarInfoListener(CarInfoListener listener)    {
        carInfoListener = listener;

        retriveCarList();
    }

    public void setCarNumbersListener(CarNumbersListener listener){
        carNumbersListener = listener;

        retrieveMyVehicleNumbers();
    }

    public CarInfo(String vehicle_no, String model_name, String availability, String location, String sellingprice, String description){
        this.availability = availability;
        this.description = description;
        this.location = location;
        this.model_name = model_name;
        this.sellingprice = sellingprice;
        this.vehicle_no = vehicle_no;
    }
/*
    public CarInfo(String vehicle_no, String model_name, String availability, String location, String sellingprice){
        this.availability = availability;
        this.location = location;
        this.model_name = model_name;
        this.sellingprice = sellingprice;
        this.vehicle_no = vehicle_no;
    }*/

//    public String getImgUrl() {
//        return imgUrl;
//    }
//
//    public void setImgUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }


    public ArrayList<String> getImage_uri_list() {
        return image_uri_list;
    }

    public void setImage_uri_list(ArrayList<String> image_uri_list) {
        this.image_uri_list = image_uri_list;
    }

//    public List<String> getMyVehicleNumbers() {
//        return myVehicleNumbers;
//    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(String sellingprice) {
        this.sellingprice = sellingprice;
    }


    /*
    to retrieve cars list (active orders) for current user
     */

    private void retriveCarList() {

        ArrayList<CarInfo> carsArraylist = new ArrayList<>();

        carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
                    carsArraylist.clear();
                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {

                        //if (activeOrders.contains(carinfo.getKey().toString())) {
                            CarInfo carInfoObj = (CarInfo) carinfo.getValue(CarInfo.class);

                        if(carInfoListener !=null){
                            carInfoListener.onProgress();
                        }
                            if (carInfoObj != null) {
                                carsArraylist.add(carInfoObj);
                                //Log.i("soni-", "retrieved carinfo object\n" + carInfoObj.getModel_name());
                            }

                        //}
                    }

                }else{
                    Log.i("soni-", "no data found in CarsInfoDup");
                }

                if(carInfoListener !=null){
                    carInfoListener.onDataRetrieved(carsArraylist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public List retrieveMyVehicleNumbers()   {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");
        activeOrders = null;

        userRef.child(encodeString(uname)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //activeOrders.clear();
                if(dataSnapshot !=null && dataSnapshot.hasChild("ownerof")) {
                    if(dataSnapshot.child("ownerof").getValue() instanceof List) {
                        activeOrders = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                        //Log.i("soni-", "retrieveMyVehicleNumbers - it is a List");
                        if(carNumbersListener !=null)   {
                            carNumbersListener.onProgress();
                        }

                    }else if(dataSnapshot.child("ownerof").getValue() instanceof HashMap){
                        HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.child("ownerof").getValue();
                        //Log.i("soni-", "retrieveMyVehicleNumbers - it is a hashmap " + hashMap.keySet().toString() );
                        if(hashMap.keySet().size() == 1)    {
                            for(String hmkey : hashMap.keySet()) {
                                activeOrders = (ArrayList<String>) hashMap.get(hmkey);
                            }
                        }
                        if(carNumbersListener !=null)   {
                            carNumbersListener.onProgress();
                        }
                    }
                    if (activeOrders.size() > 0) {
                        int index = -1;
                        for (String order : (List<String>)activeOrders) {
                            if (order == null) {
                                index = activeOrders.indexOf(order);
                                activeOrders.remove(index);
                            }
                        }


                    }

                    if(carNumbersListener !=null)   {
                        carNumbersListener.onRetrieve(activeOrders);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return activeOrders;
    }
}
