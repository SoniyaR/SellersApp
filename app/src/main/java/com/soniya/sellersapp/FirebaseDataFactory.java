package com.soniya.sellersapp;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;
import com.soniya.sellersapp.pojo.ProfileStats;
import com.soniya.sellersapp.pojo.UserInformation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FirebaseDataFactory {

    private DatabaseReference db;
    char space = ' ';
    char replacechar = '_';
    DatabaseReference carInfoReference;

    ArrayList<String> activeorders_List = new ArrayList<>();
    ArrayList<String> soldorders_List = new ArrayList<>();

    String uname = null;
    String key = null;

    public FirebaseDataFactory() {
        uname = new FirebaseAdapter().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        carInfoReference = db.child("CarsInfo");
    }

    StorageReference img_ref = FirebaseStorage.getInstance().getReference().child(new FirebaseAdapter().getCurrentUser());

    public static String encodeString(String string) {
        if (string == null || (string != null && string.isEmpty())) {
            return "";
        }
        string = string.replace(".", ",");
        return string.replace(" ", "_");

    }

    public static String decodeString(String string) {
        if (string == null || (string != null && string.isEmpty())) {
            return "";
        }
        string = string.replace("_", " ");
        return string.replace(",", ".");
    }

    public List<HashMap<String, Object>> retrieveCarsList(ArrayList<String> vehicleNumList) {

        //ArrayList<String> vehicleNumList = getactiveorders();
        if (vehicleNumList.isEmpty()) {
            Log.i("soni-fbFactory", "vehicleNumList is empty");
        }

        List<HashMap<String, Object>> hmlist = new ArrayList<>();
        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmlist.clear();
                //Log.i("soni-dataSnapshot", dataSnapshot.getValue().toString());
                for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
                    //Log.i("soni-carinfo",carinfo.getKey().toString());
                    //activeOrders.add(carinfo.getKey().toString());
                    if (vehicleNumList.contains(carinfo.getKey())) {
                        Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                        HashMap<String, Object> hm = new HashMap<String, Object>();
                        hm.put("vehicle_no", carinfo.getKey().toString());
                        while (it.hasNext()) {
                            DataSnapshot ds = it.next();
                            //Log.i("soni-onstart",ds.getKey().toString() + " " + ds.getValue().toString());
                            hm.put(ds.getKey().toString(), ds.getValue().toString());
                        /*if(ds.getKey().equalsIgnoreCase("vehicle_no")) {
                        //activeOrders.add(ds.getValue().toString());
                    }*/
                        }
                        hmlist.add(hm);
                    }
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return hmlist;

    }

    ArrayList<String> paidforCars = new ArrayList<>();

    public ArrayList<String> getactiveorders_List(AppListeners.CarNumbersListener listener) {

        activeorders_List.clear();
        DatabaseReference cur_UserRef = db.child("userInfo").child(encodeString(uname));
        cur_UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    UserInformation userinfo = dataSnapshot.getValue(UserInformation.class);
                    activeorders_List = userinfo.getActiveOrders();
                    paidforCars = userinfo.getPaidforCarNumbers();

                    if (activeorders_List.isEmpty()) {
                        if (listener != null) {
                            listener.onProgress();
                        }
                        if (dataSnapshot.child("activeorders").getValue() instanceof List) {
                            Log.i("soni-", "activeorders is a List");
                            activeorders_List = (ArrayList<String>) dataSnapshot.child("activeorders").getValue();

                        } else if (dataSnapshot.child("activeorders").getValue() instanceof HashMap) {
                            Log.i("soni-", "activeorders is a Hashmap");
                            HashMap<String, String> hm = (HashMap<String, String>) dataSnapshot.child("activeorders").getValue();
                            Set<String> keys = ((HashMap) hm).keySet();
                            for (String key : keys) {
                                activeorders_List.add(hm.get(key));
                            }
                            Log.i("soni-", "getactiveordersList , its hashmap ownerlist size=" + String.valueOf(activeorders_List.size()));
                        } else if (dataSnapshot.child("activeorders").getValue() instanceof String) {
                            Log.i("soni-", "activeorders is a String");
                            activeorders_List.add(dataSnapshot.child("activeorders").getValue().toString());
                        } else {
                            Log.i("soni-", "not able to get activeordersList");
                        }
                    }

                    if (listener != null) {
                        listener.onRetrieve(activeorders_List, paidforCars);
                    }


                } else {
                    Log.i("soni-", "dataSnapshot is null - factory 140");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return activeorders_List;
    }

    public ArrayList<String> getSoldorders_List() {

        DatabaseReference cur_UserRef = db.child("userInfo").child(encodeString(uname));
        cur_UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    /*soldorders_List.clear();

                    if (dataSnapshot.getValue() instanceof List) {
                        soldorders_List = (ArrayList<String>) dataSnapshot.getValue();

                    } else if (dataSnapshot.getValue() instanceof HashMap) {
                        HashMap<String, String> hm = (HashMap<String, String>) dataSnapshot.getValue();
                        Set<String> keys = ((HashMap) hm).keySet();
                        for (String key : keys) {
                            soldorders_List.add(hm.get(key));
                        }
                        Log.i("soni-", "getSoldordersList - hashmap");
                    } else if (dataSnapshot.getValue() instanceof String) {
                        Log.i("soni-", "activeordersList is a String");
                        soldorders_List.add(dataSnapshot.getValue().toString());
                    } else {
                        Log.i("soni-", "not able to get activeordersList");
                    }
                } else {
                    Log.i("soni-", "dataSnapshot is null - factory 201");
                }*/

                    UserInformation info = (UserInformation) dataSnapshot.getValue();
                    HashMap<String, Date> hm = info.getSoldOrders();
                    for (String key : hm.keySet()) {
                        soldorders_List.add(key);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return soldorders_List;
    }


    public void addNewProfileInfo(String username, UserInformation userInfo) {
        DatabaseReference currentUserRef = db.child("userInfo");
        currentUserRef.child(encodeString(username)).setValue(userInfo);

//        String usernameEn = "";
//        if(username.isEmpty() || username == null)   {
//            usernameEn = encodeString(new FirebaseAdapter().getCurrentUser());
//        }else{
//            usernameEn = encodeString(username);
//        }
//        Log.i("soni-", "adding to userinfo user= "+ usernameEn);
//
//        currentUserRef.child(usernameEn).child("emailId").setValue(emailId);
//        currentUserRef.child(usernameEn).child("location").setValue(location);
        //currentUserRef.child(username).child("activeorders").setValue(new ArrayList<String>());
    }


    //old way of uploading data
    /*public void uploadImportData(List<HashMap<String, Object>>hmlist, List<String> activeordersList)    {

        DatabaseReference curr_ref =  db.child("CarsInfo"); //.child(new FirebaseAdapter().getCurrentUser())
        //vehicle_no	model_name	availability description	location	sellingprice
        for(HashMap<String, Object> hm : hmlist)    {
            String vehicleNum = hm.get("vehicle_no").toString().replace(space, replacechar);
            String modelName = hm.get("model_name").toString().replace(space, replacechar);
            String availability = hm.get("availability").toString().replace(space, replacechar);
            String description = hm.get("description").toString().replace(space, replacechar);
            String location = hm.get("location").toString().replace(space, replacechar);
            String price = hm.get("sellingprice").toString().replace(space, replacechar);


            Log.i("soni-vehicleNum", vehicleNum);

            curr_ref.child(vehicleNum).child("model_name").setValue(hm.get("model_name").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("availability").setValue(hm.get("availability").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("description").setValue(hm.get("description").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("location").setValue(hm.get("location").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("sellingprice").setValue(hm.get("sellingprice").toString().replace(space, replacechar));

            // add vehicle number in userInfo for the current user who uploaded this info, it will be array of strings for vehicleNum

            if(!activeordersList.contains(vehicleNum)) {
                activeordersList.add(vehicleNum);
                updateactiveorders(activeordersList);
            }
        }

    }*/

    /*
    method for pushing carInfoSerial object into db
     */
    public void uploadData(CarInfo carInfoObj, String vehicleNum,/* ArrayList<String> activeorders_List,*/ AppListeners.CarInfoUploadListener listener) {
        DatabaseReference curr_ref = db.child("CarsInfo");

        curr_ref.child(encodeString(vehicleNum)).setValue(carInfoObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "Record added with carinfo object to database");
                if (listener != null) {
                    listener.onUploadComplete("OK");
                }
            }

        });

        /*if (!activeorders_List.contains(vehicleNum)) {
            activeorders_List.add(encodeString(vehicleNum))*/;
            updateActiveorders(vehicleNum);
