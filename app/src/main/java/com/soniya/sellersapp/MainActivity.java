package com.soniya.sellersapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/*import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;*/

import org.apache.commons.collections4.map.HashedMap;

import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView user;
    TextView pass;
    Button loginButton;
    TextView signupTextView;
    ConstraintLayout backLayout;
    ImageView logo;

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

        /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, String> vals = new HashedMap<>();
        vals.put("name", "Soniya");*/


        //Firebase test: Initialise database connectivity
       /* DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.getApplicationContext());

        if (databaseAdapter.alreadyLoggedIn()) {
            Intent i = new Intent(this, HomePage.class);
            startActivity(i);
            finish();
        }*/

        if(fbAdapter.checkCurrentUser()){
            Intent i = new Intent(this, HomePage.class);
            Log.i("soni-", "User already logged in");
            startActivity(i);
            finish();
        }

        user = (TextView) findViewById(R.id.userText);
        pass = (TextView) findViewById(R.id.passText);
        user.setText("");
        pass.setText("");
        loginButton = (Button) findViewById(R.id.loginButton);
        signupTextView = (TextView) findViewById(R.id.signupText);
        signupTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        backLayout = (ConstraintLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        logo = (ImageView) findViewById(R.id.logoView);
        logo.setImageResource(R.drawable.logo);
        logo.setOnClickListener(this);

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
                    Toast.makeText(this, "Enter valid username!", Toast.LENGTH_SHORT).show();
                }else {
                    //for parse server
                    /*if(new ParseDatabaseFactory().signInUser(user.getText().toString(), pass.getText().toString())){
                        Intent loginIntent = new Intent(getApplicationContext(), HomePage.class);
                        Toast.makeText(MainActivity.this, user.getText().toString() + " Logged In!", Toast.LENGTH_SHORT).show();
                        startActivity(loginIntent);
                    }*/

                    //firebase login process

                    mAuth.signInWithEmailAndPassword(user.getText().toString(), pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //signinUserSuccessful = true;
                                //Toast.makeText(this, "Login with Email is Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                //signinUserSuccessful = false;
                                if(task.getException() !=null && task.getException().getMessage()!=null) {
                                    Log.i("soni-", task.getException().getMessage());
                                }
                            }
                        }
                    });
                    /*if(fbAdapter.loginUser(user.getText().toString(), pass.getText().toString())){
                        Toast.makeText(this, "Login with Email is Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Log.i("soni-", fbAdapter.getErrorMessage());
                        Toast.makeText(this, "error-" + fbAdapter.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }*/
                }

                break;

            case R.id.logoView:

            case R.id.backLayout:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;


            default:
                break;
        }
    }
}
