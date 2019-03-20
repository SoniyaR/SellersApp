package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;

public class uploadNewInfo extends AppCompatActivity implements View.OnClickListener {

    TextView  editDescriptionView;
    TextView descriptionView;
    Button saveButton;
    ImageView uploadButton;
    TextView titleText;
    ParseFile img;
    byte[] arr =null;
    TextView sellingpriceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_info);

        titleText = (TextView) findViewById(R.id.titleTextView);

        descriptionView = (TextView) findViewById(R.id.descriptionView);
        descriptionView.setOnClickListener(this);

        sellingpriceView = (TextView) findViewById(R.id.sellingpriceView);

        editDescriptionView = (TextView) findViewById(R.id.editDescription);
        editDescriptionView.setVisibility(View.INVISIBLE);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        uploadButton = (ImageView) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);

        Intent i = getIntent();
        if(i != null && i.hasExtra("bitmapVal")){
            arr = i.getByteArrayExtra("bitmapVal");

        }

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.descriptionView:

                descriptionView.setVisibility(View.INVISIBLE);
                editDescriptionView.setText(descriptionView.getText());
                editDescriptionView.setVisibility(View.VISIBLE);

                break;

            case R.id.uploadButton:
                selectPhoto();
                break;

            case R.id.saveButton:

                descriptionView.setText(editDescriptionView.getText());
                descriptionView.setVisibility(View.VISIBLE);
                editDescriptionView.setVisibility(View.INVISIBLE);

                //save editDescriptionView.gettext().toString()  to database
                if(titleText.getText() != null && !titleText.getText().toString().isEmpty()
                        && descriptionView.getText() != null && !descriptionView.getText().toString().isEmpty()
                        && sellingpriceView.getText()!=null && !sellingpriceView.getText().toString().isEmpty()) {

                    //set parsefile
                    if(arr !=null && arr.length >0) {

                        img = new ParseFile("IMG_"+ descriptionView.getText().toString() + ".png", arr);

                        saveInfo(titleText.getText().toString(), img, descriptionView.getText().toString(), sellingpriceView.getText().toString());

                    }else{
                        Log.i("soni-","Please upload car image!");
                    }


                }
                else{
                    if(titleText.getText().length() == 0 || titleText.getText().toString().isEmpty()){
                        titleText.setError("This field cannot be blank!");
                    }else if(descriptionView.getText().length() == 0 || descriptionView.getText().toString().isEmpty()){
                        descriptionView.setError("This field cannot be blank!");
                    }else if(sellingpriceView.getText().length() == 0 || sellingpriceView.getText().toString().isEmpty()){
                        sellingpriceView.setError("This field cannot be blank!");
                    }
                    //Toast.makeText(this, "This field cannot be blank! ", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.backgoundLayout:
            case R.id.titleView:
            case R.id.descriptionText:
            case R.id.uploadImgText:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                break;


            default:
                break;
        }

    }

    private void selectPhoto() {
        Intent ii = new Intent(getApplicationContext(), SelectPhoto.class);
        startActivity(ii);

    }

    public void saveInfo(String title, ParseFile carImage, String desc, String sellingprice){

        ParseObject infoObject = new ParseObject("carInformation");
        infoObject.put("title", title);
        infoObject.put("Image", carImage);
        infoObject.put("description", desc);
        infoObject.put("sellingprice", sellingprice);
        infoObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(uploadNewInfo.this, "info saved successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void saveImage(){


    }
}
