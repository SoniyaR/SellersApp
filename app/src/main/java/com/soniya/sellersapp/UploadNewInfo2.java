package com.soniya.sellersapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadNewInfo2 extends AppCompatActivity {

    HashMap<String, Object> recHashmap = null;
    ImageView uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_info2);


        uploadButton = (ImageView) findViewById(R.id.uploadButton);
        //uploadButton.setOnClickListener(this);


        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getSerializableExtra("infoHashmap") !=null){
            recHashmap = (HashMap<String, Object>) intent.getSerializableExtra("infoHashmap");
        }

        //TODO

        /*

        getintent from uploadnewinfo and on this page upload images and on button click, save everything to db

        then redirect to homepage with flag clear task and new task


         */
    }

    public void saveAllInformation(View view)  {
        if(recHashmap !=null) {
            FirebaseDataFactory database = new FirebaseDataFactory();
            List<HashMap<String, Object>> hmList = new ArrayList<>();
            hmList.add(recHashmap);
            database.uploadImportData(hmList);
            Intent i = new Intent(getApplicationContext(), HomePage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }else{
            Log.i("soni-", "something went wrong");
        }
    }

}
