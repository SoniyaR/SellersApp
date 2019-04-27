package com.soniya.sellersapp;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SoldHistory extends AppCompatActivity {

    ListView soldList;
    CustomAdapter adapter;

    DatabaseReference userRef;

    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    String user=null;

    List<HashMap<String, Object>> hmList;

    String[] from = {"model_name", "sellingprice", "location"};
    int[] to = {R.id.modelName, R.id.sellingprice, R.id.location};

    List<String> soldOrders;

    char space = ' ';
    char replacechar = '_';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_history);

        setTitle("Sold History");

        soldList = (ListView) findViewById(R.id.soldList);
        adapter = new CustomAdapter(this, hmList, R.layout.carslist_layout, from, to);
        soldList.setAdapter(adapter);

        userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");
        soldOrders = new ArrayList<>();

        if(fbAdapter.checkCurrentUser()){
            user = fbAdapter.getCurrentUser();
            Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            //activeOrders.clear();
            userRef.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //activeOrders.clear();
                    if(dataSnapshot !=null && dataSnapshot.hasChild("ownerof")) {
                        if(dataSnapshot.child("ownerof").getValue() instanceof List) {
                            soldOrders = (List<String>) dataSnapshot.child("ownerof").getValue();

                            if (soldOrders.size() > 0) {
                                int index = -1;
                                for (String order : (List<String>)soldOrders) {
                                    if (order == null) {
                                        index = soldOrders.indexOf(order);
                                    }
                                }
                                if (index >= 0) {
                                    soldOrders.remove(index);
                                }
                            }
                        }else if(dataSnapshot.child("ownerof").getValue() instanceof HashMap){
                            HashMap<String, List<String>> hashMap = (HashMap<String, List<String>>) dataSnapshot.child("ownerof").getValue();
                            Log.i("soni-", "it is a hashmap " + hashMap.keySet().toString() );
                            if(hashMap.keySet().size() == 1)    {
                                for(String hmkey : hashMap.keySet()) {
                                    soldOrders = (ArrayList<String>) hashMap.get(hmkey);
                                }
                            }
                        }
                    }

                    if(soldOrders !=null && soldOrders.size()>0) {
                        retriveSoldList();
                    }
                    else    {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                        soldList.setAdapter(arrayAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void retriveSoldList() {

        DatabaseReference soldRef = FirebaseDatabase.getInstance().getReference().child("SoldHistory");

        soldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null) {
                    hmList.clear();

                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {

                        if (soldOrders.contains(carinfo.getKey().toString())) {

                            Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                            HashMap<String, Object> hm = new HashMap<String, Object>();
                            String vehiclenum = carinfo.getKey().replace(replacechar, space);
                            hm.put("vehicle_no", vehiclenum);
                            while (it.hasNext()) {
                                DataSnapshot ds = it.next();
                                hm.put(ds.getKey(), ds.getValue().toString().replace(replacechar, space));

                            }

                            if(!hm.keySet().contains("carImage"))   {
                                hm.put("carImage", BitmapFactory.decodeResource(getResources(), R.drawable.nocarpicture));
                            }

                            hmList.add(hm);
                        }
                    }
                    Log.i("soni-", "datasnapshot sold history retrieved");
                    adapter = new CustomAdapter(getApplicationContext(), hmList, R.layout.carslist_layout, from, to);
                    soldList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}