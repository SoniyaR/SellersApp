package com.soniya.sellersapp;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class DatabaseAdapter {

    Context applicationContext=null;

    public DatabaseAdapter(Context applicationContext){
        this.applicationContext = applicationContext;

        initDatabase();
    }


    private void initDatabase() {
        //go to http://18.222.158.81:80/apps
        //username: user
        //password: AdYwvkNbhOI5

        Parse.initialize(new Parse.Configuration.Builder(applicationContext)
                .applicationId("43896d2dc980a22532259c8f8248619f7aaf30f3")
                .clientKey("fdfd872f6b6bf51c5744355b01fb1eaaad9acda0")
                .server("http://18.222.158.81:80/parse")
                .build()
        );

        //setting access control
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    public boolean alreadyLoggedIn(){
        if(ParseUser.getCurrentUser() != null){
            return true;
        }else {
            return false;
        }
    }


}
