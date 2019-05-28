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

    ArrayList<CarInfoSerial> carsArraylist;

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

            if (!fbAdapter.checkCurrentUser()) {
                //goto login screen
                Log.i("soni-", "Not logged in, back to mainActivity classs");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                setupTabFragments();
            }

        }
        else {
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

    private void setupTabFragments()    {

        tab1frag = new Tab1Fragment();
        tab2frag = new Tab2Fragment();

        activeOrders = null;
        carsArraylist = new ArrayList<>();

        carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
        userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");

        viewPager = findViewById(R.id.viewPagerHome);
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Log.i("soni-", "tab selected " + String.valueOf(tab.getPosition()));
                switch (tab.getPosition()) {
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

        CarInfoSerial CarInfoSerialInstance = new CarInfoSerial();

        CarInfoSerialInstance.setCarInfoListener(new CarInfoSerial.CarInfoListener() {
            @Override
            public void onDataRetrieved(ArrayList<CarInfoSerial> data) {
                if (data != null && data.size() > 0) {

                    carsArraylist.addAll(data);

                    adapter = new TabAdapter(getSupportFragmentManager(), carsArraylist);

                    replaceFragment(tab1frag);

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
