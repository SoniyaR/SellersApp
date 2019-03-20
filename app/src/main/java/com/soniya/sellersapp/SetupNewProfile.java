package com.soniya.sellersapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

public class SetupNewProfile extends AppCompatActivity implements View.OnClickListener {

    TextView newUsername;
    TextView location;
    String currentEmailId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_new_profile);

        setTitle("Set up your profile");

        Intent intent = getIntent();
        if(intent.getExtras() !=null && intent.getStringExtra("emailId") !=null){
            currentEmailId = intent.getStringExtra("emailId");
        }

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.setprofLayout);
        layout.setOnClickListener(this);

        newUsername = (TextView) findViewById(R.id.editUsername);
        location = (TextView) findViewById(R.id.editLocation);

        if(!currentEmailId.isEmpty() && currentEmailId.contains("@"))   {
            newUsername.setText(currentEmailId.split("@")[0]);
        }

        Button nextButton = (Button)  findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.nextButton:
                if(newUsername.getText()!=null && !newUsername.getText().toString().isEmpty()) {
                    if (!currentEmailId.equals("") && !currentEmailId.isEmpty()) {
                        FirebaseDataFactory dataFactory = new FirebaseDataFactory();
                        dataFactory.addNewProfileInfo(currentEmailId, newUsername.getText().toString(), location.getText().toString());

                        Intent i = new Intent(getApplicationContext(), HomePage.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else{
                        Log.i("soni-", "Something went wrong! (In SetupNewProfile)");
                    }

                }else{
                    newUsername.setError("Username is mandatory");
                }
                break;

            case R.id.location:
                //find current location

                break;

            case R.id.setprofLayout:
                //hide keyboard
                InputMethodManager ipMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ipMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                break;

            default:
                break;
        }

    }
}
