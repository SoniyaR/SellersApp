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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;
import com.soniya.sellersapp.pojo.ProfileStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;


public class UploadNewInfo2 extends AppCompatActivity  implements View.OnClickListener {

    char space = ' ';
    char replacechar = '_';

//    ConstraintLayout uploadinfo2back;
//    ScrollView uploadinfo2scroll;

    CarInfoSerial carInfoSerial ;
    Button uploadButton;
    ImageView selectedImages;
    public static final int PICK_IMAGE = 1;
    ArrayList<Uri> selectedUriList = new ArrayList<>();
    int current_img_index = 0;
    TextView skipText;
    boolean skipImages=false;

    Button saveAllButton;
    boolean isImageSelected = false;
    String thumbnailUrlStr ="";
    int thumb_img_index = 0;

    Button prevButton;
    Button nextButton;

    EditText descriptionView;
    Bitmap img = null;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference img_ref=null;
    String curr_vehicleNum="";
    String curr_model = "";

    ProgressBar progressBar;
    CheckBox checkBox;

    FirebaseDataFactory database = new FirebaseDataFactory();


    public static String encodeString(String string) {
        if(string == null || (string !=null && string.isEmpty())){
            return "";
        }
        string = string.replace(".", ",");
        return string.replace(" ", "_");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Upload New Information");
//        uploadinfo2back = (ConstraintLayout)findViewById(R.id.uploadinfo2back);
//        uploadinfo2scroll = (ScrollView) findViewById(R.id.uploadinfo2SCroll);
//        uploadinfo2back.setOnClickListener(this);
//        uploadinfo2scroll.setOnClickListener(this);

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
        checkBox = findViewById(R.id.checkBox);
        checkBox.setVisibility(View.INVISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && selectedUriList !=null && !selectedUriList.isEmpty() && selectedUriList.size() > 0){
                    //thumbnailUrlStr = selectedUriList.get(current_img_index).toString();
                    thumb_img_index = current_img_index;
                }
            }
        });

        selectedUriList.clear();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        uploadButton = findViewById(R.id.uploadImgButton);
        uploadButton.setOnClickListener(this);

        selectedImages = findViewById(R.id.selectedImgVie);
        selectedImages.setOnClickListener(this);
        selectedImages.setVisibility(View.INVISIBLE);

        descriptionView = findViewById(R.id.descriptionView);
        descriptionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("soni-", "description "+ s + " " + String.valueOf(start) + " " + String.valueOf(count));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skipText= findViewById(R.id.skipText);
        skipText.setOnClickListener(this);
        skipText.setPaintFlags(skipText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getSerializableExtra("newcarinfo") !=null){
            carInfoSerial = (CarInfoSerial) intent.getSerializableExtra("newcarinfo");
            curr_vehicleNum = carInfoSerial.getVehicle_no();
            curr_model = carInfoSerial.getModel_name();
            img_ref = storageReference.child(new FirebaseAdapter().getCurrentUser()).child(curr_vehicleNum.replace(space, replacechar));
        }

        if(!isImageSelected){
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }

    }

    int index=0;

    public void saveAllInformation()  {
        if(carInfoSerial !=null) {

            CarInfo object = buildCarinfoObject(carInfoSerial);

            AppListeners listener = new AppListeners(curr_vehicleNum);
            listener.setCarInfoUploadListener(object, new AppListeners.CarInfoUploadListener() {
                @Override
                public void onUploadComplete(String result) {
                    if(result.equalsIgnoreCase(listener.resultOk))  {

                        ProfileStats stats = new ProfileStats();
                        stats.setAvailableInventory(1);
                        stats.setTotalWorth(Long.valueOf(object.getSellingprice()));
                        database.updateStats(stats);

                        if(!skipImages && selectedUriList != null && selectedUriList.size() > 0) {

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
                            Toast.makeText(UploadNewInfo2.this, "New Record Added Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                        }
                    }
                }

                /*@Override
                public void onUploadFail(String Error) {

                    Intent i = new Intent(getApplicationContext(), HomePage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(UploadNewInfo2.this, "Something went wrong, Try again! " + Error, Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }*/
            });

        }else{
            Log.i("soni-", "uploadinfo2- something went wrong");
        }
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
        Log.i("soni-", " uploading image " + index);

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

                    if(index == thumb_img_index)    {
                        //update this uri as thumbnail uri in CarInfo
                        database.updateThumbnailUri(curr_vehicleNum.replace(space, replacechar), String.valueOf(uri));
                    }

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
                    Intent intentImport = new Intent();
                    intentImport.setType("image/*");
                    intentImport.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intentImport.setAction(Intent.ACTION_GET_CONTENT);
                    Log.i("soni-", " about to select picture/s from gallery");
                    startActivityForResult(Intent.createChooser(intentImport, "Select Picture"), PICK_IMAGE);
                }
                break;

            case R.id.prevButton:
               // prev_img_index = current_img_index;
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
                checkBox.setChecked(false);

                break;

            case R.id.nextButton:
               // prev_img_index = current_img_index;
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
                checkBox.setChecked(false);

                break;

            case R.id.saveAllButton:
                saveAllInformation();
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
                skipImages = true;
                saveAllInformation();
                break;

//            case R.id.uploadinfo2back:
//            case R.id.uploadinfo2SCroll:
//
//                //hide keyboard
//                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK )  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() !=null && data !=null) {
                    Log.i("soni-", " got picture data");
                    selectedImages.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
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

    public CarInfo buildCarinfoObject(CarInfoSerial obj)    {

        CarInfo info = new CarInfo(obj.getBrand_name(), encodeString(obj.getVehicle_no()), obj.getModel_name(), obj.getAvailability(), obj.getLocation(),
                obj.getSellingprice(), obj.getImage_uri_list());

        info.setFuelType(obj.getFuelType());
        info.setColor(obj.getColor());
        info.setYear(obj.getYearManufacturing());
        info.setInsurance(obj.getInsurance());
        info.setKmsDriven(obj.getKmsDriven());
        info.setOwner(obj.getOwner());
        info.setTransmission(obj.getTransmission());
        info.setDescription(obj.getDescription());
        info.setThumbnailUriString(thumbnailUrlStr);
        info.setCreatedDateTime(Calendar.getInstance().getTime());

        return info;

    }

}
