package com.soniya.sellersapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashMap;

public class OrderDetails extends AppCompatActivity {

    TextView model;
    TextView availability;
    TextView price;
    Switch soldSwitch;
    TextView soldtext;
    AlertDialog dialog;
    EditText priceEdit;
    boolean editmode=false;

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
                item.setTitle("Save");
            }else{
                editmode = false;
                item.setTitle("Edit");
                updateCarInfo();
            }
        }

        return true;
    }

    private void updateCarInfo() {
        //TODO update specific node with current vehicle number
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        model = (TextView) findViewById(R.id.modelname);
        availability = (TextView) findViewById(R.id.availability);
        price = (TextView) findViewById(R.id.price);
        priceEdit = new EditText(this);


        soldSwitch = (Switch) findViewById(R.id.soldSwitch);
        soldtext = (TextView)findViewById(R.id.soldtext);

        dialog = new AlertDialog.Builder(this).create();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editmode) {
                    price.setText(priceEdit.getText());
                }
            }
        });


        price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editmode) {
                    dialog.setView(priceEdit);
                    dialog.setTitle("Enter the new Price");
                    priceEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    priceEdit.setText(price.getText());
                    dialog.show();
                }
            }
        });

        if(availability.getText().toString().equalsIgnoreCase("Sold")) {

        }

        Intent intent = getIntent();
        if(intent.getExtras() != null && intent.hasExtra("selectedHM")) {
            HashMap<String, Object> hm = (HashMap<String, Object>) intent.getSerializableExtra("selectedHM");
            for(String key: hm.keySet()){
                Log.i("soni-orderdetail", key);
            }
        }
    }
}