//        }

    }


    private void updateActiveorders(String vehicleNum) {
        DatabaseReference activeOrdersReference = db.child("userInfo").child(encodeString(uname));

        activeOrdersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null){
                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
                    ArrayList<String> list = info.getActiveOrders();
                    list.add(encodeString(vehicleNum));
                    info.setActiveOrders(list);

                    activeOrdersReference.setValue(info);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSoldOrders(String vehicleNum) {
        DatabaseReference usrReference = db.child("userInfo").child(encodeString(uname));
        usrReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    //get current time
                    Timestamp timestamp = new Timestamp(new Date().getTime());
                    //String timeStampStr = encodeString(timestamp.toString().substring(0, 18));
                    Date date = new Date();
                    Log.i("soni-", "storing sold order at "+date.toString());
                    /*try {
                        date = new SimpleDateFormat("YYYY-mm-dd").parse(timeStampStr.substring(0, 10));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/
                    //get userinfo object
                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
//                    if(date !=null) {
                        HashMap<String, Date> hm = new HashMap<>();
                        if (info.getSoldOrders() != null && !info.getSoldOrders().isEmpty()) {
                            hm = info.getSoldOrders();
                            hm.put(vehicleNum, date);
                        } else {
                            hm.put(vehicleNum, date);
                        }

                        info.setSoldOrders(hm);
//                    }
//                    else{
//                        Log.i("soni-", "date is null");
//                    }

                    //store hm
                    usrReference.setValue(info);
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    /*
    while uploading images one by one to firebase storage, we collect the uri for the image
    and that uri should be stored in database -> user -> vehiclenum -> image_uri_list

     */
    public void updateUriList(String vehicleNum, String uri) {
        DatabaseReference imgInfoRef = db.child("CarsInfo").child(vehicleNum).child("image_uri_list");

        imgInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = null;

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    //Log.i("soni-", "dataSnapshot " + dataSnapshot.getValue().toString());

                    list = (ArrayList<String>) dataSnapshot.getValue();
                    if (!list.contains(uri)) {
                        list.add(uri);
                    }

                } else {

                    list = new ArrayList<String>();
                    list.add(uri);
                    //Log.i("soni-", "dataSnapshot has NO child image_uri_list");
                }

                if (list != null) {
                    imgInfoRef.setValue(list);

                } else {
                    //Log.i("soni-factory", "uri list is null for " + vehicleNum);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateThumbnailUri(String vehicleNum, String uri) {
        db.child("CarsInfo").child(vehicleNum).child("thumbnailUriString").setValue(uri);
    }

    public void deleteOldActiveorders_List() {
        DatabaseReference cur_UserRef = db.child("userInfo").child(encodeString(uname)).child("activeorders");
        cur_UserRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "removed old activeorders list");
            }
        });


    }

    /*
    this method moves the record to sold history node
    and deletes record from carinfo node
     */
    public void moveToSoldHistory(String vehicleNum) {
        if (vehicleNum.contains(" ")) {
            vehicleNum = vehicleNum.replace(" ", "_");
        }

        //updates the hashmap in userInfo -> user node
        deleteFromActiveOrders(vehicleNum);
        updateSoldOrders(vehicleNum);

        DatabaseReference fromRef = db.child("CarsInfo").child(vehicleNum);
        DatabaseReference toRef = db.child("SoldHistory").child(encodeString(uname));
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.i("soni-", "record moved to history");
                            //remove from carsInfo node
                            fromRef.removeValue();
                        }
                    });

                    moveInfoUpdatestoHistory(number);

                    //update stats in profile
                    CarInfo carInfo = dataSnapshot.getValue(CarInfo.class);
                    ProfileStats stats = new ProfileStats();
                    stats.setSoldInventory(1);
                    stats.setSoldWorth(Long.valueOf(carInfo.getSellingprice()));
                    updateStats(stats);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteFromActiveOrders(String vehicleNum) {

        updateActiveorders(vehicleNum);

    }

    public void activateOrder(String vehicleNum) {
        if (vehicleNum.contains(" ")) {
            vehicleNum = vehicleNum.replace(" ", "_");
        }

        DatabaseReference toRef = db.child("CarsInfo");
        DatabaseReference fromRef = db.child("SoldHistory").child(encodeString(uname)).child(vehicleNum);
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.i("soni-", "record moved to carinfo");
                            //remove node from sold history
                            fromRef.removeValue();
                            activateInfoUpdates(number);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void moveInfoUpdatestoHistory(String vehicleNum) {
        if (vehicleNum.contains(" ")) {
            vehicleNum = vehicleNum.replace(" ", "_");
        }
        final String number = vehicleNum;
        DatabaseReference fromRef = db.child("InfoUpdates").child(vehicleNum);
        DatabaseReference toRef = db.child("InfoUpdatesHistory");

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            fromRef.removeValue();
                            Log.i("soni-", "infoupdate for " + number + " moved to history");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void activateInfoUpdates(String vehicleNum) {
        if (vehicleNum.contains(" ")) {
            vehicleNum = vehicleNum.replace(" ", "_");
        }
        final String number = vehicleNum;
        DatabaseReference toRef = db.child("InfoUpdates");
        DatabaseReference fromRef = db.child("InfoUpdatesHistory").child(vehicleNum);

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            fromRef.removeValue();
                            Log.i("soni-", "infoupdates of " + number + " restored");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /*
    method to delete the car record from database
    identified by vehicle number
     */
    public void deleteRecord(String vehicleNum) {

        DatabaseReference fromRef = db.child("CarInfo").child(vehicleNum);
        DatabaseReference toRef = db.child(encodeString(uname)).child("DeletedRecords");
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            fromRef.removeValue();
                            Log.i("soni-", "Carinfo for " + number + " moved to Deleted Records");
                        }
                    });

                    deleteFromActiveOrders(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*

    below methods used by listeners to retrieve data

     */

    /*
    to retrieve cars list (active orders) for current user
     */

    public void retriveCarList(AppListeners.CarInfoRetrieveListener carInfoListener) {
        Log.i("soni-", "retriveCarList method");

        ArrayList<CarInfo> carsArraylist = new ArrayList<>();

        carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    //carsArraylist.clear();
                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
                        if (carinfo != null) {
                            CarInfo carInfoObj = carinfo.getValue(CarInfo.class);
                            String vehicleNum = carinfo.getKey();
                            carInfoObj.setVehicle_no(vehicleNum);
                            carInfoListener.onProgress();

                            carsArraylist.add(carInfoObj);
                        }
                    }

                    if (carInfoListener != null && carsArraylist != null && carsArraylist.size() > 0) {
                        Log.i("soni-", "Carinfo Data retrieved..");
                        //ArrayList<CarInfoSerial> carsSeriallist= new FirebaseAdapter().buildInfoSerializable(carsArraylist);
                        carInfoListener.onDataRetrieved(carsArraylist);
                    }

                } else {
                    ArrayList<CarInfoSerial> carsSeriallist = new ArrayList<>();
                    carInfoListener.onDataRetrieved(carsArraylist);
                    Log.i("soni-", "no data found in CarsInfoDup");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (carInfoListener != null) {
                    carInfoListener.onRetrieveFailed(databaseError.getMessage());
                }
            }
        });
    }

    /*public List retrieveMyVehicleNumbers(AppListeners.CarNumbersListener carNumbersListener)   {

        //Log.i("soni-", "retrieveMyVehicleNumbers method");

        db.child("userInfo").child(encodeString(uname)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() != null*//*&& dataSnapshot.hasChild("activeorders") && dataSnapshot.hasChild("paymentStatus")*//*) {

     *//*if(dataSnapshot.child("paymentStatus").getValue() !=null)    {
                        paymentStatus = (String)dataSnapshot.child("paymentStatus").getValue();
                    }
                    if(dataSnapshot.child("activeorders").getValue() instanceof List) {

                        activeOrders = (ArrayList<String>) dataSnapshot.child("activeorders").getValue();
                        Log.i("soni-", "retrieveMyVehicleNumbers - it is a List");
                        if(carNumbersListener !=null)   {
                            carNumbersListener.onProgress();
                        }
                    }else if(dataSnapshot.child("activeorders").getValue() instanceof HashMap){

                        HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.child("activeorders").getValue();
                        Log.i("soni-", "retrieveMyVehicleNumbers - it is a hashmap " + hashMap.keySet().toString() );
                        if(hashMap.keySet().size() == 1)    {
                            for(String hmkey : hashMap.keySet()) {
                                activeOrders = (ArrayList<String>) hashMap.get(hmkey);
                            }
                        }
                        if(carNumbersListener !=null)   {
                            carNumbersListener.onProgress();
                        }
                    }*//*
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    if(userInformation !=null && !userInformation.getActiveOrders().isEmpty()) {
                        Log.i("soni-", "retrieved info in userInformation");
                        activeOrders = userInformation.getActiveOrders();
                        paymentStatus = userInformation.getPaymentStatus();
                    }else{
                        paymentStatus = (String)dataSnapshot.child("paymentStatus").getValue();
                        if(dataSnapshot.child("activeorders").getValue() instanceof HashMap){
                            Log.i("soni-", "activeOrders is hashmap");
                        }
                        if(dataSnapshot.child("activeorders").getValue() instanceof List)   {
                            Log.i("soni-", "activeOrders is List");
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
                        carNumbersListener.onRetrieve(activeOrders, paymentStatus);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return activeOrders;
    }
*/

    /*
    method to update stats like total worth, sold worth, available inventory etc
     */

    public void updateStats(ProfileStats stats) {

        Log.i("soni-", " updating profile stats for "+ uname);

        DatabaseReference statsDb = db.child("profileStats").child(encodeString(uname));

        statsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    ProfileStats s = dataSnapshot.getValue(ProfileStats.class);

                    //check unit change for worth
                    if((s.getTotalWorth()/10000 >= 1 || s.getSoldWorth()/10000 >= 1) && s.getWorthUnit().equalsIgnoreCase("K") ){
                        s.setWorthUnit("M");
                        s.setTotalWorth(s.getTotalWorth()/1000);
                        s.setSoldWorth(s.getSoldWorth()/1000);
                        if(s.getAvgSellWorthMonth()!=0){
                        s.setAvgSellWorthMonth(s.getAvgSellWorthMonth()/1000);}
                    }else if((s.getTotalWorth()/1000 >= 1 || s.getSoldWorth()/1000 >= 1) && s.getWorthUnit().isEmpty()){
                        s.setWorthUnit("K");
                        s.setTotalWorth(s.getTotalWorth()/1000);
                        s.setSoldWorth(s.getSoldWorth()/1000);
                        if(s.getAvgSellWorthMonth()!=0){
                        s.setAvgSellWorthMonth(s.getAvgSellWorthMonth()/1000);}
                    }

                    if(s.getWorthUnit().equalsIgnoreCase("K"))  {
                        if(stats.getSoldWorth()!=0){
                        stats.setSoldWorth(stats.getSoldWorth()/1000);}
                        if(stats.getTotalWorth()!=0){
                        stats.setTotalWorth(stats.getTotalWorth()/1000);}
                    }else if(s.getWorthUnit().equalsIgnoreCase("M"))    {
                        if(stats.getSoldWorth()!=0){
                            stats.setSoldWorth(stats.getSoldWorth()/1000000);}
                        if(stats.getTotalWorth()!=0){
                            stats.setTotalWorth(stats.getTotalWorth()/1000000);}
                    }

                    if (stats.getAvailableInventory() != 0) {
                        s.setAvailableInventory(s.getAvailableInventory() + stats.getAvailableInventory());
                    }

                    if (stats.getSoldWorth() != 0) {
                        //update sellthismonth
                        s.setAvgSellThisMonth(((s.getAvgSellThisMonth() * s.getSoldInventory())+stats.getSoldWorth())
                                /(s.getSoldInventory() + stats.getSoldInventory()));
                        //calculate and update avgSell
                        s.setTotalWorth(s.getTotalWorth()-stats.getSoldWorth());
                        s.setSoldWorth(s.getSoldWorth() + stats.getSoldWorth());
                    }
                    if (stats.getSoldInventory() != 0) {
                        s.setAvailableInventory(s.getAvailableInventory()-stats.getSoldInventory());
                        s.setSoldInventory(s.getSoldInventory() + stats.getSoldInventory());
                    }
                    if (stats.getTotalWorth() != 0) {
                        s.setTotalWorth(s.getTotalWorth() + stats.getTotalWorth());
                    }



                    statsDb.setValue(s);

                } else {
                    Log.i("soni-", "updateStats- stats not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("soni-", "updateStats- " + databaseError.getMessage());
            }
        });

    }


    UserInformation userinfo = new UserInformation();


    /*
    method to retrieve stats like total worth, sold worth, available inventory etc
     */
    public ProfileStats retrieveStats(ProfileListener.RetrieveStatsListener listener) {
        ProfileStats stats = new ProfileStats();


        DatabaseReference statsDb = db.child("profileStats").child(encodeString(uname));

        statsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    ProfileStats s = dataSnapshot.getValue(ProfileStats.class);
                    stats.setAvailableInventory(s.getAvailableInventory());
                    stats.setAvgSellperMonth(s.getAvgSellperMonth());
                    stats.setAvgSellThisMonth(s.getAvgSellThisMonth());
                    stats.setSoldWorth(s.getSoldWorth());
                    stats.setTotalWorth(s.getTotalWorth());
                    stats.setSoldInventory(s.getSoldInventory());
                    stats.setWorthUnit(s.getWorthUnit());

                    if (listener != null) {
                        listener.onDataRetrieve(stats);
                    }
                } else {
                    Log.i("soni-", "stats not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null) {
                    listener.onDataCancelled();
                }
            }
        });

        return stats;

    }


    public void addNewStatsInfo() {
        ProfileStats stats = new ProfileStats();

        DatabaseReference statsDb = db.child("profileStats").child(encodeString(uname));
        statsDb.setValue(stats).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "new profileStats added for this user");
            }
        });
    }
}
