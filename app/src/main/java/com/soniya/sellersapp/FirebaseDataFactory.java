package com.soniya.sellersapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class FirebaseDataFactory {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

   /* public void addUserInfo(String emailId){

        //create new node for signed up user
        db.child("userInfo").child("Username").setValue(emailId);

    }*/

    public List<HashMap<String, Object>> retrieveCarsList ()    {

        return null;

    }

    public void addNewProfileInfo(String emailId, String username, String location)   {
        DatabaseReference currentUserRef =  db.child("userInfo");

        currentUserRef.child(username).child("emailId").setValue(emailId);
        currentUserRef.child(username).child("location").setValue(location);
    }

    public void searchUserInfo()    {


    }


    public void uploadImportData(List<HashMap<String, Object>>hmlist)    {

        DatabaseReference curr_ref =  db.child("CarsInfo");



    }


}
