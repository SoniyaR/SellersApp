package com.soniya.sellersapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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

public class FirebaseDataFactory {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    char space = ' ';
    char replacechar = '_';
    DatabaseReference carInfoReference;
    DatabaseReference userInfoReference;

    DatabaseReference ownerInfoReference;
    List<String> ownerofList = new ArrayList<>();

    List<String> vehicleNumbers = new ArrayList<>();

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

    public String getOwnerofListKey()   {

        DatabaseReference cur_UserRef =  db.child("userInfo").child(uname).child("ownerof");
        cur_UserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                key = dataSnapshot.getKey();
                Log.i("soni-", "key fetched " + key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return key;
    }


    public List<String> getOwnerofList() {
        vehicleNumbers.clear();
        ownerofList.clear();
        DatabaseReference cur_UserRef =  db.child("userInfo").child(uname).child("ownerof");
        cur_UserRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                vehicleNumbers = (List<String>)dataSnapshot.getValue();

                if(!vehicleNumbers.isEmpty()) {
                    Log.i("soni-", "getOwnerofList - we have ownerofList vals");
                    for(String num : vehicleNumbers) {
                        ownerofList.add(num);
                    }

                }else{
                    Log.i("soni-", "getOwnerofList - ownerofList is empty");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return ownerofList;
    }

    public void addNewProfileInfo(String emailId, String username, String location)   {
        DatabaseReference currentUserRef =  db.child("userInfo");
        currentUserRef.child(username).child("emailId").setValue(emailId);
        currentUserRef.child(username).child("location").setValue(location);
        //currentUserRef.child(username).child("ownerof").setValue(new ArrayList<String>());
    }

    public void uploadImportData(List<HashMap<String, Object>>hmlist, List<String> ownerofList)    {

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

    }

//    public void uploadImage(Uri uri, String filename, String vehicleNum){
//        //progressBar.setVisibility(View.VISIBLE);
//        Log.i("soni-", "in uploadImage");
//        UploadTask uploadTask = img_ref.child(vehicleNum).child("IMG_"+filename).putFile(uri);
//        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//
//                if(!task.isSuccessful())    {
//                    throw task.getException();
//                }
//                return img_ref.child("IMG_"+filename).getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if(task.isSuccessful()) {
//                    Uri uri = task.getResult();
//                    Log.i("soni- uri ", String.valueOf(uri));
//                    //updateUriList(vehicleNum, uri);
//                }
//            }
//        });
//
//    }



    private void updateOwnerOf(List<String> ownerofList) {

        Log.i("soni-updateUserInfo", "curr user "+ new FirebaseAdapter().getCurrentUser());
        ownerInfoReference = db.child("userInfo").child(new FirebaseAdapter().getCurrentUser()).child("ownerof");
            ownerInfoReference.push().setValue(ownerofList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("soni-", "added new ownerof list");
                }
            });
        /*userInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.hasChild("ownerof")) {
                    Log.i("soni-userinforef", dataSnapshot.getValue().toString());

                    if (dataSnapshot.child("ownerof").getValue() instanceof List) {
                        ArrayList<String> list = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                        if(!list.contains(vehicleNum)){
                            Log.i("soni-", "list already has vehicle number");
                            list.add(vehicleNum);
                        }else {
                            list = new ArrayList<>();
                            list.add(vehicleNum);
                        }
                        if (list != null) {
                            userInfoReference.child("ownerof").setValue(list);
                        } else {
                            Log.i("soni-", "list is null");
                        }

                    }else if(dataSnapshot.child("ownerof").getValue() instanceof HashMap){
                        Log.i("soni-dbfactory", "ownerof is Hashmap ");
                        HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.child("ownerof").getValue();
                        ArrayList<String> vals = (ArrayList<String>) hashMap.values();
                        for(String val : vals){
                            Log.i("soni-val", val);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    /*
    while uploading images one by one to firebase storage, we collect the uri for the image
    and that uri should be stored in database -> user -> vehiclenum -> image_uri_list

     */
    public void updateUriList(String vehicleNum, String uri){
        DatabaseReference imgInfoRef = db.child("CarsInfo").child(vehicleNum).child("image_uri_list");
        //Log.i("soni-", "in updateUriList " + vehicleNum);
        //String uriStr = String.valueOf(uri);

        //imgInfoRef.setValue(uri);
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
                    Log.i("soni-", "dataSnapshot has NO child image_uri_list");
                }

                if (list != null) {
                    imgInfoRef.setValue(list);
                    //Log.i("soni-", "added/updated uri list for " + vehicleNum);
                    //list.clear();
                } else {
                    Log.i("soni-factory", "uri list is null for " + vehicleNum);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteOldOwnerofList() {
        DatabaseReference cur_UserRef =  db.child("userInfo").child(uname).child("ownerof");
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

        DatabaseReference fromRef = db.child("CarsInfo").child(vehicleNum);
        DatabaseReference toRef = db.child("SoldHistory");

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){


                    toRef.child(vehicleNum).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.i("soni-", "record moved to history");
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

        DatabaseReference toRef = db.child("CarsInfo");
        DatabaseReference fromRef = db.child("SoldHistory").child(vehicleNum);

        fromRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    toRef.child(vehicleNum).setValue(dataSnapshot.getValue(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Log.i("soni-", "record moved to carinfo");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
