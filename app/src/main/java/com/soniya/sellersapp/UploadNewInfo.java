package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.util.HashMap;

public class UploadNewInfo extends AppCompatActivity implements View.OnClickListener {

//    TextView  editDescriptionView;
    TextView descriptionView;
    Button saveButton;
    TextView titleText;
    //ParseFile img;
    //byte[] arr =null;
    TextView sellingpriceView;
    HashMap<String, Object> infoHashmap = new HashMap<>();
    TextView vehicleNum;
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_info);

        infoHashmap.clear();

        titleText = (TextView) findViewById(R.id.titleTextView);

        descriptionView = (TextView) findViewById(R.id.descriptionView);
        descriptionView.setOnClickListener(this);

        sellingpriceView = (TextView) findViewById(R.id.sellingpriceView);

//        editDescriptionView = (TextView) findViewById(R.id.descriptionView);
//        editDescriptionView.setVisibility(View.INVISIBLE);

        vehicleNum = (TextView) findViewById(R.id.numberText);
        vehicleNum.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        location = (TextView) findViewById(R.id.locationText);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);


        /*Intent i = getIntent();
        if(i != null && i.hasExtra("bitmapVal")){
            arr = i.getByteArrayExtra("bitmapVal");

        }*/

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

//            case R.id.descriptionView:
//
//                descriptionView.setVisibility(View.INVISIBLE);
//                editDescriptionView.setText(descriptionView.getText());
//                editDescriptionView.setVisibility(View.VISIBLE);
//
//                break;

            case R.id.uploadButton:
                selectPhoto();
                break;

            case R.id.saveButton:

//                descriptionView.setText(editDescriptionView.getText());
//                descriptionView.setVisibility(View.VISIBLE);
//                editDescriptionView.setVisibility(View.INVISIBLE);

                //save editDescriptionView.gettext().toString()  to database

                    if(titleText.getText().length() == 0 || titleText.getText().toString().isEmpty()){
                        titleText.setError("This field cannot be blank!");
                    }else if(descriptionView.getText().length() == 0 || descriptionView.getText().toString().isEmpty()){
                        descriptionView.setError("This field cannot be blank!");
                    }else if(sellingpriceView.getText().length() == 0 || sellingpriceView.getText().toString().isEmpty()){
                        sellingpriceView.setError("This field cannot be blank!");
                    }else if(vehicleNum.getText().length() == 0 ){
                        vehicleNum.setError("This field cannot be blank!");
                    }else if(location.getText().length() == 0){
                        location.setError("This field cannot be blank!");
                    }else {
                            //saveInfo(titleText.getText().toString(), img, descriptionView.getText().toString(), sellingpriceView.getText().toString());
                            saveInfoFirebase(infoHashmap);

                            Intent nextInfo = new Intent(getApplicationContext(), UploadNewInfo2.class);
                            nextInfo.putExtra("infoHashmap", infoHashmap);
                            startActivity(nextInfo);

                    }
                break;

            case R.id.backgoundLayout:
            case R.id.titleView:
            case R.id.locationView:
            case R.id.vehiclenum:
            case R.id.uploadImgText:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;


            default:
                break;
        }

    }

    //vehicle_no	model_name	availability description	location	sellingprice
    private void saveInfoFirebase(HashMap<String, Object> infoHashmap) {

        infoHashmap.put("vehicle_no", vehicleNum.getText().toString());
        infoHashmap.put("model_name", titleText.getText().toString());
        infoHashmap.put("availability", "Available");
        infoHashmap.put("description", descriptionView.getText().toString());
        infoHashmap.put("location", location.getText().toString());
        infoHashmap.put("sellingprice", sellingpriceView.getText().toString());

    }

    private void selectPhoto() {
        Intent ii = new Intent(getApplicationContext(), SelectPhoto.class);
        startActivity(ii);

    }

   /* public void saveInfo(String title, ParseFile carImage, String desc, String sellingprice){

        ParseObject infoObject = new ParseObject("carInformation");
        infoObject.put("title", title);
        infoObject.put("Image", carImage);
        infoObject.put("description", desc);
        infoObject.put("sellingprice", sellingprice);
        infoObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(UploadNewInfo.this, "info saved successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }*/

    public void saveImage(){


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       /* if(item.getItemId() == android.R.id.home)   {
            Log.i("soni-back", " arrow pressed");
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
