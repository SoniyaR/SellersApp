package com.soniya.sellersapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/*import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;*/

import org.apache.commons.collections4.map.HashedMap;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView user;
    TextView pass;
    Button loginButton;
    TextView signupTextView;
    ConstraintLayout backLayout;
    ImageView logo;
    TextView forgotPwd;

    //in case of username login
    String emailId = "";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseAdapter fbAdapter = new FirebaseAdapter();

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finish();
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);

        Log.i("soni-multi window mode", "changed!");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode()) {
                Log.i("soni-", "in multi-window mode");

            }
        }

       if(isOnline()) {
           if (fbAdapter.checkCurrentUser()) {
               Intent i = new Intent(this, HomePage.class);
               Log.i("soni-", "User already logged in");
               startActivity(i);
               finish();
           }else {

               user = (TextView) findViewById(R.id.userText);
               pass = (TextView) findViewById(R.id.passText);
               user.setText("");
               pass.setText("");
               pass.setOnClickListener(this);
               loginButton = (Button) findViewById(R.id.loginButton);
               signupTextView = (TextView) findViewById(R.id.signupText);
               signupTextView.setOnClickListener(this);
               loginButton.setOnClickListener(this);
               backLayout = (ConstraintLayout) findViewById(R.id.backLayout);
               backLayout.setOnClickListener(this);
               logo = (ImageView) findViewById(R.id.logoView);
               logo.setImageResource(R.drawable.logo);
               logo.setOnClickListener(this);
               forgotPwd = findViewById(R.id.forgotpwd);
               forgotPwd.setOnClickListener(this);
           }
       }else{
           try {
               AlertDialog.Builder alert = new AlertDialog.Builder(this)
                       .setMessage("Not connected to Internet!")
                       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               finish();
                           }
                       });
               alert.show();
           }
           catch(Exception e)   {
               Log.i("soni-", "mainactivity-alertdialog exc - "+ e.getMessage());
           }
       }

    }

    public boolean isOnline()   {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected())   {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.signupText:
                //Log.i("soni-", "signup text clicked");
                Intent i = new Intent(getApplicationContext(), SignupForm.class);
                startActivity(i);

                break;

            case R.id.loginButton:

                if(user.getText() !=null && pass.getText() != null && (user.getText().toString().isEmpty() || user.getText().toString().contains(" ")
                        || pass.getText().toString().isEmpty() || pass.getText().toString().contains(" "))){
                    Toast.makeText(this, "Enter valid username/password!", Toast.LENGTH_SHORT).show();
                }else {
                    //firebase login process

                    if(user.getText().toString().contains("@")) {

                        mAuth.signInWithEmailAndPassword(emailId, pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //signinUserSuccessful = true;
                                    //Toast.makeText(this, "Login with Email is Successful!", Toast.LENGTH_SHORT).show();
                                    checkEmailVerified();

                                } else {
                                    //signinUserSuccessful = false;
                                    if (task.getException() != null && task.getException().getMessage() != null) {
                                        Log.i("soni-", task.getException().getMessage());
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    else{

                    }
                }

                break;

            case R.id.passText:
                if(!user.getText().toString().contains("@")) {
                    //username entered case
                    Log.i("soni-", "its not mail id");
                    emailId =  findEmailId(user.getText().toString());
                }else{
                    emailId = user.getText().toString();
                }
                break;

            case R.id.logoView:

            case R.id.backLayout:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;

            case R.id.forgotpwd:
                EditText emailEdit = new EditText(this);
                emailEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                new AlertDialog.Builder(this)
                        .setTitle("Enter your email Id")
                        .setView(emailEdit)
                        .setNeutralButton("Reset Password", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //send email for reset password
                                FirebaseAuth.getInstance().sendPasswordResetEmail(emailEdit.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("soni-", "Email sent for password reset.");
                                                    Toast.makeText(MainActivity.this, "Reset password and try login again!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }).create();
                break;

            default:
                break;
        }
    }

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

    private String findEmailId(String username)   {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("userInfo").child(encodeString(username));
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.getValue()!=null)  {
                    if(dataSnapshot.getValue() instanceof HashMap)  {

                        HashMap<String, Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();
                        emailId = hm.get("emailId").toString();

                        Log.i("soni-", "data is hashmap (username login process) " + emailId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return emailId;
    }

    private void checkEmailVerified() {

        FirebaseUser user = mAuth.getCurrentUser();
        if(user.isEmailVerified())  {
            Toast.makeText(this, "Successfully Logged in!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else    {
            Toast.makeText(this, "Email is not verified!", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
}
