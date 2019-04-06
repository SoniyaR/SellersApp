package com.soniya.sellersapp;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {

    TextView model;
    TextView availability;
    TextView price;
    TextView vehicleNum;
    AlertDialog dialog;
    EditText priceEdit;
    boolean editmode=false;
    Button soldButton;
    EditText descEdit;
    ImageView img;

    TextView editDescription;

    HashMap<String, Object> hm = new HashMap<>();

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
        availability = (TextView) findViewById(R.id.availability);
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

        img = findViewById(R.id.carImage);

        Intent intent = getIntent();
        if(intent.getExtras() != null && intent.hasExtra("selectedHM")) {
            hm = (HashMap<String, Object>) intent.getSerializableExtra("selectedHM");
            /*for(String key: hm.keySet()){
                Log.i("soni-orderdetail", key);
            }*/

            model.setText(hm.get("model_name").toString());
            availability.setText(hm.get("availability").toString());
            price.setText(hm.get("sellingprice").toString());
            editDescription.setText(hm.get("description").toString());
            descEdit.setText(editDescription.getText());
            vehicleNum.setText(hm.get("vehicle_no").toString());
            //set images

            if(intent.hasExtra("forEdit") && intent.getBooleanExtra("forEdit", false)) {
                editmode = true;
            }
        }

        dialog = new AlertDialog.Builder(this).create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("soni-which OrdDetails", Integer.toString(which));
                if(editmode) {
                    price.setText(priceEdit.getText());
                }
            }
        });

        price.setOnClickListener(this);

        if(availability.getText().toString().equalsIgnoreCase("Sold")) {

        }


    }

    @Override
    public void onClick(View v) {
        
        switch (v.getId())  {
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

            case R.id.editTextDesc:

                if(editmode)    {
                    /*dialog.setView(descEdit);
                    dialog.setTitle("Update Description");
                    descEdit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    descEdit.setText(editDescription.getText());
                    dialog.show();*/
                    descEdit.setVisibility(View.VISIBLE);
                    editDescription.setVisibility(View.INVISIBLE);
                    descEdit.setText(editDescription.getText().toString());

                }
                break;

            case R.id.carImage:
                //opens all images to show
                displayCarImages();
                break;

        }
    }

    private void displayCarImages() {
//TODO
        Intent displayIntent = new Intent(getApplicationContext(), DisplayImages.class);
        displayIntent.putExtra("vehicleNum", vehicleNum.getText().toString());
        startActivity(displayIntent);

    }
}
