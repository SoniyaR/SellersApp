package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomePage extends AppCompatActivity {
    //List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();

    private boolean doubleBackToExitPressedOnce=false;

    List<HashMap<String, Object>> hmList = new ArrayList<>();
    ListView carsList;
    SimpleAdapter adapter;
    ArrayList<String> activeOrders = new ArrayList<>();

    Bitmap carImage;
    FirebaseAdapter fbAdapter = new FirebaseAdapter();

    DatabaseReference carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");

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
            case R.id.addnew:
                uploadNewOrder();

                break;

            case R.id.myprofile:
                gotoProfile();
                break;

            case R.id.importexcel:
                Intent importIntent = new Intent(getApplicationContext(), ImportExcel.class);
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

            default: break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("soni-in onstart", "");

        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmList.clear();
                Log.i("soni-datachange", "in onstart");
                for(DataSnapshot carinfo : dataSnapshot.getChildren())  {
                    Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    while(it.hasNext()){
                        DataSnapshot str = it.next();
                        Log.i("soni-onstart",str.getValue().toString());
                        hm.put(str.getKey(), str.getValue().toString());
                    }
                    hmList.add(hm);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void uploadNewOrder() {

        Intent i = new Intent(getApplicationContext(), uploadNewInfo.class);
        startActivity(i);
        finish();

    }

    private void gotoProfile()  {
        Intent intentProfile = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intentProfile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Log.i("soni-", "in oncreate");
        setTitle("Active Orders");
        //{model_name=qwe12_bb, sellingprice=90000, description=nnhh_ffgg, location=pune, availability=Available}
        String[] from = {"model_name", "sellingprice", "location", "carImage"};
        int[] to = {R.id.modelName, R.id.sellingprice, R.id.location, R.id.carImageView};

        carsList = (ListView) findViewById(R.id.listView);
        activeOrders.clear();
        adapter = new SimpleAdapter(this, hmList, R.layout.carslist_layout, from, to);
        carsList.setAdapter(adapter);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && view.getId() == R.id.carImageView && data instanceof Bitmap){
                    //R.id.userImageView

                    ImageView imgV = (ImageView) view;
                    imgV.setImageBitmap((Bitmap)data);

                }else if(view instanceof TextView && data instanceof String){
                    TextView textV = (TextView) view;
                    textV.setText((String) data);
                }
                return true;
            }
        });

        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmList.clear();
                Log.i("soni-datachange", "in oncreate");
                for(DataSnapshot carinfo : dataSnapshot.getChildren())  {
                    Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    while(it.hasNext()){
                        DataSnapshot str = it.next();
                        Log.i("soni-create",str.getValue().toString());
                        hm.put(str.getKey(), str.getValue().toString());
                    }
                    hmList.add(hm);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(fbAdapter.checkCurrentUser()){
            Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            //retriveCarList();
        }
        else  {
            //goto login screen
            Log.i("soni-", "Not logged in, back to mainActivity classs");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        carsList.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(getApplicationContext(), OrderDetails.class);
            intent.putExtra("selectedHM", hmList.get(position));
            startActivity(intent);

        });

    }

    private void retriveCarList() {

        /*ParseDatabaseFactory parseDatabaseFactory = new ParseDatabaseFactory();
        hmList = parseDatabaseFactory.retriveCarList();*/

        FirebaseDataFactory fbFactory = new FirebaseDataFactory();
        hmList = fbFactory.retrieveCarsList();
        if(hmList.size() > 0){
            Log.i("soni-retrievelist", "got the data");
            adapter.notifyDataSetChanged();
        }else{
            carsList.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, new String[]{"No Records found..."}));
        }
    }

/*
    private Bitmap getImageofVehicle(String model) {

        Log.i("soni-model is", model);

        ParseQuery<ParseObject> imgQuery = ParseQuery.getQuery("activeOrders");
        imgQuery.whereEqualTo("username", recipient);
        imgQuery.whereEqualTo("imagetype", "profile_pic");
        imgQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && objects.size() > 0) {
                    for (ParseObject obj : objects) {
                        ParseFile f = (ParseFile) obj.get("image");
                        try {
                            byte[] fileData = f.getData();
                            image = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    image = BitmapFactory.decodeResource(getResources(), R.raw.noprofilepic);
                }
            }
        });

        carImage = BitmapFactory.decodeResource(getResources(), R.raw.nocarpicture);

        return carImage;

    }
    */

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


    public void refreshList()   {
        Log.i("soni-", "Sync icon clicked...");
        hmList.clear();
        retriveCarList();
        adapter.notifyDataSetChanged();
    }
}
