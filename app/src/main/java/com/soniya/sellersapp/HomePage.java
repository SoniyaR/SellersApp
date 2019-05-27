package com.soniya.sellersapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HomePage extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private boolean doubleBackToExitPressedOnce=false;

    List activeOrders ;

    char space = ' ';
    char replacechar = '_';

    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    FirebaseDataFactory fbFactory = new FirebaseDataFactory();

    DatabaseReference carInfoReference;
    DatabaseReference userRef;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    String selVehicleNum = "";
    String uname = "";

    int contextSelPosition = 0;

    Fragment tab1frag;
    Fragment tab2frag;
    Bundle tabbundle = new Bundle();

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    ArrayList<CarInfo> carsArraylist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())   {
            case R.id.addinfo:
                uploadNewOrder();
                break;

            case R.id.myprofile:
                gotoProfile();
                break;

            case R.id.importexcel:
                Intent importIntent = new Intent(getApplicationContext(), ImportNewInfo.class);
                startActivity(importIntent);
                break;

            case R.id.logout:
                //ParseUser.logOut();
                fbAdapter.logoutUser();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.refreshList:
                refreshList();
                break;

            case R.id.soldhistory:
                showSoldHistory();
                break;

            default: break;
        }

        return true;
    }

    private void showSoldHistory() {
        Intent soldIntent = new Intent(getApplicationContext(), SoldHistory.class);
        startActivity(soldIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setTitle("Active Orders");

        //{model_name=qwe12_bb, sellingprice=90000, description=nnhh_ffgg, location=pune, availability=Available}

        if (isOnline()) {

            tab1frag = new Tab1Fragment();
            tab2frag = new Tab2Fragment();

            activeOrders = null;
            carsArraylist = new ArrayList<>();

            carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
            userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");

            viewPager = findViewById(R.id.viewPagerHome);
            tabLayout = findViewById(R.id.tabLayout);


//            adapter = new TabAdapter(getSupportFragmentManager());

//        adapter.addFragment(tab1frag, "My Orders");
//        adapter.addFragment(tab2frag, "Other Orders");

//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    //Log.i("soni-", "tab selected " + String.valueOf(tab.getPosition()));
                    switch(tab.getPosition())   {
                        case 0:
                            replaceFragment(tab1frag);
                            break;

                        case 1:
                            replaceFragment(tab2frag);
                            break;

                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


            if (fbAdapter.checkCurrentUser()) {

                CarInfo carInfoInstance = new CarInfo();

                carInfoInstance.setCarInfoListener(new CarInfo.CarInfoListener() {
                    @Override
                    public void onDataRetrieved(ArrayList<CarInfo> data) {
                        if (data != null && data.size() > 0) {

                            carsArraylist.addAll(data);

                            adapter = new TabAdapter(getSupportFragmentManager(), carsArraylist);

                            replaceFragment(tab1frag);

                            //tabbundle.putSerializable("carsArrayList", carsArraylist);


                            adapter.addFragment(tab1frag, "My Orders");
                            adapter.addFragment(tab2frag, "Other Orders");

                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);

                        }
                    }

                    @Override
                    public void onProgress() {
                        //Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRetrieveFailed() {
                        Log.i("soni-", "homepage- carinfo retrieve is failed");
                    }
                });


            } else {
                //goto login screen
                Log.i("soni-", "Not logged in, back to mainActivity classs");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            try {
                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setMessage("Not connected to Internet!")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alert.show();
            }
            catch (Exception e) {
                Log.i("soni-", "homepage-alertdialog exc - "+ e.getMessage());
            }
        }
/*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("soni-", "onPageSelected = " + String.valueOf(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });*/
    }

    private void replaceFragment(Fragment fragment) {

        tabbundle.putSerializable("carsArrayList", carsArraylist);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment.setArguments(tabbundle);
        ft.replace(R.id.viewPagerHome, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Log.i("soni-hompage", "replace fragment");
        ft.commit();
    }

    /*
    to retrieve cars list (active orders) for current user
     */

//    private void retriveCarList() {
//
//        CarInfo carInfoInstance = new CarInfo();
//        carInfoInstance.setCarInfoListener(new CarInfo.CarInfoListener() {
//            @Override
//            public void onDataRetrieved(ArrayList<CarInfo> data) {
//                if(data != null && data.size() > 0) {
////                    carListAdapter = new CustomAdapter(getApplicationContext(), data, R.layout.carslist_layout);
////                    carsListView.setAdapter(carListAdapter);
//                }
//            }
//
//            @Override
//            public void onProgress() {
//                Toast.makeText(HomePage.this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        /*//DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("CarsInfoDup");
//        carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
//                    carsArraylist.clear();
//                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
//                        //Log.i("soni-carinfo",carinfo.getKey().toString());
//
//                        if (activeOrders.contains(carinfo.getKey().toString())) {
//                            CarInfo carInfoObj = (CarInfo) carinfo.getValue(CarInfo.class);
//
//                            if (carInfoObj != null) {
//                                carsArraylist.add(carInfoObj);
//                                //Log.i("soni-", "retrieved carinfo object\n" + carInfoObj.getModel_name());
//                            }
//
//                        }
//                    }
//                }else{
//                    Log.i("soni-", "no data found in CarsInfoDup");
//                }
//
//                carListAdapter = new CustomAdapter(getApplicationContext(), carsArraylist, R.layout.carslist_layout);
//                carsListView.setAdapter(carListAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });*/
//
//            /*carInfoReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    hmList.clear();
//                    //vehicle_no	model_name	availability description    location	sellingprice    image_uri_list
//
//                    for (DataSnapshot carinfo : dataSnapshot.getChildren()) {
//                        //Log.i("soni-carinfo",carinfo.getKey().toString());
//
//                        if (activeOrders.contains(carinfo.getKey().toString())) {
//
//                            Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
//                            HashMap<String, Object> hm = new HashMap<String, Object>();
//                            String vehiclenum = carinfo.getKey().replace(replacechar, space);
//                            hm.put("vehicle_no", vehiclenum);
//                            while (it.hasNext()) {
//                                DataSnapshot ds = it.next();
//                                if (ds.getKey().equals("image_uri_list")) {
//                                    ArrayList<String> tempList = (ArrayList<String>) ds.getValue();
//                                    //imgUriForRecord.put(carinfo.getKey().toString(), tempList.get(0));
//                                    hm.put("carImage", tempList.get(0));
//                                    //Log.i("soni-", "got one img uri " + tempList.get(0));
//
//                                } else {
//                                    hm.put(ds.getKey(), ds.getValue().toString().replace(replacechar, space));
//                                }
//                            }
//
//                            if(!hm.keySet().contains("carImage"))   {
//                                hm.put("carImage", BitmapFactory.decodeResource(getResources(), R.drawable.nocarpicture));
//                            }
//
//                            hmList.add(hm);
//                            //simpleAdapter.notifyDataSetChanged();
//
//                        }
//                    }
//                    Log.i("soni-", "datasnapshot loop completed");
////                    simpleAdapter = new CustomAdapter(getApplicationContext(), hmList, R.layout.carslist_layout, from, to);
////                    carsList.setAdapter(simpleAdapter);
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });*/
//
//    }

    public boolean isOnline()   {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected())   {
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_tab1_context, menu);
        menu.setHeaderTitle("Select Action");
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextSelPosition = contextMenuInfo.position;
        selVehicleNum = carsArraylist.get(contextSelPosition).getVehicle_no().replace(space, replacechar);
        if(selVehicleNum != null) {
            Log.i("soni-hp-veh", selVehicleNum);
            Log.i("soni-hp-id", String.valueOf(v.getId()));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId())   {

            case R.id.deleteRecord:
                Log.i("soni-contextmenu", "deleteRecord selected");
                deleteRecord(selVehicleNum);
                return true;

            case R.id.editRecord:
                Log.i("soni-contextmenu", "editRecord selected");
                Intent editIntent = new Intent(getApplicationContext(), OrderDetails.class);
                editIntent.putExtra("forEdit", true);
                editIntent.putExtra("selVehicleNum", carsArraylist.get(contextSelPosition).getVehicle_no());
                startActivity(editIntent);
                return true;

        }

        return super.onContextItemSelected(item);

    }


    /*
    method to delete the car record from database
    identified by vehicle number
     */
    private void deleteRecord(String vehicleNum) {

        activeOrders.remove(vehicleNum);

        if(vehicleNum != null && !vehicleNum.isEmpty() && !vehicleNum.equalsIgnoreCase(""))   {

            fbFactory.moveToSoldHistory(vehicleNum);

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

                        fbFactory.deleteOldOwnerofList();

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
            fbFactory.moveInfoUpdatestoHistory(vehicleNum);
        }
    }

    private void removeImagesFromStorage(String vehicleNum) {
        StorageReference img_reference = storageRef.child(encodeString(uname)).child(vehicleNum.replace(space, replacechar));
        img_reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-homepage", "Images deleted from storage for vahicle "+ vehicleNum);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("soni-homepage", "Could not delete image(s) "+ e.getMessage() + " " + vehicleNum + uname);
            }
        })
        ;

    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    /*
        method to call Upload New info in which Car info is entered manually by user
    */
    private void uploadNewOrder() {
        if (fbAdapter.getFirebaseUser().isEmailVerified()) {
            Intent i = new Intent(getApplicationContext(), UploadNewInfo.class);
            startActivity(i);
        } else {
            Log.i("soni-", "cant add data, email not verified");
            Toast.makeText(this, "cant add data, email not verified", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    to redirect to profile
     */

    private void gotoProfile()  {
        Intent intentProfile = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intentProfile);
    }

    public void refreshList()   {
        Log.i("soni-", "Sync icon clicked...");
        carsArraylist.clear();
//        retriveCarList();
//        carListAdapter.notifyDataSetChanged();
    }

}
