package com.soniya.sellersapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SelectPhoto extends AppCompatActivity implements View.OnClickListener {
    Button confirm;
    //Button retake;
    int action;
    Bitmap bitmap;
    TextView text;
    ImageView imgView;
    //boolean hasProfilePic;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
/*
        if(requestCode == 1 ) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(action == 1) {
                    selectPhoto();
                }else if(action== 2){
                    takePhoto();
                }
            }
        }

        */

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        text = (TextView) findViewById(R.id.textView);
        confirm = (Button) findViewById(R.id.confirmButton);
        //retake  = (Button) findViewById(R.id.retakeButton);
        imgView = (ImageView) findViewById(R.id.imageView);

        confirm.setOnClickListener(this);
        //retake.setOnClickListener(this);

        //retrieveImage(ParseUser.getCurrentUser().getUsername(), "profile_pic");

        //check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            /*
            else{

                if(action == 1) {
                    onClick(findViewById(R.id.importButton));
                }else {
                    onClick(findViewById(R.id.captureButton));
                }
            }
            */
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            try {
                if(action==1) {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);
                }
                else if(action==2){

                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(bitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.confirmButton:
                final String curr_user = ParseUser.getCurrentUser().getUsername();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] arr = outputStream.toByteArray();

                Intent i= new Intent(getApplicationContext(), UploadNewInfo.class);
                i.putExtra("bitmapVal", arr);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

                /*final ParseFile file = new ParseFile(curr_user + "_image.png", arr);

                final ParseQuery<ParseObject> imgQuery = ParseQuery.getQuery("Images");
                imgQuery.whereEqualTo("username", curr_user);
                imgQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        //user does not exist in Image table
                        if(e==null && objects.size() == 0)  {
                            ParseObject ob = new ParseObject("Images");
                            ob.put("image", file);
                            ob.put("username", curr_user);
                            ob.put("imagetype", "profile_pic");

                            ob.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null )    {
                                        Toast.makeText(SelectPhoto.this, "Image shared!", Toast.LENGTH_SHORT).show();
                                        //addToGallery();
                                        Intent i = new Intent(getApplicationContext(), UploadNewInfo.class);
                                        //i.putExtra("imageSet", "yes");
                                        startActivity(i);
                                        //createImageFile(i);
                                    }
                                    else{
                                        Toast.makeText(SelectPhoto.this, "Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else if(objects!=null && objects.size()>0 && objects.get(0) !=null){
                            for(ParseObject obj:objects){
                                if(!obj.get("imagetype").toString().isEmpty() && obj.get("imagetype").toString().equalsIgnoreCase("profile_pic")){
                                    obj.put("image", file);
                                    obj.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null )    {
                                                Toast.makeText(SelectPhoto.this, "Image replaced!", Toast.LENGTH_SHORT).show();
                                                //addToGallery();
                                                Intent i = new Intent(getApplicationContext(), UploadNewInfo.class);
                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(SelectPhoto.this, "Please try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }
                });
                */

                //deleteCurrentProfilePic();
                //String user = ParseUser.getCurrentUser().getUsername();


                break;
/*
            case  R.id.retakeButton:
                if(action == 1) {
                    onClick(findViewById(R.id.importButton));
                }else{
                    onClick(findViewById(R.id.captureButton));
                }
                break;
*/
            case R.id.importButton:

                action = 1;
                Intent intentImport = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentImport, 1);

                break;

            case R.id.captureButton:

                action = 2;
                Intent intentCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intentCapture.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intentCapture, 1);
                }

                break;


            default:
                  break;

        }


    }
/*
    public void deleteCurrentProfilePic(){
        String curr_user = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseObject> imgQuery = ParseQuery.getQuery("Images");
        imgQuery.whereEqualTo("username", curr_user);
        imgQuery.whereEqualTo("imagetype", "profile_pic");
        imgQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

            }
        });
    }

    public void retrieveImage(String username, final String imageType) {

        ParseQuery<ParseObject> imageQuery = ParseQuery.getQuery("Images");
        imageQuery.whereEqualTo("username", username);
        imageQuery.whereEqualTo("imagetype", imageType);

        imageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    //hasProfilePic = true;
                    for (ParseObject obj : objects) {
                        ParseFile f = (ParseFile) obj.get("image");
                        try {
                            byte[] fileData = f.getData();
                            bitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
                            if(imageType.equalsIgnoreCase("profile_pic")) {
                                imgView.setImageBitmap(bitmap);
                            }

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    text.setText("Update Profile Picture...");
                }
            }

        });

    }
    */
/*
    private void createImageFile(Intent intent)  {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFile = "JPEG_"+timestamp+"_";

        File newStorageDir = new File(Environment.getExternalStorageDirectory()+ "/MyAppFolder/InstaApp/");
        if(!newStorageDir.exists()) {
            newStorageDir.mkdir();
        }

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File img = File.createTempFile(imageFile, ".jpg", newStorageDir);
            //if(img != null) {
                //Uri photoUri = FileProvider.getUriForFile(this, "com.soniya.insta_clone", img);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                //startActivityForResult(intent, 1);
            //}
            Log.i("soni-path", img.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void addToGallery()  {
        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File();
    }
    */

}
