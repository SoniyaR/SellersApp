package com.soniya.sellersapp;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;

public class CarInfoSerial implements Serializable {

    //vehicle_no	model_name	availability description	location	sellingprice
    private String vehicle_no;
    private String model_name;
    private String availability;
    private String description;
    private String location;
    private String sellingprice;
    private ArrayList<String> image_uri_list;

    public CarInfoSerial(String vehicle_no, String model_name, String availability, String location, String sellingprice, String description, ArrayList<String> uri_list){
        this.availability = availability;
        this.description = description;
        this.location = location;
        this.model_name = model_name;
        this.sellingprice = sellingprice;
        this.vehicle_no = vehicle_no;
        this.image_uri_list = uri_list;
    }

    public ArrayList<String> getImage_uri_list() {
        return image_uri_list;
    }

    public void setImage_uri_list(ArrayList<String> image_uri_list) {
        this.image_uri_list = image_uri_list;
    }

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
Listener code below
 */

    String uname = "";
    private DatabaseReference carInfoReference;
    FirebaseDataFactory dataFactory;
    FirebaseAdapter fbadapter;

    public interface CarInfoListener    {

        public void onDataRetrieved(ArrayList<CarInfoSerial> data);
        public void onProgress();
        public void onRetrieveFailed();
    }

    public interface CarNumbersListener {

        public void onRetrieve(ArrayList<String> data);
        public void onProgress();
    }

    public CarInfoListener carInfoListener;
    public CarNumbersListener carNumbersListener;

    public CarInfoSerial() {
        fbadapter = new FirebaseAdapter();
        this.uname = fbadapter.getCurrentUser();
        this.carInfoListener = null;
        this.carNumbersListener = null;
        dataFactory = new FirebaseDataFactory();
    }
    public void setCarInfoListener(CarInfoListener listener)    {
        carInfoListener = listener;
        dataFactory.retriveCarList(carInfoListener);
    }

    public void setCarNumbersListener(CarNumbersListener listener){
        carNumbersListener = listener;
        dataFactory.retrieveMyVehicleNumbers(carNumbersListener);
    }




}
