package com.soniya.sellersapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.media.RatingCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {

    TextView model;
    EditText modelEdit;
    TextView availability;
    TextView price;
    TextView vehicleNum;
    AlertDialog pricedialog;
    AlertDialog modeldialog;
    EditText priceEdit;
    boolean editmode=false;
    Button soldButton;
    EditText descEdit;
    ImageView imgView;
    ArrayList<String> urlList = new ArrayList<>();
    TextView editDescription;

    DatabaseReference carInfoReference;

    char space = ' ';
    char replacechar = '_';

    HashMap<String, Object> hm = new HashMap<>();
    List<Bitmap> carImagesList = new ArrayList<>();

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(getApplicationContext(), HomePage.class);
//        startActivity(i);
//    }

    boolean modelchanged = false;
    boolean pricechanged = false;
    boolean statuschanged = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardetail_menu, menu);

        if(editmode)    {
            menu.getItem(0).setTitle("Save");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.editCarInfo){
            if(!editmode) {
                editmode = true;
                //makeEditable();
                item.setTitle("Save");
            }else{
                editmode = false;
                item.setTitle("Edit");
                updateCarInfo();
            }
        }

        return true;
    }

    private void makeEditable() {

        if(editmode)    {
            descEdit.setVisibility(View.VISIBLE);
            editDescription.setVisibility(View.INVISIBLE);
            descEdit.setText(editDescription.getText().toString());

        }

    }

    private void updateCarInfo() {

        //TODO update specific node with current vehicle number

        //FirebaseDataFactory factory = new FirebaseDataFactory();
        //vehicle_no	model_name	availability description	location	sellingprice
        //whatever is updated, goes to hashmap, and only that is updated in the node in db

        //description
        if(!descEdit.getText().toString().equals(editDescription.getText().toString())) {
            editDescription.setText(descEdit.getText());
            editDescription.setVisibility(View.VISIBLE);
            descEdit.setVisibility(View.INVISIBLE);
            carInfoReference.child("description").setValue(editDescription.getText().toString());
        }

        //model
        if(modelchanged)    {
            carInfoReference.child("model_name").setValue(model.getText().toString());
        }

        //price
        if(pricechanged)    {
            carInfoReference.child("sellingprice").setValue(price.getText().toString());
        }

        //availability


        Log.i("soni-update", "info saved successfully!");
    }
