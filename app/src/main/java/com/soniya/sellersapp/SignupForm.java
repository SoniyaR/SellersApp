package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.soniya.sellersapp.adapters.FirebaseAdapter;


public class SignupForm extends AppCompatActivity implements View.OnClickListener {

    EditText userEmailView;
    EditText passwordView;
    EditText repPassword;
    Button signupButton;
    String curr_pwd = "";
    Button signupGoogleButton;
    ScrollView signupbackscrolllayout;
    ConstraintLayout signupback;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    FirebaseDataFactory database = new FirebaseDataFactory();
    FirebaseAuth aAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        signupbackscrolllayout = findViewById(R.id.signupLayout);
        signupbackscrolllayout.setOnClickListener(this);
        signupback = findViewById(R.id.signupback);
        signupback.setOnClickListener(this);

        userEmailView = findViewById(R.id.newMailText);
        userEmailView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        passwordView = findViewById(R.id.newpassText);
        repPassword = findViewById(R.id.repPasswordText);
        passwordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repPassword.setTag("repeatPassword");
        signupGoogleButton = findViewById(R.id.signupGoogle);
        signupGoogleButton.setOnClickListener(this);
        //TODO signin with google

        signupButton = findViewById(R.id.signupButton) ;
        signupButton.setOnClickListener(this);

        Log.i("soni-", "in signupform class");
        userEmailView.addTextChangedListener(new TextValidation(userEmailView));
        passwordView.addTextChangedListener(new TextValidation(passwordView));
        repPassword.addTextChangedListener(new TextValidation(repPassword, this));
        repPassword.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void signupClicked(){

        boolean allgood=false;
        Editable emailText = userEmailView.getText();
        Editable passwordText = passwordView.getText();

        if(emailText!=null && !emailText.toString().isEmpty() && !emailText.toString().trim().equals("")
                && passwordText!=null  && !passwordText.toString().isEmpty() && !passwordText.toString().trim().equals(""))   {
                if(repPassword.getText()!=null && repPassword.getText().toString().equals(passwordText.toString())) {
                    allgood = true;
                }else{
                    repPassword.setError("Password does not match!");
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

            Intent intent = new Intent(getApplicationContext(), SetupNewProfile.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("emailId", emailText.toString());
            intent.putExtra("password", passwordText.toString());
            startActivity(intent);

        }else if(!validateSignup()){
            Toast.makeText(this, "Error in one or more fields!", Toast.LENGTH_SHORT).show();
            //Log.i("soni-", "error in the form");
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

    @Override
    public void onClick(View v) {

        switch (v.getId())  {
            case R.id.repPasswordText:
                curr_pwd = passwordView.getText().toString();
                SharedPreferences preferences = getSharedPreferences("com.soniya.sellersapp", MODE_PRIVATE);
                preferences.edit().putString("curr_pwd", curr_pwd).apply();
                break;

            case R.id.signupButton:

                signupClicked();
                break;

            case R.id.signupGoogle:


                break;

            case R.id.signupback:
            case R.id.signupLayout:
                hideKeyboard(v);
                break;
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i("soni-", "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.i("soni-", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("soni", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignupForm.this, user.getEmail() + " " + user.getDisplayName() + " " + user.getPhoneNumber(), Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("soni-", "signInWithCredential:failure", task.getException());
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }
}

