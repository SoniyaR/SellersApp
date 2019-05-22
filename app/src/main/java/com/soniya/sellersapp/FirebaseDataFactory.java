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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FirebaseDataFactory {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    char space = ' ';
    char replacechar = '_';
    DatabaseReference carInfoReference;
    DatabaseReference userInfoReference;

    DatabaseReference ownerInfoReference;
    ArrayList<String> ownerofList = new ArrayList<>();

    ArrayList<String> vehicleNumbers = new ArrayList<>();

    String uname=null;
    String key = null;

    public FirebaseDataFactory(){
        uname = new FirebaseAdapter().getCurrentUser();
    }
   /* public void addUserInfo(String emailId){

        //create new node for signed up user
        db.child("userInfo").child("Username").setValue(emailId);

    }*/

    StorageReference img_ref = FirebaseStorage.getInstance().getReference().child(new FirebaseAdapter().getCurrentUser());

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

/*
    to retrieve cars list (active orders) for current user
     */

    public ArrayList<CarInfo> retriveCarList(List activeOrders, ArrayList<CarInfo> carsArraylist) {

        //DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("CarsInfoDup");
        carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
                    carsArraylist.clear();
                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
                        //Log.i("soni-carinfo",carinfo.getKey().toString());

                        if (activeOrders.contains(carinfo.getKey().toString())) {
                            CarInfo carInfoObj = (CarInfo) carinfo.getValue(CarInfo.class);

                            if (carInfoObj != null) {
                                carsArraylist.add(carInfoObj);
                                //Log.i("soni-", "retrieved carinfo object\n" + carInfoObj.getModel_name());
                            }

                        }
                    }
                }else{
                    Log.i("soni-", "no data found in CarsInfoDup");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            /*carInfoReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hmList.clear();
                    //vehicle_no	model_name	availability description    location	sellingprice    image_uri_list

                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
                        //Log.i("soni-carinfo",carinfo.getKey().toString());

                        if (activeOrders.contains(carinfo.getKey().toString())) {

                            Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                            HashMap<String, Object> hm = new HashMap<String, Object>();
                            String vehiclenum = carinfo.getKey().replace(replacechar, space);
                            hm.put("vehicle_no", vehiclenum);
                            while (it.hasNext()) {
                                DataSnapshot ds = it.next();
                                if (ds.getKey().equals("image_uri_list")) {
                                    ArrayList<String> tempList = (ArrayList<String>) ds.getValue();
                                    //imgUriForRecord.put(carinfo.getKey().toString(), tempList.get(0));
                                    hm.put("carImage", tempList.get(0));
                                    //Log.i("soni-", "got one img uri " + tempList.get(0));

                                } else {
                                    hm.put(ds.getKey(), ds.getValue().toString().replace(replacechar, space));
                                }
                            }

                            if(!hm.keySet().contains("carImage"))   {
                                hm.put("carImage", BitmapFactory.decodeResource(getResources(), R.drawable.nocarpicture));
                            }

                            hmList.add(hm);
                            //simpleAdapter.notifyDataSetChanged();

                        }
                    }
                    Log.i("soni-", "datasnapshot loop completed");
//                    simpleAdapter = new CustomAdapter(getApplicationContext(), hmList, R.layout.carslist_layout, from, to);
//                    carsList.setAdapter(simpleAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            return carsArraylist;

    }

    public List<HashMap<String, Object>> retrieveCarsList (ArrayList<String> vehicleNumList) {

        carInfoReference = db.child("CarsInfo");

        //ArrayList<String> vehicleNumList = getOwnerof();
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

    public ArrayList<String> getOwnerofList() {
        vehicleNumbers.clear();
        ownerofList.clear();
        Log.i("soni-", "uname = " + encodeString(uname));
        DatabaseReference cur_UserRef =  db.child("userInfo").child(encodeString(uname))/*.child("ownerof")*/;
        cur_UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChild("ownerof")) {

                    if (dataSnapshot.child("ownerof").getValue() instanceof List) {
                        vehicleNumbers = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();

                        if (!vehicleNumbers.isEmpty()) {
                            Log.i("soni-", "getOwnerofList - we have ownerofList vals");
                            for (String num : vehicleNumbers) {
                                ownerofList.add(num);
                            }

                        } else {
                            Log.i("soni-", "getOwnerofList - ownerofList is empty");
                        }
                    } else if (dataSnapshot.child("ownerof").getValue() instanceof HashMap) {
                        HashMap<String, String> hm = (HashMap<String, String>) dataSnapshot.child("ownerof").getValue();
                        Set<String> keys = ((HashMap) hm).keySet();
                        for (String key : keys) {
                            vehicleNumbers.add(hm.get(key));
                            ownerofList.add(hm.get(key));
                        }
                        Log.i("soni-", "getOwnerofList , its hashmap ownerlist size=" + String.valueOf(ownerofList.size()));
                    } else if (dataSnapshot.child("ownerof").getValue() instanceof String) {
                        Log.i("soni-", "ownerofList is a String");
                        ownerofList.add(dataSnapshot.child("ownerof").getValue().toString());
                    } else {
                        Log.i("soni-", "not able to get ownerofList");
                    }
                } else{
                    Log.i("soni-", "dataSnapshot is null - factory 140");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return ownerofList;
    }


    public void addNewProfileInfo(String emailId, String username, String location)   {
        DatabaseReference currentUserRef =  db.child("userInfo");

        String usernameEn = "";
        if(username.isEmpty() || username == null)   {
            usernameEn = encodeString(new FirebaseAdapter().getCurrentUser());
        }else{
            usernameEn = encodeString(username);
        }
        Log.i("soni-", "adding to userinfo user= "+ usernameEn);

        currentUserRef.child(usernameEn).child("emailId").setValue(emailId);
        currentUserRef.child(usernameEn).child("location").setValue(location);
        //currentUserRef.child(username).child("ownerof").setValue(new ArrayList<String>());
    }


    //old way of uploading data
    /*public void uploadImportData(List<HashMap<String, Object>>hmlist, List<String> ownerofList)    {

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

            if(!ownerofList.contains(vehicleNum)) {
                ownerofList.add(vehicleNum);
                updateOwnerOf(ownerofList);
            }
        }

    }*/

    /*
    method for pushing carInfo object into db
     */
    public void uploadData(CarInfo carInfo, String vehicleNum, ArrayList<String> ownerofList)    {
        DatabaseReference curr_ref =  db.child("CarsInfo");

        curr_ref.child(vehicleNum).setValue(carInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "Record added with carinfo object to database");
            }
        });

        if(!ownerofList.contains(vehicleNum)) {
            ownerofList.add(vehicleNum);
            updateOwnerOf(ownerofList);
        }

    }


    private void updateOwnerOf(ArrayList<String> ownerofList) {
        String currUser = new FirebaseAdapter().getCurrentUser();
        Log.i("soni-updateUserInfo", "curr user "+  currUser);
        ownerInfoReference = db.child("userInfo").child(encodeString(currUser)).child("ownerof");
            ownerInfoReference.setValue(ownerofList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("soni-", "added new ownerof list");
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

    public void deleteOldOwnerofList() {
        DatabaseReference cur_UserRef =  db.child("userInfo").child(encodeString(uname)).child("ownerof");
        cur_UserRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "removed old ownerof list");
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        //activeOrders.remove(vehicleNum);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");

        if(vehicleNum != null && !vehicleNum.isEmpty() && !vehicleNum.equalsIgnoreCase(""))   {

            moveToSoldHistory(vehicleNum);

            //remove vehicle number from userinfo -> username-> ownerof list
            userRef.child(encodeString(uname)).child("ownerof").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() instanceof List) {
                        List<String> numList = (ArrayList<String>) dataSnapshot.getValue();
                        if (numList.size() > 0 && numList.contains(vehicleNum)) {
                            numList.remove(vehicleNum);
                            Log.i("soni-homepage", "deleterecord() ---> removed " + vehicleNum + " from userinfo");
                            userRef.child(encodeString(uname)).child("ownerof").setValue(numList);
                        }
                    }
                    else if(dataSnapshot.getValue() instanceof HashMap){
                        HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.getValue();

                        deleteOldOwnerofList();

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

                            userRef.child(encodeString(uname)).child("ownerof").setValue(tempList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("soni-", "updated ownerof list for deleteRecord");
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
        }
    }

}
