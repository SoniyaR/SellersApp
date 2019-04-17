package com.soniya.sellersapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DisplayImages extends AppCompatActivity {

    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        Intent intent = getIntent();
        if(intent.getExtras() !=null && intent.hasExtra("urlList")) {
            urls = (ArrayList<String>) intent.getSerializableExtra("urlList");
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        DisplayImageAdapter imageAdapter = new DisplayImageAdapter(this, urls);
        viewPager.setAdapter(imageAdapter);
    }
}
