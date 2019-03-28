package com.soniya.sellersapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FirebaseDataFactory {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    char space = ' ';
    char replacechar = '_';
    DatabaseReference carInfoReference;

   /* public void addUserInfo(String emailId){

        //create new node for signed up user
        db.child("userInfo").child("Username").setValue(emailId);

    }*/

    public List<HashMap<String, Object>> retrieveCarsList ()    {

        List<HashMap<String, Object>> hmlist = new ArrayList<>();
        carInfoReference = db.child("CarsInfo").child("MH02_RT_4532");
            carInfoReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() !=null) {
                        Log.i("soni-dataSnapshot", dataSnapshot.getValue().toString());

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.hasChildren() && child.getValue() != null) {
                                Log.i("soni-", child.getValue().toString());
                                HashMap<String, Object> hashMap = new HashMap<>();
                                Iterator<DataSnapshot> iterator = child.getChildren().iterator();
                                while(iterator.hasNext()){
                                    DataSnapshot ds = iterator.next();
                                    hashMap.put(ds.getKey(), ds.getValue());
                                    Log.i("soni-iter", ds.getKey() + " "+ ds.getValue());

                                }
                                hmlist.add(hashMap);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        /*Query query = carInfoReference.equalTo("MH02_RT_4532");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("soni-query",String.valueOf(dataSnapshot.hasChildren()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        return hmlist;

    }

    public void addNewProfileInfo(String emailId, String username, String location)   {
        DatabaseReference currentUserRef =  db.child("userInfo");

        currentUserRef.child(username).child("emailId").setValue(emailId);
        currentUserRef.child(username).child("location").setValue(location);
    }

    public void searchUserInfo()    {


    }


    public void uploadImportData(List<HashMap<String, Object>>hmlist)    {

        DatabaseReference curr_ref =  db.child("CarsInfo").child(new FirebaseAdapter().getCurrentUser());
        //vehicle_no	model_name	availability description	location	sellingprice
        for(HashMap<String, Object> hm : hmlist)    {
            String vehicleNum = hm.get("vehicle_no").toString().replace(space, replacechar);
            String modelName = hm.get("model_name").toString().replace(space, replacechar);
            String availability = hm.get("availability").toString().replace(space, replacechar);
            String description = hm.get("description").toString().replace(space, replacechar);
            String location = hm.get("location").toString().replace(space, replacechar);
            String price = hm.get("sellingprice").toString().replace(space, replacechar);


            Log.i("soni-vehicleNum", vehicleNum);
            /*curr_ref.child(vehicleNum).child("model_name").setValue(hm.get("model_name").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("availability").setValue(hm.get("availability").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("description").setValue(hm.get("description").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("location").setValue(hm.get("location").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("sellingprice").setValue(hm.get("sellingprice").toString().replace(space, replacechar));*/

            CarInfo carInfo = new CarInfo(vehicleNum, modelName, availability, location, price, description);
            curr_ref.push().setValue(carInfo);
            //TODO: add vehicle number in userInfo for the current user who uploaded this info, it will be array of strings for vehicleNum

        }
        //Map<String, CarInfo> carMap = new HashMap<>();
    }


}
