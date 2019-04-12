package com.soniya.sellersapp;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


public class UploadNewInfo2 extends AppCompatActivity  implements View.OnClickListener {

    char space = ' ';
    char replacechar = '_';

    HashMap<String, Object> recHashmap = null;
    ImageView uploadButton;
    ImageView selectedImages;
    public static final int PICK_IMAGE = 1;
    ArrayList<Uri> selectedUriList = new ArrayList<>();
    int current_img_index = 0;

    Button saveAllButton;
    boolean isImageSelected = false;

    Button prevButton;
    Button nextButton;

    DatabaseReference carInfoRef;

    Bitmap img = null;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    StorageReference img_ref=null;
    String curr_vehicleNum="";

    ProgressBar progressBar;
    //HashMap<String, List<Uri>> urisHashMap = new HashMap<>();
    //List<Uri> urisList = new ArrayList<>();

    FirebaseDataFactory database = new FirebaseDataFactory();
    List<HashMap<String, Object>> hmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isImageSelected = false;
        current_img_index = 0;
        setContentView(R.layout.activity_upload_new_info2);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(this);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        saveAllButton = findViewById(R.id.saveAllButton);
        saveAllButton.setOnClickListener(this);

        selectedUriList.clear();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        uploadButton = (ImageView) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);

        selectedImages = findViewById(R.id.selectedImgVie);

        Intent intent = getIntent();
        if(intent.getExtras()!=null && intent.getSerializableExtra("infoHashmap") !=null){
            recHashmap = (HashMap<String, Object>) intent.getSerializableExtra("infoHashmap");
            curr_vehicleNum = recHashmap.get("vehicle_no").toString();
            img_ref = storageReference.child(new FirebaseAdapter().getCurrentUser()).child(curr_vehicleNum.replace(space, replacechar));
        }

        if(!isImageSelected){
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }

    }

    public void saveAllInformation(View view)  {
        if(recHashmap !=null) {

            hmList.clear();
            //urisList.clear();

            hmList.add(recHashmap);
            database.uploadImportData(hmList);

            if(selectedUriList != null && selectedUriList.size() > 0) {

                if (img_ref != null) {

                    for (Uri uri : selectedUriList) {
                        String filename = "";
                        String path = uri.getPath().toString();
                        StringTokenizer tokenizer = new StringTokenizer(path, "/");
                        while (tokenizer.hasMoreTokens()) {
                            filename = tokenizer.nextToken();
                        }

                        Log.i("soni-filename", filename);
                        uploadImage(uri, filename);
                    }
                }

            }

            Intent i = new Intent(getApplicationContext(), HomePage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(this, "New Record Added Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(i);

        }else{
            Log.i("soni-", "something went wrong");
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

        /*img_ref.child("IMG_"+filename).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.i("soni-","Uploaded "+ img_ref.child("IMG_"+filename).getDownloadUrl());
                Log.i("soni-", taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                urlsList.add(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                    }
                }, 1000);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("soni-","Failed "+ e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadNewInfo2.this, "Upload in Progress!", Toast.LENGTH_SHORT).show();
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressBar.setProgress((int) progress);
            }
        });*/


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
                    //urisList.add(uri);
                    Log.i("soni- uri ", String.valueOf(uri));
                    //urisList.add(uri);
                    database.updateUriList(curr_vehicleNum.replace(space, replacechar), String.valueOf(uri));

                    //urisHashMap.put(recHashmap.get("vehicle_no").toString(), urisList);
//                    AsyncRunner asyncRunner = new AsyncRunner();
//                    asyncRunner.execute(new String[]{String.valueOf(uri), vehicleNum});
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())  {
            case R.id.uploadButton:
                Intent intentImport = new Intent(Intent.ACTION_GET_CONTENT);
                intentImport.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intentImport.setType("image/*");
                startActivityForResult(Intent.createChooser(intentImport, "Select Picture"), PICK_IMAGE);
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

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK )  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if(data.getClipData() !=null && data !=null) {
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

                        //test code
                        for (Uri uri : selectedUriList) {
                            String filename = "";
                            String path = uri.getPath().toString();
                            StringTokenizer tokenizer = new StringTokenizer(path, "/");
                            while (tokenizer.hasMoreTokens()) {
                                filename = tokenizer.nextToken();
                            }

                            Log.i("soni-filename", filename);
                        }
                    }
                    //Log.i("soni-activityRes", "selected some img " + data.getClipData().getDescription().toString());
                    //selectedImages.setImageResource();
                }
            }
        }
    }

    private String getFileExtension(Uri uri)    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private class AsyncRunner extends AsyncTask<String[], Void, String> {


        @Override
        protected String doInBackground(String[]... strings) {
            if(strings.length == 2 && strings[0] !=null) {
                Log.i("soni-", "in async task, " + strings[0] + "  " + strings[1]);
                Uri uri = Uri.parse(String.valueOf(strings[0]));

                //update Uri in carInfo -> vehicleNum -> image_uri_list

                /*carInfoRef = FirebaseDatabase.getInstance().getReference().child("CarsInfo").child(String.valueOf(lists[1]));
                Log.i("soni-", "in asynctask-> updateUriList");
                carInfoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot !=null) {
                            //Log.i("soni-userinforef", dataSnapshot.getValue().toString());
                            ArrayList<Uri> list = null;
                            if (dataSnapshot.hasChild("image_uri_list")) {
                                list = (ArrayList<Uri>) dataSnapshot.child("image_uri_list").getValue();
                                if(!list.contains(uri)) {
                                    list.add(uri);
                                }
                            } else {
                                list = new ArrayList<>();
                                list.add(uri);
                            }
                            if (list != null) {
                                carInfoRef.child("image_uri_list").setValue(list);
                            } else {
                                Log.i("soni-factory", "uri list is null for " + String.valueOf(lists[1]) );
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

            }
            else{
                Log.i("soni-", "problem with params to async task");
            }
            return null;
        }

    }
}
