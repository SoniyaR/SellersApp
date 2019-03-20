package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParseDatabaseFactory {

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage = "";
    boolean isSignupSuccessful = false;
    boolean isSignInSuccessful = false;
    //used in Homepage class

    public List<HashMap<String, Object>> retriveCarList() {

        List<HashMap<String, Object>> hashMapList = new ArrayList<>();

        ParseQuery<ParseObject> chatsQuery = ParseQuery.getQuery("activeOrders");
        chatsQuery.orderByDescending("createdAt");
        chatsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {
                    if (objects.size() > 0) {
                        for (ParseObject vehicle : objects) {
                            //activeOrders.add(vehicle.getString("modelName"));
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("model_name", (String) vehicle.getString("modelName"));
                            //hm.put("highlight", (String)vehicle.getString("summary"));
                            hm.put("sellingprice", (String) vehicle.getString("sellingprice"));

                            if (vehicle.getParseFile("carimage") != null) {
                                ParseFile carFile = vehicle.getParseFile("carimage");
                                try {
                                    byte[] arr = carFile.getData();
                                    Bitmap carBitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                                    hm.put("carImage", carBitmap);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                //hm.put("carImage", BitmapFactory.decodeResource(getResources(),R.drawable.nocarpicture));
                            }

                            hashMapList.add(hm);
                        }
                    }
                }

            }
        });

        return hashMapList;

    }


    public boolean signupUser(String username, String password){

        ParseUser userNew = new ParseUser();
        userNew.setUsername(username);
        userNew.setPassword(password);
        userNew.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    isSignupSuccessful = true;
                }
                else{
                    isSignupSuccessful = false;
                    setErrorMessage(e.getMessage());
                }
            }
        });

        return isSignupSuccessful;

    }

    public boolean signInUser(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    isSignInSuccessful = true;
                } else {
                    isSignInSuccessful = false;
                    setErrorMessage(e.getMessage());
                }
            }
        });
        return isSignInSuccessful;
    }


}
