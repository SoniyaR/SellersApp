package com.soniya.sellersapp;

import android.support.annotation.NonNull;
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

import java.lang.reflect.Array;
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

    ArrayList<String> vehicleNumbers = new ArrayList<>();

    String uname=null;
    String key = null;

    public FirebaseDataFactory(){
        uname = new FirebaseAdapter().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        carInfoReference = db.child("CarsInfo");
    }

    StorageReference img_ref = FirebaseStorage.getInstance().getReference().child(new FirebaseAdapter().getCurrentUser());

    public static String encodeString(String string) {
        string = string.replace(".", ",");
        return string.replace(" ", "_");

    }

    public static String decodeString(String string) {
        string = string.replace("_", " ");
        return string.replace(",", ".");
    }

    public List<HashMap<String, Object>> retrieveCarsList (ArrayList<String> vehicleNumList) {

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

    public ArrayList<String> getactiveorders_List() {
        vehicleNumbers.clear();
        activeorders_List.clear();
        Log.i("soni-", "uname = " + encodeString(uname));
        DatabaseReference cur_UserRef =  db.child("userInfo").child(encodeString(uname)).child("activeorders");
        cur_UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue() !=null) {

                    if (dataSnapshot.getValue() instanceof List) {
                        vehicleNumbers = (ArrayList<String>) dataSnapshot.getValue();

                        if (!vehicleNumbers.isEmpty()) {
                            Log.i("soni-", "getactiveordersList - we have activeordersList vals");
                            for (String num : vehicleNumbers) {
                                activeorders_List.add(num);
                            }

                        }

                    } else if (dataSnapshot.getValue() instanceof HashMap) {
                        HashMap<String, String> hm = (HashMap<String, String>) dataSnapshot.getValue();
                        Set<String> keys = ((HashMap) hm).keySet();
                        for (String key : keys) {
                            vehicleNumbers.add(hm.get(key));
                            activeorders_List.add(hm.get(key));
                        }
                        Log.i("soni-", "getactiveordersList , its hashmap ownerlist size=" + String.valueOf(activeorders_List.size()));
                    } else if (dataSnapshot.getValue() instanceof String) {
                        Log.i("soni-", "activeordersList is a String");
                        activeorders_List.add(dataSnapshot.getValue().toString());
                    } else {
                        Log.i("soni-", "not able to get activeordersList");
                    }
                } else{
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
        ArrayList<String> soldorders_List = new ArrayList<>();
        DatabaseReference cur_UserRef =  db.child("userInfo").child(encodeString(uname)).child("soldorders");
        cur_UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue() !=null) {

                    if (dataSnapshot.getValue() instanceof List) {
                        vehicleNumbers = (ArrayList<String>) dataSnapshot.getValue();

                        if (!vehicleNumbers.isEmpty()) {
                            Log.i("soni-", "getSoldordersList - we have soldordersList vals");
                            for (String num : vehicleNumbers) {
                                soldorders_List.add(num);
                            }

                        }

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
                } else{
                    Log.i("soni-", "dataSnapshot is null - factory 140");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return soldorders_List;
    }


    public void addNewProfileInfo(String username, UserInformation userInfo)   {
        DatabaseReference currentUserRef =  db.child("userInfo");
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
    public void uploadData(CarInfo carInfoObj, String vehicleNum, ArrayList<String> activeorders_List, CarInfo.CarInfoUploadListener listener)    {
        DatabaseReference curr_ref =  db.child("CarsInfo");

        curr_ref.child(vehicleNum).setValue(carInfoObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "Record added with carinfo object to database");
                if(listener !=null) {
                    listener.onUploadComplete("OK");
                }
            }

        });

        if(!activeorders_List.contains(vehicleNum)) {
            activeorders_List.add(vehicleNum);
            updateActiveorders(activeorders_List);
        }

    }


    private void updateActiveorders(ArrayList<String> activeorders_List) {
        DatabaseReference activeOrdersReference = db.child("userInfo").child(encodeString(uname)).child("activeorders");
        activeOrdersReference.setValue(activeorders_List).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("soni-", "added new activeorders list");
                }
            });
    }

    private void updateSoldOrders(ArrayList<String> soldOrders) {
        DatabaseReference soldOrdersReference = db.child("userInfo").child(encodeString(uname)).child("soldorders");
        soldOrdersReference.setValue(soldOrders).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "added new soldOrders list");
            }
        });
    }

    /*
    while uploading images one by one to firebase storage, we collect the uri for the image
    and that uri should be stored in database -> user -> vehiclenum -> image_uri_list

     */
    public void updateUriList(String vehicleNum, String uri){
        DatabaseReference imgInfoRef = db.child("CarsInfo").child(vehicleNum).child("image_uri_list");

        imgInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = null;

                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
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

    public void deleteOldActiveorders_List() {
        DatabaseReference cur_UserRef =  db.child("userInfo").child(encodeString(uname)).child("activeorders");
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
        if(vehicleNum.contains(" ")){
            vehicleNum = vehicleNum.replace(" ", "_");
        }

        deleteFromActiveOrders(vehicleNum);
        addToSoldOrders(vehicleNum);

        DatabaseReference fromRef = db.child("CarsInfo").child(vehicleNum);
        DatabaseReference toRef = db.child("SoldHistory");
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){


                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.i("soni-", "record moved to history");
                            //remove from carsInfo node
                            fromRef.removeValue();
                        }
                    });
                    moveInfoUpdatestoHistory(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteFromActiveOrders(String vehicleNum)   {
        ArrayList<String> activeList = getactiveorders_List();
        deleteOldActiveorders_List();
        activeList.remove(vehicleNum);
        updateActiveorders(activeList);

    }

    public void addToSoldOrders(String vehicleNum)  {
        ArrayList<String> soldOrders = getSoldorders_List();
        soldOrders.add(vehicleNum);
        updateSoldOrders(soldOrders);
    }

    public void activateOrder(String vehicleNum)    {
        if(vehicleNum.contains(" ")){
            vehicleNum = vehicleNum.replace(" ", "_");
        }

        DatabaseReference toRef = db.child("CarsInfo");
        DatabaseReference fromRef = db.child("SoldHistory").child(vehicleNum);
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
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
        if(vehicleNum.contains(" ")){
            vehicleNum = vehicleNum.replace(" ", "_");
        }
        final String number = vehicleNum;
        DatabaseReference fromRef = db.child("InfoUpdates").child(vehicleNum);
        DatabaseReference toRef = db.child("InfoUpdatesHistory");

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null)   {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            fromRef.removeValue();
                            Log.i("soni-", "infoupdate for "+ number + " moved to history");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void activateInfoUpdates(String vehicleNum){
        if(vehicleNum.contains(" ")){
            vehicleNum = vehicleNum.replace(" ", "_");
        }
        final String number = vehicleNum;
        DatabaseReference toRef = db.child("InfoUpdates");
        DatabaseReference fromRef = db.child("InfoUpdatesHistory").child(vehicleNum);

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null)   {
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
        DatabaseReference toRef = db.child("DeletedRecords");
        final String number = vehicleNum;

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null)   {
                    toRef.child(number).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            fromRef.removeValue();
                            Log.i("soni-", "Carinfo for "+ number + " moved to Deleted Records");
                        }
                    });

                    deleteFromActiveOrders(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*if(vehicleNum != null && !vehicleNum.isEmpty() && !vehicleNum.equalsIgnoreCase(""))   {

            moveToSoldHistory(vehicleNum);

            //remove vehicle number from userinfo -> username-> activeorders list
            userRef.child(encodeString(uname)).child("activeorders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() instanceof List) {
                        List<String> numList = (ArrayList<String>) dataSnapshot.getValue();
                        if (numList.size() > 0 && numList.contains(vehicleNum)) {
                            numList.remove(vehicleNum);
                            Log.i("soni-homepage", "deleterecord() ---> removed " + vehicleNum + " from userinfo");
                            userRef.child(encodeString(uname)).child("activeorders").setValue(numList);
                        }
                    }
                    else if(dataSnapshot.getValue() instanceof HashMap){
                        HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.getValue();

                        deleteOldActiveorders_List();

                        List<String> vehicleList =  new ArrayList<>();
                        if(hashMap.keySet().size() == 1)    {
                            Set<String> keyset = hashMap.keySet();
                            for(String key: keyset) {
                                vehicleList = hashMap.get(key);
                            }
                        }
                        if(vehicleList.contains(null)){
                            Log.i("soni-homepage", "vehicleList has null elements");
                            while(vehicleList.remove(null));
                        }
                        if(vehicleList.contains(vehicleNum)) {
                            List<String> tempList  = new ArrayList<>();
                            for(String num: vehicleList)   {
                                if(!num.equalsIgnoreCase(vehicleNum))   {
                                    tempList.add(num);
                                }
                            }

                            userRef.child(encodeString(uname)).child("activeorders").setValue(tempList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("soni-", "updated activeorders list for deleteRecord");
                                }
                            });
                        }

                        Log.i("soni-deleterecord ", "its hashmap");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //removeImagesFromStorage(vehicleNum);
            moveInfoUpdatestoHistory(vehicleNum);
        }*/



    }


    /*

    below methods used by listeners to retrieve data

     */

    /*
    to retrieve cars list (active orders) for current user
     */

    public void retriveCarList(CarInfo.CarInfoRetrieveListener carInfoListener) {
        Log.i("soni-", "retriveCarList method");

        ArrayList<CarInfo> carsArraylist = new ArrayList<>();

        carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {

                    //carsArraylist.clear();
                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
                        if(carinfo !=null) {
                            CarInfo carInfoObj = carinfo.getValue(CarInfo.class);
                            String vehicleNum = carinfo.getKey();
                            carInfoObj.setVehicle_no(vehicleNum);
                            carInfoListener.onProgress();

                            carsArraylist.add(carInfoObj);
                        }
                    }

                    if(carInfoListener !=null && carsArraylist != null && carsArraylist.size()>0){
                        Log.i("soni-", "Carinfo Data retrieved..");
                        //ArrayList<CarInfoSerial> carsSeriallist= new FirebaseAdapter().buildInfoSerializable(carsArraylist);
                        carInfoListener.onDataRetrieved(carsArraylist);
                    }

                }else{
                    ArrayList<CarInfoSerial> carsSeriallist = new ArrayList<>();
                    carInfoListener.onDataRetrieved(carsArraylist);
                    Log.i("soni-", "no data found in CarsInfoDup");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(carInfoListener !=null) {
                    carInfoListener.onRetrieveFailed(databaseError.getMessage());
                }
            }
        });
    }

    ArrayList<String> activeOrders = new ArrayList<>();

    public List retrieveMyVehicleNumbers(CarInfo.CarNumbersListener carNumbersListener)   {

        Log.i("soni-", "retrieveMyVehicleNumbers method");

        db.child("userInfo").child(encodeString(uname)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot !=null && dataSnapshot.hasChild("activeorders")) {
                    if(dataSnapshot.child("activeorders").getValue() instanceof List) {

                        activeOrders = (ArrayList<String>) dataSnapshot.child("activeorders").getValue();
                        //Log.i("soni-", "retrieveMyVehicleNumbers - it is a List");
                        if(carNumbersListener !=null)   {
                            carNumbersListener.onProgress();
                        }

                    }else if(dataSnapshot.child("activeorders").getValue() instanceof HashMap){

                        HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.child("activeorders").getValue();
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
