package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
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

            default: break;
        }

        return true;
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

        setTitle("Active Orders");

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

        if(fbAdapter.checkCurrentUser()){
            Toast.makeText(this, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
            retriveCarList();
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
            intent.putExtra("hm", hmList.get(position).get("model_name").toString());
            startActivity(intent);

        });

    }

    private void retriveCarList() {

        /*ParseDatabaseFactory parseDatabaseFactory = new ParseDatabaseFactory();
        hmList = parseDatabaseFactory.retriveCarList();*/

        FirebaseDataFactory fbFactory = new FirebaseDataFactory();
        hmList = fbFactory.retrieveCarsList();

        adapter.notifyDataSetChanged();

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
}
