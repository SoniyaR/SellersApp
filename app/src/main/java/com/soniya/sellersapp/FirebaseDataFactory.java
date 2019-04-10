package com.soniya.sellersapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FirebaseDataFactory {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    char space = ' ';
    char replacechar = '_';
    DatabaseReference carInfoReference;
    DatabaseReference userInfoReference;
    ArrayList<String> vehicleNumbers = new ArrayList<>();

   /* public void addUserInfo(String emailId){

        //create new node for signed up user
        db.child("userInfo").child("Username").setValue(emailId);

    }*/

    StorageReference img_ref = FirebaseStorage.getInstance().getReference().child(new FirebaseAdapter().getCurrentUser());


    public List<HashMap<String, Object>> retrieveCarsList (ArrayList<String> vehicleNumList)    {

        carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");

        //ArrayList<String> vehicleNumList = getOwnerof();
        if(vehicleNumList.isEmpty())    {
            Log.i("soni-fbFactory", "vehicleNumList is empty");
        }

        List<HashMap<String, Object>> hmlist = new ArrayList<>();
        carInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hmlist.clear();
                //Log.i("soni-dataSnapshot", dataSnapshot.getValue().toString());
                for(DataSnapshot carinfo : dataSnapshot.getChildren())  {
                    //Log.i("soni-carinfo",carinfo.getKey().toString());
                    //activeOrders.add(carinfo.getKey().toString());
                    if(vehicleNumList.contains(carinfo.getKey())) {
                        Iterator<DataSnapshot> it = carinfo.getChildren().iterator();
                        HashMap<String, Object> hm = new HashMap<String, Object>();
                        hm.put("vehicle_no", carinfo.getKey().toString());
                        while (it.hasNext()) {
                            DataSnapshot ds = it.next();
                            //Log.i("soni-onstart",ds.getKey().toString() + " " + ds.getValue().toString());
                            hm.put(ds.getKey().toString(), ds.getValue().toString());
                        /*if(ds.getKey().equalsIgnoreCase("vehicle_no")) {
                        //activeOrders.add(ds.getValue().toString());
                    }*/
                        }
                        hmlist.add(hm);
                    }
            }
                //adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });


        /*Query query = carInfoReference.equalTo("MH02_RT_4532");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("soni-query",String.valueOf(dataSnapshot.hasChildren()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        return hmlist;

    }

    public ArrayList<String> getOwnerof() {
        vehicleNumbers.clear();
        String uname = new FirebaseAdapter().getCurrentUser();
        DatabaseReference cur_UserRef =  db.child("userInfo").child(uname);
        cur_UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.hasChild("ownerof")) {
                    vehicleNumbers = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return vehicleNumbers;
    }

    public void addNewProfileInfo(String emailId, String username, String location)   {
        DatabaseReference currentUserRef =  db.child("userInfo");
        currentUserRef.child(username).child("emailId").setValue(emailId);
        currentUserRef.child(username).child("location").setValue(location);
        currentUserRef.child(username).child("ownerof").setValue(new ArrayList<String>());
    }

    public void uploadImportData(List<HashMap<String, Object>>hmlist)    {

        DatabaseReference curr_ref =  db.child("CarsInfo"); //.child(new FirebaseAdapter().getCurrentUser())
        //vehicle_no	model_name	availability description	location	sellingprice
        for(HashMap<String, Object> hm : hmlist)    {
            String vehicleNum = hm.get("vehicle_no").toString().replace(space, replacechar);
            String modelName = hm.get("model_name").toString().replace(space, replacechar);
            String availability = hm.get("availability").toString().replace(space, replacechar);
            String description = hm.get("description").toString().replace(space, replacechar);
            String location = hm.get("location").toString().replace(space, replacechar);
            String price = hm.get("sellingprice").toString().replace(space, replacechar);


            Log.i("soni-vehicleNum", vehicleNum);

            curr_ref.child(vehicleNum).child("model_name").setValue(hm.get("model_name").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("availability").setValue(hm.get("availability").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("description").setValue(hm.get("description").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("location").setValue(hm.get("location").toString().replace(space, replacechar));
            curr_ref.child(vehicleNum).child("sellingprice").setValue(hm.get("sellingprice").toString().replace(space, replacechar));

            // add vehicle number in userInfo for the current user who uploaded this info, it will be array of strings for vehicleNum
            updateOwnerOf(vehicleNum);

//            if(selectedUriList != null && selectedUriList.size() > 0) {
//                for (Uri uri : selectedUriList) {
//                    String filename = "";
//                    String path = uri.getPath().toString();
//                    StringTokenizer tokenizer = new StringTokenizer(path, "/");
//                    while (tokenizer.hasMoreTokens()) {
//                        filename = tokenizer.nextToken();
//                    }
//
//                    Log.i("soni-filename", filename);
//                    //uploadImage(uri, filename, vehicleNum);
//                }
//            }

        }

    }

//    public void uploadImage(Uri uri, String filename, String vehicleNum){
//        //progressBar.setVisibility(View.VISIBLE);
//        Log.i("soni-", "in uploadImage");
//        UploadTask uploadTask = img_ref.child(vehicleNum).child("IMG_"+filename).putFile(uri);
//        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//
//                if(!task.isSuccessful())    {
//                    throw task.getException();
//                }
//                return img_ref.child("IMG_"+filename).getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if(task.isSuccessful()) {
//                    Uri uri = task.getResult();
//                    Log.i("soni- uri ", String.valueOf(uri));
//                    //updateUriList(vehicleNum, uri);
//                }
//            }
//        });
//
//    }



    private void updateOwnerOf(String vehicleNum) {
        Log.i("soni-updateUserInfo", "curr user "+ new FirebaseAdapter().getCurrentUser());
        userInfoReference = db.child("userInfo").child(new FirebaseAdapter().getCurrentUser());
        userInfoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null) {
                    Log.i("soni-userinforef", dataSnapshot.getValue().toString());
                    ArrayList<String> list = null;
                    if (dataSnapshot.hasChild("ownerof")) {
                        list = (ArrayList<String>) dataSnapshot.child("ownerof").getValue();
                        if(!list.contains(vehicleNum)){
                            Log.i("soni-", "list already has vehicle number");
                            list.add(vehicleNum);
                        }

                    } else {
                        list = new ArrayList<>();
                        list.add(vehicleNum);
                    }
                    if (list != null) {
                        userInfoReference.child("ownerof").setValue(list);
                    } else {
                        Log.i("soni-", "list is null");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*
    while uploading images one by one to firebase storage, we collect the uri for the image
    and that uri should be stored in database -> user -> vehiclenum -> image_uri_list

     */
    public void updateUriList(String vehicleNum, String uri){
        DatabaseReference imgInfoRef = db.child("ImgInfo").child(vehicleNum);
        Log.i("soni-", "in updateUriList " + vehicleNum);
        //String uriStr = String.valueOf(uri);

        imgInfoRef.setValue(uri);
        imgInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null) {

                    ArrayList<String> list = null;

                    if (dataSnapshot.hasChild("image_uri_list")) {
                        list = (ArrayList<String>) dataSnapshot.child("image_uri_list").getValue();
                        if (!list.contains(uri)) {
                            list.add(uri);
                        }
                    }
                    else {
                        Log.i("soni-", "dataSnapshot has NO child image_uri_list");
                        list = new ArrayList<>();
                        list.add(uri);
                    }

                    if (list != null) {
                        imgInfoRef.child("image_uri_list").setValue(list);
                        Log.i("soni-", "added/updated uri list for " + vehicleNum);
                        list.clear();
                    } else {
                        Log.i("soni-factory", "uri list is null for " + vehicleNum);
                    }

                }else{
                    Log.i("soni-", "dataSnapshot is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
