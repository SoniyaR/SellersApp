package com.soniya.sellersapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.adapters.TabAdapter;
import com.soniya.sellersapp.fragments.OrdersFragment;
import com.soniya.sellersapp.fragments.AllOrdersFragment;
import com.soniya.sellersapp.fragments.LeadsFragment;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int gotoTab = 0;
    Context context;

    private boolean doubleBackToExitPressedOnce=false;

    List activeOrders ;

    FirebaseAdapter fbAdapter = new FirebaseAdapter();

    DatabaseReference carInfoReference;
    DatabaseReference userRef;

    Fragment tab1frag;
    Fragment tab2frag;
    Fragment tab3frag;
    Bundle tabbundle = new Bundle();

    ArrayList<CarInfoSerial> carsArraylist;

    DrawerLayout homeDrawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("Active Orders");
        context = this;

        homeDrawerLayout = findViewById(R.id.main_drawer);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);

        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.myprofile:
                        gotoProfile();
                        break;

                    case R.id.settings:
                        gotoSettings();
                        break;

                    case R.id.privacypolicy:
                        openPolicy();
                        break;

                    case R.id.logout:
                        fbAdapter.logoutUser();
                        context.getSharedPreferences("LoginInfo",MODE_PRIVATE).edit().clear().apply();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                        break;

                        default:
                            Log.i("soni-", "default case on navigation change...");

                }
                menuItem.setChecked(true);
                homeDrawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_orders);
        loadFragment(new OrdersFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
                switch(item.getItemId()){

                    case R.id.bottom_nav_orders:
                        fragment = new OrdersFragment();
                        break;

                    case R.id.bottom_nav_other_orders:
                        fragment = new AllOrdersFragment();
                        break;

                    case R.id.bottom_nav_leads:
                        fragment = new LeadsFragment();
                        break;
                }
                item.setChecked(true);
                if(fragment!=null)
                loadFragment(fragment);

                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView nameNav = headerView.findViewById(R.id.nav_name);
        nameNav.setText(getSharedPreferences("LoginInfo", MODE_PRIVATE).getString("userName", ""));
        TextView emailNav = headerView.findViewById(R.id.nav_email);
        emailNav.setText(getSharedPreferences("LoginInfo", MODE_PRIVATE).getString("userEmail", ""));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        Log.i("soni-date ", fbAdapter.getSignupDate().toString());
        carsArraylist = new ArrayList<>();

        //{model_name=qwe12_bb, sellingprice=90000, description=nnhh_ffgg, location=pune, availability=Available}

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.list_container, fragment)
                .commit();

    }

    private void openPolicy() {
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.nav_items, menu);

        return super.onCreateOptionsMenu(menu);
    }
*/
    //handles click on toolbar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                homeDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())   {
            case R.id.addinfo:
                uploadNewOrder();
                break;

            case R.id.myprofile:

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

            case R.id.soldhistory:
                showSoldHistory();
                break;

            default: break;
        }

        return true;
    }*/

    private void showSoldHistory() {
        Intent soldIntent = new Intent(getApplicationContext(), SoldHistory.class);
        startActivity(soldIntent);
    }

    private void setupTabFragments()    {

        tab1frag = new OrdersFragment();
        tab2frag = new AllOrdersFragment();
        tab3frag = new LeadsFragment();

        activeOrders = null;
        carsArraylist = new ArrayList<>();

        carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
        userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");


        AppListeners listenerInstance = new AppListeners();

        listenerInstance.setCarInfoRetrieveListener(new AppListeners.CarInfoRetrieveListener() {
            @Override
            public void onDataRetrieved(ArrayList<CarInfo> data) {
                if (data != null) {

                    carsArraylist.addAll(fbAdapter.buildInfoSerializable(data));

                    adapter = new TabAdapter(getSupportFragmentManager());

//                    replaceFragment(tab1frag);

                    adapter.addFragment(tab1frag, "My Orders");
                    adapter.addFragment(tab2frag, "Other Orders");
                    adapter.addFragment(tab3frag, "Leads");

                    if(gotoTab > 0) {
                        viewPager.setCurrentItem(gotoTab);
                    }
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);

                }
            }

            @Override
            public void onProgress() {
                //Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetrieveFailed(String error) {

                Log.i("soni-", "homepage- carinfo retrieve is failed " + error);
            }
        });

    }
/*

    private void replaceFragment(Fragment fragment) {

        tabbundle.putSerializable("carsArrayList", carsArraylist);
        if (!isFinishing() && fragment !=null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            fragment.setArguments(tabbundle);
            ft.replace(R.id.viewPagerHome, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    private void paymentPendingFragment(Fragment fragment)  {
        Bundle tempbundle = new Bundle();
        tempbundle.putString("paymentStatus", "Pending");
        if (!isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            fragment.setArguments(tempbundle);
            ft.replace(R.id.viewPagerHome, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //ft.addToBackStack(null);
            //Log.i("soni-hompage", "payment Pending fragment");
            ft.commit();
        }
    }

*/


/*

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
*/


    /*
    method to delete the car record from database
    identified by vehicle number
     */
   /* private void deleteRecord(String vehicleNum) {

        activeOrders.remove(vehicleNum);

        if(vehicleNum != null && !vehicleNum.isEmpty() && !vehicleNum.equalsIgnoreCase(""))   {

            fbFactory.moveToSoldHistory(vehicleNum);

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

                        fbFactory.deleteOldActiveorders_List();

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

*/
    @Override
    public void onBackPressed() {

        if(homeDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            homeDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

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

    private void gotoSettings(){

    }

  /*  public void refreshList()   {
        Log.i("soni-", "Sync icon clicked...");
        carsArraylist.clear();
//        retriveCarList();
//        carListAdapter.notifyDataSetChanged();
    }
*/
}
