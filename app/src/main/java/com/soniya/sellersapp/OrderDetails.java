package com.soniya.sellersapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {

    TextView model;
    EditText modelEdit;
    TextView availability;
    TextView price;
    TextView vehicleNum;
    AlertDialog dialog;
    EditText priceEdit;
    boolean editmode=false;
    Button soldButton;
    EditText descEdit;
    ImageView imgView;
    ArrayList<String> urlList = new ArrayList<>();
    TextView editDescription;

    HashMap<String, Object> hm = new HashMap<>();
    List<Bitmap> carImagesList = new ArrayList<>();

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(getApplicationContext(), HomePage.class);
//        startActivity(i);
//    }

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
                makeEditable();
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

        //description
        if(!descEdit.getText().toString().equals(editDescription.getText().toString())) {
            editDescription.setText(descEdit.getText());
            editDescription.setVisibility(View.VISIBLE);
            descEdit.setVisibility(View.INVISIBLE);
        }

        //TODO update specific node with current vehicle number

        FirebaseDataFactory factory = new FirebaseDataFactory();
        //vehicle_no	model_name	availability description	location	sellingprice
        //whatever is updated, goes to hashmap, and only that is updated in the node in db


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
        modelEdit = findViewById(R.id.modeledit);
        modelEdit.setVisibility(View.INVISIBLE);

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
        if(intent.getExtras() != null && intent.hasExtra("selectedHM")) {

            hm = (HashMap<String, Object>) intent.getSerializableExtra("selectedHM");
            /*for(String key: hm.keySet()){
                Log.i("soni-orderdetail", key);
            }*/

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

        }

        dialog = new AlertDialog.Builder(this).create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("soni-which OrdDetails", Integer.toString(which));
                if(editmode) {
                    price.setText(priceEdit.getText());
                }
            }
        });

        price.setOnClickListener(this);

        /*if(availability.getText().toString().equalsIgnoreCase("Sold")) {

        }*/


    }

    private void loadImagesToList() {
        //retrieve images from urls to list of bitmap
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for(String uri : urlList)   {
            uriArrayList.add(Uri.parse(uri));
        }

    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId())  {

            case R.id.modelname:


                break;

            case R.id.soldButton:
                availability.setText("Sold");
                soldButton.setEnabled(false);
                
                break;

            case R.id.price:

                if(editmode) {
                    dialog.setView(priceEdit);
                    dialog.setTitle("Enter the new Price");
                    priceEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    priceEdit.setText(price.getText());
                    dialog.show();
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
