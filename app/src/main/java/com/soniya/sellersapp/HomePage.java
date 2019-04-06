package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class HomePage extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce=false;

    List<HashMap<String, Object>> hmList;
    ListView carsList;
    SimpleAdapter simpleAdapter;
    ArrayList<String> activeOrders ;

    char space = ' ';
    char replacechar = '_';

    Bitmap carImage;
    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    FirebaseDataFactory fbFactory = new FirebaseDataFactory();

    DatabaseReference carInfoReference;
    DatabaseReference userRef;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    String selVehicleNum = "";
    String uname = "";

    String[] from = {"model_name", "sellingprice", "location", "carImage"};
    int[] to = {R.id.modelName, R.id.sellingprice, R.id.location, R.id.carImageView};

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Log.i("soni-", "in oncreate");
        setTitle("Active Orders");
        //{model_name=qwe12_bb, sellingprice=90000, description=nnhh_ffgg, location=pune, availability=Available}

        activeOrders = new ArrayList<>();
        hmList = new ArrayList<>();

        carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
        userRef = FirebaseDatabase.getInstance().getReference().child("userInfo");

        carsList = (ListView) findViewById(R.id.listView);
        simpleAdapter = new SimpleAdapter(this, hmList, R.layout.carslist_layout, from, to);
        carsList.setAdapter(simpleAdapter);
        registerForContextMenu(carsList);

        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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

        if(fbAdapter.checkCurrentUser()){
            uname = fbAdapter.getCurrentUser();
            Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            activeOrders.clear();
            userRef.child(uname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    activeOrders.clear();
                    if(dataSnapshot !=null && dataSnapshot.hasChild("ownerof")) {
                        activeOrders = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                    }

                    retriveCarList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("soni-", "in onStart");

        /*
        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //hmList.clear();
                //vehicle_no	model_name	availability description	location	sellingprice

                //Log.i("soni-dataSnapshot", dataSnapshot.getValue().toString());
                for(DataSnapshot carinfo : dataSnapshot.getChildren())  {
                    //Log.i("soni-carinfo",carinfo.getKey().toString());

                    if(activeOrders.contains(carinfo.getKey().toString()))   {

                        Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                        HashMap<String, Object> hm = new HashMap<String, Object>();
                        hm.put("vehicle_no", carinfo.getKey().toString());
                        while(it.hasNext()) {
                            DataSnapshot ds = it.next();

                            hm.put(ds.getKey().toString(), ds.getValue().toString().replace(replacechar, space));
                        }
                        hmList.add(hm);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }
                simpleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        //TODO fetch first image from storage for active orders (vehicle no.)


}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_menu, menu);
        menu.setHeaderTitle("Select Action");
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int selPosition = contextMenuInfo.position;
        selVehicleNum = activeOrders.get(selPosition);
        Log.i("soni-hp-veh", selVehicleNum);
        Log.i("soni-hp-id", String.valueOf(v.getId()));

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
                startActivity(editIntent);
                return true;

        }

        return super.onContextItemSelected(item);

    }

    private void deleteRecord(String vehicleNum) {

        if(!vehicleNum.isEmpty() && !vehicleNum.equalsIgnoreCase(""))   {
            Log.i("soni-Record", vehicleNum + " deleted!");
            carInfoReference.child(vehicleNum).removeValue();

            //remove vehicle number from userinfo -> username-> ownerof list
            userRef.child(uname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> numList = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                    if(numList.size() > 0 && numList.contains(vehicleNum))    {
                        numList.remove(vehicleNum);
                        Log.i("soni-homepage", "deleterecord() ---> removed "+ vehicleNum + " from userinfo");
                        userRef.child(uname).child("ownerof").setValue(numList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            removeImagesFromStorage(vehicleNum);
        }
    }

    private void removeImagesFromStorage(String vehicleNum) {
        StorageReference img_reference = storageRef.child(uname).child(vehicleNum);
        img_reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-homepage", "Images deleted from storage for vahicle "+ vehicleNum);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("soni-homepage", "Could not delete image(s) "+ e.getMessage());
            }
        })
        ;

    }

    private void uploadNewOrder() {

        Intent i = new Intent(getApplicationContext(), uploadNewInfo.class);
        startActivity(i);
    }

    private void gotoProfile()  {
        Intent intentProfile = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intentProfile);
    }



    private void retriveCarList() {

        if(activeOrders.isEmpty())    {
            Log.i("soni-homepage", "activeOrders list is empty");
        }

        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmList.clear();
                //vehicle_no	model_name	availability description	location	sellingprice

                for(DataSnapshot carinfo : dataSnapshot.getChildren())  {
                    //Log.i("soni-carinfo",carinfo.getKey().toString());

                    if(activeOrders.contains(carinfo.getKey().toString()))   {

                        Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                        HashMap<String, Object> hm = new HashMap<String, Object>();
                        hm.put("vehicle_no", carinfo.getKey().toString());
                        while(it.hasNext()) {
                            DataSnapshot ds = it.next();

                            hm.put(ds.getKey().toString(), ds.getValue().toString().replace(replacechar, space));
                        }
                        hmList.add(hm);
                        //simpleAdapter.notifyDataSetChanged();
                    }
                }
                Log.i("soni-", "datasnapshot loop completed");
                simpleAdapter = new SimpleAdapter(getApplicationContext(), hmList, R.layout.carslist_layout, from, to);
                carsList.setAdapter(simpleAdapter);
                if(hmList.size()>0) {
                    Log.i("soni-", "data fetched, adapter was set!");
                }

                //simpleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        simpleAdapter.notifyDataSetChanged();
    }
}
