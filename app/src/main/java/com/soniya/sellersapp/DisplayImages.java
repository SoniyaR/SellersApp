package com.soniya.sellersapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DisplayImages extends AppCompatActivity {

    ArrayList<String> urls = new ArrayList<>();
    public static final int PICK_IMAGE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.displayimg_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.addimages)  {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent intentImport = new Intent(Intent.ACTION_GET_CONTENT);
                intentImport.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intentImport.setType("image/*");
                startActivityForResult(Intent.createChooser(intentImport, "Select Picture"), PICK_IMAGE);
            }
        }

        return true;
    }

    ArrayList<Uri> selectedUriList = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK )  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() !=null && data !=null) {
//                    selectedImages.setVisibility(View.VISIBLE);
//                    prevButton.setVisibility(View.VISIBLE);
//                    nextButton.setVisibility(View.VISIBLE);
//                    saveAllButton.setVisibility(View.VISIBLE);
                    int imgCount = data.getClipData().getItemCount();
                    if(imgCount < 6) {
                        for (int i = 0; i < imgCount; i++) {
                            selectedUriList.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }else{
                        Toast.makeText(this, "You can upload upto five pictures only!", Toast.LENGTH_SHORT).show();
                    }
                    if(selectedUriList.size()>0) {
                        UploadNewInfo2 uploadInfo= new UploadNewInfo2();
                        for (Uri uri : selectedUriList) {
                            String filename = "";
                            String path = uri.getPath().toString();
                            StringTokenizer tokenizer = new StringTokenizer(path, "/");
                            while (tokenizer.hasMoreTokens()) {
                                filename = tokenizer.nextToken();
                            }

                            //Log.i("soni-new file", filename);

                            uploadInfo.uploadImage(uri, filename);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intentImport = new Intent(Intent.ACTION_GET_CONTENT);
            intentImport.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intentImport.setType("image/*");
            startActivityForResult(Intent.createChooser(intentImport, "Select Picture"), PICK_IMAGE);
        }
    }

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