/*
    @Override
    protected void onStart() {
        super.onStart();


    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        model = (TextView) findViewById(R.id.modelname);
        model.setOnClickListener(this);
        modelEdit = new EditText(this);

        availability = (TextView) findViewById(R.id.availability);
        availability.setOnClickListener(this);
        price = (TextView) findViewById(R.id.price);
        priceEdit = new EditText(this);

        vehicleNum = findViewById(R.id.vehNumDetailsView);

        descEdit = findViewById(R.id.editDescription);
        descEdit.setVisibility(View.INVISIBLE);

        editmode = false;

        soldButton = (Button) findViewById(R.id.soldButton);
        soldButton.setOnClickListener(this);

        editDescription = (TextView) findViewById(R.id.editTextDesc);
        editDescription.setOnClickListener(this);

        imgView = findViewById(R.id.carImage);
        imgView.setOnClickListener(this);


        Intent intent = getIntent();
        /*if(intent.getExtras() != null && intent.hasExtra("selectedHM")) {

            hm = (HashMap<String, Object>) intent.getSerializableExtra("selectedHM");
            *//*for(String key: hm.keySet()){
                Log.i("soni-orderdetail", key);
            }*//*

            model.setText(hm.get("model_name").toString());
            modelEdit.setText(model.getText());
            availability.setText(hm.get("availability").toString());
            price.setText(hm.get("sellingprice").toString());
            editDescription.setText(hm.get("description").toString());
            descEdit.setText(editDescription.getText());
            vehicleNum.setText(hm.get("vehicle_no").toString());
            //set images
            if(hm.keySet().contains("image_uri_list")) {
                urlList = (ArrayList<String>) hm.get("image_uri_list");

                if(urlList.size() > 0)  {
                    Glide.with(this).load(urlList.get(0)).into(imgView);
                    loadImagesToList();
                }
            }

            if(intent.hasExtra("forEdit") && intent.getBooleanExtra("forEdit", false)) {
                editmode = true;
                makeEditable();
            }

        }*/

        if(intent.getExtras() != null && intent.hasExtra("selVehicleNum"))  {
            vehicleNum.setText(intent.getStringExtra("selVehicleNum"));
            // call retrieve car info
            String vehicleNo = intent.getStringExtra("selVehicleNum").replace(space, replacechar);
            carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo").child(vehicleNo);

            carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if( dataSnapshot != null && dataSnapshot.hasChildren())   {

                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while(it.hasNext()) {
                            DataSnapshot ds = it.next();
                            if(ds.getKey().equals("image_uri_list"))    {
                                urlList = (ArrayList<String>) ds.getValue();
                                //load img to imgview
                                if(urlList.size() > 0)  {
                                    Glide.with(getApplicationContext()).load(urlList.get(0)).into(imgView);
                                    //loadImagesToList();
                                }
                            }
                            else{

                                switch(ds.getKey()) {

                                    case "model_name":

                                        model.setText(ds.getValue().toString());

                                        break;

                                    case "availability":
                                        availability.setText(ds.getValue().toString());
                                        break;

                                    case "sellingprice":

                                        price.setText(ds.getValue().toString());

                                        break;

                                    case "description":

                                        editDescription.setText(ds.getValue().toString());
                                        descEdit.setText(editDescription.getText());

                                        break;
                                }

                            }

                        }


                        /*//set images
                        if(hm.keySet().contains("image_uri_list")) {
                            urlList = (ArrayList<String>) hm.get("image_uri_list");

                            if(urlList.size() > 0)  {
                                Glide.with(this).load(urlList.get(0)).into(imgView);
                                loadImagesToList();
                            }
                        }*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(intent.hasExtra("forEdit") && intent.getBooleanExtra("forEdit", false)) {
                editmode = true;
                makeEditable();
            }
        }

        pricedialog = new AlertDialog.Builder(this).create();
        pricedialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("soni-which OrdDetails", Integer.toString(which));
                if(editmode) {
                    if(!price.getText().toString().equals(priceEdit.getText().toString()))  {
                        pricechanged = true;
                    }
                    price.setText(priceEdit.getText());
                }
            }
        });


        modeldialog = new AlertDialog.Builder(this).create();
        modeldialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i("soni-which OrdDetails", Integer.toString(which));
                if(editmode) {
                    if(!modelEdit.getText().toString().equals(model.getText().toString()))  {
                        modelchanged = true;
                    }
                    model.setText(modelEdit.getText());
                }
            }
        });

        price.setOnClickListener(this);

        /*if(availability.getText().toString().equalsIgnoreCase("Sold")) {

        }*/


    }

   /* private void loadImagesToList() {
        //retrieve images from urls to list of bitmap
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for(String uri : urlList)   {
            uriArrayList.add(Uri.parse(uri));
        }

    }*/

    @Override
    public void onClick(View v) {
        
        switch (v.getId())  {

            case R.id.modelname:
                modeldialog.setView(modelEdit);
                modeldialog.setTitle("Enter new model name");
                modelEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                modelEdit.setText(model.getText());
                modeldialog.show();
                break;

            case R.id.soldButton:
                availability.setText("Sold");
                soldButton.setEnabled(false);
                carInfoReference.child("availability").setValue("sold");
                
                break;

            case R.id.price:

                if(editmode) {
                    pricedialog.setView(priceEdit);
                    pricedialog.setTitle("Enter the new Price");
                    priceEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    priceEdit.setText(price.getText());
                    pricedialog.show();
                }

                break;

            case R.id.editDescription:

                if(editmode)    {
                    descEdit.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    descEdit.setText(editDescription.getText().toString());

                }
                break;

            case R.id.carImage:
                //opens all images to show
                displayCarImages();
                break;


            case R.id.availability:
//                Dialog dialog = new Dialog(this);
//                dialog.setContentView(android.R.layout.r);

                break;

        }
    }

    private void displayCarImages() {
        //TODO
        Intent displayIntent = new Intent(getApplicationContext(), DisplayImages.class);
        displayIntent.putExtra("urlList", urlList);
        startActivity(displayIntent);

    }
}
