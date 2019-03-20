package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignupForm extends AppCompatActivity {

    EditText userEmailView;
    EditText passwordView;
    EditText repPassword;
    Button signupButton;

    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    FirebaseDataFactory database = new FirebaseDataFactory();
    FirebaseAuth aAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        userEmailView = (EditText) findViewById(R.id.newMailText);
        userEmailView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        passwordView = (EditText) findViewById(R.id.newpassText);
        repPassword = (EditText) findViewById(R.id.repPasswordText);
        passwordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repPassword.setTag("repeatPassword");

        signupButton = (Button) findViewById(R.id.signupButton) ;
        //TODO when all fields are error-free, enable the signup button
        //signupButton.setEnabled(false);
        //signupButton.

        Log.i("soni-", "in signupform class");
        userEmailView.addTextChangedListener(new SignupValidation(userEmailView));
        passwordView.addTextChangedListener(new SignupValidation(passwordView));
        repPassword.addTextChangedListener(new SignupValidation(repPassword));

    }

    public void signupClicked(View view){

        boolean allgood=false;
        Editable emailText = userEmailView.getText();
        Editable passwordText = passwordView.getText();

        if(emailText!=null && !emailText.toString().isEmpty() && !emailText.toString().trim().equals("")
                && passwordText!=null  && !passwordText.toString().isEmpty() && !passwordText.toString().trim().equals(""))   {
                if(repPassword.getText()!=null && repPassword.getText().toString().equals(passwordText.toString())) {
                    allgood = true;
                }
        }
        else if(emailText.toString().isEmpty() && emailText.toString().trim().equals("")){
            Toast.makeText(this, "Username can not be blank!", Toast.LENGTH_SHORT).show();
            userEmailView.setError("Username can not be blank!");
            allgood=false;
        }
        else if(passwordText.toString().isEmpty() && passwordText.toString().trim().equals("")){
            Toast.makeText(this, "Password can not be blank!", Toast.LENGTH_SHORT).show();
            passwordView.setError("Password can not be blank!");
            allgood=false;
        }

        if(allgood && validateSignup()) {

            /*ParseDatabaseFactory db = new ParseDatabaseFactory();
            if(db.signupUser(userView.getText().toString(), passwordView.getText().toString())){
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else {
                Log.i("soni-", "Signup Error"+ db.getErrorMessage());
            }*/

            //TODO Firebase signing up: can user asynctask listener

            /*
            //boolean signupSuccess = fbAdapter.signupUser(this, emailText.toString(), passwordText.toString());

            Log.i("soni-signingup", emailText.toString()+ " " + passwordText.toString());

            hold();

            Log.i("soni-signingup", "after 6 secs "+signupSuccess);

            if(fbAdapter.signupUser(this, emailText.toString(), passwordText.toString())    {
                Log.i("soni-signup", "User signup with Email successful");
                Intent intent = new Intent(getApplicationContext(), SetupNewProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("emailId", emailText.toString());
                startActivity(intent);
               // Toast.makeText(this, "User signup with Email successful", Toast.LENGTH_SHORT).show();
                //goto homepage

            }else{
                Log.i("soni-errorsignup", fbAdapter.getErrorMessage());
               // Toast.makeText(this,  fbAdapter.getErrorMessage(), Toast.LENGTH_LONG).show();
                //Toast.makeText(this, fbAdapter.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }*/


            Log.i("soni-signingup", emailText.toString()+ " " + passwordText.toString());
            aAuth.createUserWithEmailAndPassword(emailText.toString(), passwordText.toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(getApplicationContext(), SetupNewProfile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("emailId", emailText.toString());
                        startActivity(intent);
                        //Log.i("soni-adapterclass", "Signup done!");
                    }else{
                         Log.i("soni-signup error", task.getException().getMessage());
                    }

                }
            });

        }else if(!validateSignup()){
            Log.i("soni-", "error in the form");
        }
    }

    private void hold() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("soni-signupSuccess", "holding");
            }
        }, 6000);
    }

    private boolean validateSignup() {

        return (isErrorFree(userEmailView) && isErrorFree(passwordView) && isErrorFree(repPassword));

    }

    private boolean isErrorFree(TextView view){
        if(TextUtils.isEmpty(view.getError())){
            return true;
        }else{
            return false;
        }
    }

    /*public static boolean invalidUsername(String username) {
        if(username.length()<6) {
            return true;
        }
        Pattern pattern;
        Matcher matcher;

        final String usernamerules = ".*[@$!%*?&/].*";
        pattern = Pattern.compile(usernamerules);
        matcher = pattern.matcher(username);

        return matcher.matches();

    }

    public boolean validPassword(String pass){
        if(isValidPassword(pass))    {
            //passwordMsg = "Password must contain at least 1 Upper case, 1 Lower case letter, 1 Symbol & 1 Number!";
            passwordView.setError("Password must contain at least 1 Upper case, 1 Lower case letter, 1 Symbol & 1 Number!");
            return false;
        }

        if(pass.length() < 6){
            //passwordMsg = "Password should be at least 6 characters!";
            passwordView.setError("Password should be at least 6 characters!");
            return false;
        }

        return true;
    }

    public boolean isValidPassword(String pass){
        Pattern pattern;
        Matcher matcher;
       *//* final String passwordRules = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$";
        final String pwdrules = "ˆ(?=.*[\\W])(?=.*[\\d])(?=.*[A-Z])(?=.*[a-z])(?=.{6,})$";
        *//*
        final String pwdrules = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
        pattern = Pattern.compile(pwdrules);
        matcher = pattern.matcher(pass);

        return matcher.matches();
    }*/

    public void hideKeyboard(View v){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }

}

