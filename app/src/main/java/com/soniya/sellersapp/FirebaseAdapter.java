package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseAdapter {

    private FirebaseAuth mAuth;

    boolean signinUserSuccessful=false;
    boolean signupUserSuccessful = false;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage = "";


    public FirebaseAdapter(){
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    signinUserSuccessful = true;
                }else{
                    signinUserSuccessful = false;
                    setErrorMessage(task.getException().getMessage());
                }
            }
        });

        return signinUserSuccessful;
    }

    /*
    returns true if current user exists (i.e. logged in)
     */
    public boolean checkCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null) {
            //Log.i("soni-",  user.getDisplayName()+ " " + user.getEmail() + " " + user.getUid());
            return true;
        }
        return false;
    }

    /*
    return current user (String)
     */
    public String getCurrentUser()  {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user !=null) {
            //Log.i("soni-",  user.getDisplayName()+ " " + user.getEmail() + " " + user.getUid());
            if(user.getDisplayName()!=null && !user.getDisplayName().isEmpty()) {
                return user.getDisplayName();
            }else{
                return user.getEmail().split("@")[0];
            }
        }
        return "Not known";
    }

    public FirebaseUser getFirebaseUser()
    {
        return mAuth.getCurrentUser();
    }
    public boolean signupUser(Activity context, String email, String password)  {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    signupUserSuccessful = true;
                    Log.i("soni-adapterclass", "Signup done!");
                }else{
                    signupUserSuccessful = false;
                    setErrorMessage(task.getException().getMessage());
                   // Log.i("soni-signup error", getErrorMessage());
                }

            }
        });

        return signupUserSuccessful;
    }

    public void logoutUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null) {
            mAuth.signOut();
        }
    }


}
