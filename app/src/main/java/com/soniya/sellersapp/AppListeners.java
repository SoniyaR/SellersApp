package com.soniya.sellersapp;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AppListeners {

    String uname = "";
    private DatabaseReference carInfoReference;
    FirebaseDataFactory dataFactory;
    FirebaseAdapter fbadapter;

    public interface CarInfoRetrieveListener    {

        public void onDataRetrieved(ArrayList<CarInfo> data);
        public void onProgress();
        public void onRetrieveFailed(String error);
    }

    public interface CarNumbersListener {

        public void onRetrieve(ArrayList<String> data, ArrayList<String> paidforCars);
        public void onProgress();
    }

    public CarInfoRetrieveListener carInfoListener;
    public CarNumbersListener carNumbersListener;

    public AppListeners() {
        fbadapter = new FirebaseAdapter();
        this.uname = fbadapter.getCurrentUser();
        this.carInfoListener = null;
        this.carNumbersListener = null;
        dataFactory = new FirebaseDataFactory();
    }

    public void setCarInfoRetrieveListener(CarInfoRetrieveListener listener)    {
        carInfoListener = listener;
        dataFactory.retriveCarList(carInfoListener);
    }

    public void setCarNumbersListener(CarNumbersListener listener){
        carNumbersListener = listener;
        dataFactory.getactiveorders_List(carNumbersListener);
    }

    //uploading data into database one object of Carinfo at a time

    String vehicleNum;
    ArrayList<String> activeOrders;
    String resultOk = "OK";

    public interface CarInfoUploadListener  {
        public void onUploadComplete(String result);
        // public void onUploadFail(String Error);
    }

    public CarInfoUploadListener carInfoUploadListener;

    public AppListeners(String vehicleNum, ArrayList<String> active_orders)    {
        carInfoUploadListener = null;
        this.vehicleNum = vehicleNum;
        this.activeOrders = active_orders;
        dataFactory = new FirebaseDataFactory();
    }

    public void setCarInfoUploadListener( CarInfo carInfoObject, CarInfoUploadListener listener) {

        carInfoUploadListener = listener;
        dataFactory.uploadData(carInfoObject, vehicleNum, activeOrders, listener);
    }

}
