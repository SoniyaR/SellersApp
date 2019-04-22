package com.soniya.sellersapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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

            if(intent.hasExtra("modelname"))    {
                setTitle(intent.getStringExtra("modelname"));
            }
        }

        ViewPager viewPager = findViewById(R.id.viewpager);
        DisplayImageAdapter imageAdapter = new DisplayImageAdapter(this, urls);
        viewPager.setAdapter(imageAdapter);
        Toast.makeText(this, "Swipe up to delete the image!", Toast.LENGTH_LONG).show();

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });*/

        /*viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP)  {
                    Log.i("soni-", "Image swiped Up " + String.valueOf(event.getX()));
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN)    {
                    Log.i("soni-", "Image swiped Down " + String.valueOf(event.getX()));
                }

                return true;
            }
        });*/



    }
}
