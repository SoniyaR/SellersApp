package com.soniya.sellersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.soniya.sellersapp.adapters.OrdersFragmentAdapter;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.UserInformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SoldHistory extends AppCompatActivity {

    ListView soldList;
    OrdersFragmentAdapter adapter;

    DatabaseReference userRef;

    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    String user=null;

    //List<HashMap<String, Object>> hmList;

//    String[] from = {"model_name", "sellingprice", "location"};
//    int[] to = {R.id.modelName, R.id.sellingprice, R.id.location};

    List<String> soldOrders;

    ArrayList<CarInfo> soldCarInfoList;

    char space = ' ';
    char replacechar = '_';


    public static String encodeString(String string) {
        if(string == null || (string !=null && string.isEmpty())){
            return "";
        }
        return string.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_history);

        setTitle("Sold History");

        soldList = (ListView) findViewById(R.id.soldList);
        //adapter = new OrdersFragmentAdapter(this, hmList, R.layout.carslist_layout, from, to);
        //soldList.setAdapter(adapter);

        userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");
        soldOrders = new ArrayList<>();
        soldCarInfoList = new ArrayList<>();

        if(fbAdapter.checkCurrentUser()){
            user = fbAdapter.getCurrentUser();
            Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            //activeOrders.clear();
            userRef.child(encodeString(user)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //activeOrders.clear();
                    if(dataSnapshot !=null && dataSnapshot.getValue()!=null){
                        soldOrders.clear();
                        UserInformation userInfo = (UserInformation) dataSnapshot.getValue();
                        if(userInfo.getSoldOrders() !=null && !userInfo.getSoldOrders().isEmpty()) {
                            HashMap<String, Date> hm = userInfo.getSoldOrders();
                            for (String key : hm.keySet()) {
                                soldOrders.add(key);
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

        DatabaseReference soldRef = FirebaseDatabase.getInstance().getReference().child("SoldHistory").child(encodeString(user));

        soldRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
                    soldCarInfoList.clear();
                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {

                        if (carinfo !=null && soldOrders.contains(carinfo.getKey())) {
                            String vehiclenum = carinfo.getKey();
                            CarInfo info = carinfo.getValue(CarInfo.class);
                            info.setVehicle_no(vehiclenum);
                            soldCarInfoList.add(info);


                        }
                    }

                    //ArrayList<CarInfoSerial> soldCarsSeriallist= new FirebaseAdapter().buildInfoSerializable(soldCarInfoList);
                    Log.i("soni-", "datasnapshot sold history retrieved");
                    adapter = new OrdersFragmentAdapter(getApplicationContext(), soldCarInfoList, R.layout.carslist_layout);
                    soldList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
