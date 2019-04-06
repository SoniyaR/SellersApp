package com.soniya.sellersapp;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DisplayImages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        ArrayList<String> list = new ArrayList<>();
        list.add("somestring");

        ViewPager viewPager = findViewById(R.id.viewpager);
        DisplayImageAdapter imageAdapter = new DisplayImageAdapter(this, list);
        viewPager.setAdapter(imageAdapter);
    }
}
