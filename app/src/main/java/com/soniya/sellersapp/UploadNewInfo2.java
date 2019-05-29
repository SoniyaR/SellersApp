package com.soniya.sellersapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class UploadNewInfo2 extends AppCompatActivity  implements View.OnClickListener {

    char space = ' ';
    char replacechar = '_';

    HashMap<String, Object> recHashmap = null;
    Button uploadButton;
    ImageView selectedImages;
    public static final int PICK_IMAGE = 1;
    ArrayList<Uri> selectedUriList = new ArrayList<>();
    int current_img_index = 0;
    TextView skipText;

    Button saveAllButton;
    boolean isImageSelected = false;

    Button prevButton;
    Button nextButton;

    DatabaseReference carInfoRef;

    Bitmap img = null;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference img_ref=null;
    String curr_vehicleNum="";
    String curr_model = "";

    ProgressBar progressBar;

    FirebaseDataFactory database = new FirebaseDataFactory();
   // List<HashMap<String, Object>> hmList = new ArrayList<>();

    ArrayList<String> activeordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Upload New Information");

        isImageSelected = false;
        current_img_index = 0;
        setContentView(R.layout.activity_upload_new_info2);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(this);
        prevButton.setVisibility(View.INVISIBLE);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        nextButton.setVisibility(View.INVISIBLE);
        saveAllButton = findViewById(R.id.saveAllButton);
        saveAllButton.setOnClickListener(this);
        saveAllButton.setVisibility(View.INVISIBLE);

        selectedUriList.clear();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        uploadButton = findViewById(R.id.uploadImgButton);
        uploadButton.setOnClickListener(this);

        selectedImages = findViewById(R.id.selectedImgVie);
        selectedImages.setOnClickListener(this);
        selectedImages.setVisibility(View.INVISIBLE);

        skipText= findViewById(R.id.skipText);
        skipText.setOnClickListener(this);
        skipText.setPaintFlags(skipText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getSerializableExtra("infoHashmap") !=null){
            recHashmap = (HashMap<String, Object>) intent.getSerializableExtra("infoHashmap");
            curr_vehicleNum = recHashmap.get("vehicle_no").toString();
            curr_model = recHashmap.get("model_name").toString();
            img_ref = storageReference.child(new FirebaseAdapter().getCurrentUser()).child(curr_vehicleNum.replace(space, replacechar));
        }

        if(!isImageSelected){
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
        activeordersList = new ArrayList<>();

        activeordersList=database.getactiveorders_List();

    }

    int index=0;

    public void saveAllInformation(View view)  {
        if(recHashmap !=null) {

            CarInfo carinfoDup = buildCarinfoObject(recHashmap);
            database.uploadData(carinfoDup, curr_vehicleNum.replace(space, replacechar), activeordersList);

            if(selectedUriList != null && selectedUriList.size() > 0) {

                if (img_ref != null) {
                    index=0;

                    for (Uri uri : selectedUriList) {
                        String filename = "";
                        String path = uri.getPath().toString();
                        StringTokenizer tokenizer = new StringTokenizer(path, "/");
                        while (tokenizer.hasMoreTokens()) {
                            filename = tokenizer.nextToken();
                        }

                        //Log.i("soni-filename", filename);
                        uploadImage(uri, filename);
                    }

                }

            }else{
                Log.i("soni-176", "uploadinfo2 - No images to upload...");

                Intent i = new Intent(getApplicationContext(), HomePage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(this, "New Record Added Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

        }else{
            Log.i("soni-", "uploadinfo2- something went wrong");
        }
    }


    private CarInfo buildCarinfoObject(HashMap<String, Object> recHashmap) {
        //vehicle_no	model_name	availability description	location	sellingprice
        CarInfo info = new CarInfo();
        String vehicleNum = recHashmap.get("vehicle_no").toString().replace(space, replacechar);
        String modelName = recHashmap.get("model_name").toString().replace(space, replacechar);
        String availability = recHashmap.get("availability").toString().replace(space, replacechar);
        String description = recHashmap.get("description").toString().replace(space, replacechar);
        String location = recHashmap.get("location").toString().replace(space, replacechar);
        String price = recHashmap.get("sellingprice").toString().replace(space, replacechar);

        //info.setVehicle_no(vehicleNum);
        curr_vehicleNum = vehicleNum;
        info.setModel_name(modelName);
        info.setAvailability(availability);
        info.setDescription(description);
        info.setLocation(location);
        info.setSellingprice(price);

        return info;

    }

    /*
    to upload images for current user for current vehicle (newly added)
    into firebase storage,
    --> Username
        ---> vehicleNum
            ---> img.jpg
            ---> img2.jpg
     */

    public void uploadImage(Uri uri, String filename){
        progressBar.setVisibility(View.VISIBLE);

        UploadTask uploadTask = img_ref.child("IMG_"+filename).putFile(uri);
        Task<Uri> uriTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount()/**selectedUriList.size())*/);
                progressBar.setProgress((int) progress);
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    database.updateUriList(curr_vehicleNum.replace(space, replacechar), String.valueOf(uri));

                    if(index == selectedUriList.size()){
                        Intent i = new Intent(getApplicationContext(), HomePage.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(UploadNewInfo2.this, "New Record Added Successfully!", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {

        switch (v.getId())  {
            case R.id.uploadImgButton:
                //check permission

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
                break;

            case R.id.prevButton:

                nextButton.setEnabled(true);
                if(current_img_index > 0)   {
                    current_img_index -= 1;
                }
                if(current_img_index == 0)  {
                    prevButton.setEnabled(false);
                }
                try {
                    img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUriList.get(current_img_index));
                    selectedImages.setImageBitmap(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.nextButton:
                current_img_index += 1;
                if(current_img_index == selectedUriList.size()-1){
                    nextButton.setEnabled(false);
                }
                try {
                    img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUriList.get(current_img_index));
                    selectedImages.setImageBitmap(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                prevButton.setEnabled(true);

                break;

            case R.id.saveAllButton:
                saveAllInformation(v);
                break;


            case R.id.selectedImgVie:
                //open DisplayImages()
                if(selectedUriList.size() > 0) {
                    Intent displayIntent = new Intent(getApplicationContext(), DisplayImages.class);
                    displayIntent.putExtra("urlList", selectedUriList);
                    displayIntent.putExtra("modelname", curr_model);
                    startActivity(displayIntent);
                }
                break;


            case R.id.skipText:
                saveAllInformation(v);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK )  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() !=null && data !=null) {
                    selectedImages.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    saveAllButton.setVisibility(View.VISIBLE);
                    int imgCount = data.getClipData().getItemCount();
                    if(imgCount < 6) {
                        for (int i = 0; i < imgCount; i++) {
                            selectedUriList.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }else{
                        Toast.makeText(this, "You can upload upto five pictures only!", Toast.LENGTH_SHORT).show();
                    }
                    if(selectedUriList.size()>0) {
                        isImageSelected = true;
                        try {
                            img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUriList.get(current_img_index));
                            selectedImages.setImageBitmap(img);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(selectedUriList.size() > 1)  {
                            nextButton.setEnabled(true);
                        }

                       /* //test code
                        for (Uri uri : selectedUriList) {
                            String filename = "";
                            String path = uri.getPath().toString();
                            StringTokenizer tokenizer = new StringTokenizer(path, "/");
                            while (tokenizer.hasMoreTokens()) {
                                filename = tokenizer.nextToken();
                            }

                            //Log.i("soni-filename", filename);
                        }*/
                    }
                }
            }
        }
    }
/*
    private String getFileExtension(Uri uri)    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }*/

}
