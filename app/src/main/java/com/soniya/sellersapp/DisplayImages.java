package com.soniya.sellersapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soniya.sellersapp.adapters.DisplayImageAdapter;
import com.soniya.sellersapp.adapters.FirebaseAdapter;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DisplayImages extends AppCompatActivity {

    ArrayList<String> urls = new ArrayList<>();
    public static final int PICK_IMAGE = 1;

    String vehicleNum = "";
    DatabaseReference updateUriRef = null;

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
    int index = 0;

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

                    updateUriRef = FirebaseDatabase.getInstance().getReference().child("InfoUpdates")
                            .child(vehicleNum).child("image_uri_list");

                    if(selectedUriList.size()>0 && !vehicleNum.equalsIgnoreCase("")) {

                        index = 0;
                        for (Uri uri : selectedUriList) {
                            String filename = "";
                            String path = uri.getPath().toString();
                            StringTokenizer tokenizer = new StringTokenizer(path, "/");
                            while (tokenizer.hasMoreTokens()) {
                                filename = tokenizer.nextToken();
                            }

                            uploadImageFromDisplay(uri, filename);
                        }

                    }
                    else{
                        Log.i("soni-", "Not able to upload more images");
                        if(vehicleNum.isEmpty())    {
                            Log.i("soni-", "vehicle number is empty");
                        }
                        if(selectedUriList.size()==0)   {
                            Log.i("soni-", "Probably no images are selected");
                        }

                        Intent i = new Intent(getApplicationContext(), OrderDetails.class);
                        i.putExtra("selVehicleNum", vehicleNum.replace("_", " "));
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            }
        }
    }

    private void uploadImageFromDisplay(Uri uri, String filename) {

        StorageReference img_ref= FirebaseStorage.getInstance().getReference()
                .child(new FirebaseAdapter().getCurrentUser()).child(vehicleNum);
        FirebaseDataFactory database = new FirebaseDataFactory();

            UploadTask uploadTask = img_ref.child("IMG_"+filename).putFile(uri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful())    {
                        throw task.getException();
                    }

                    return img_ref.child("IMG_"+filename).getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri uri = task.getResult();
                        index++;
                        database.updateUriList(vehicleNum, String.valueOf(uri));
                        //add this in InfoUpdate node
                        updateUriRef.child("New").setValue(String.valueOf(uri));

                        if(index == selectedUriList.size()){
                            Intent i = new Intent(getApplicationContext(), OrderDetails.class);
                            i.putExtra("selVehicleNum", vehicleNum.replace("_", " "));
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Toast.makeText(DisplayImages.this, "New Images Added Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                        }
                    }
                }
            });

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

            if(intent.hasExtra("vehicle_no"))   {
                vehicleNum = intent.getStringExtra("vehicle_no");
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
