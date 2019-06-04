package com.soniya.sellersapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.Date;

public class AddNewLead extends AppCompatActivity implements View.OnClickListener {

    EditText brandName;
    EditText modelName;
    EditText location;
    EditText price;
    EditText customerName;
    EditText mobileNum;
    EditText emailId;

    Button addleadButton;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    boolean requestReady;
    FirebaseAdapter fbadapter;

    private String encodeString(String s) {
        if(s == null || (s !=null && s.isEmpty())){
            return "";
        }
        s = s.replace(" ", "_");
        s = s.replace(".", ",");
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lead);
        setTitle("Add new Lead");

        requestReady = false;
        fbadapter = new FirebaseAdapter();

        addleadButton = findViewById(R.id.addLeadButton);
        addleadButton.setOnClickListener(this);

        brandName = findViewById(R.id.leadbrandEdit);
        modelName = findViewById(R.id.leadmodelEdit);
        location = findViewById(R.id.leadlocationEdit);
        price = findViewById(R.id.leadpriceEdit);
        customerName = findViewById(R.id.custNameEdit);
        mobileNum = findViewById(R.id.mobNumEdit);
        emailId= findViewById(R.id.emailidEdit);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId())   {
            case R.id.addLeadButton:
                LeadRequest request = new LeadRequest();

                String brand = brandName.getText().toString();
                String model = modelName.getText().toString();
                String loc = location.getText().toString();
                String pric = price.getText().toString();
                String customer = customerName.getText().toString();
                String mobnum = mobileNum.getText().toString();

                if(brand.isEmpty())    {
                    brandName.setError("Field Cannot be Empty");
                }
                if(model.isEmpty())  {
                    modelName.setError("Field Cannot be Empty");
                }
                if(loc.isEmpty())  {
                    location.setError("Field Cannot be Empty");
                }
                if(pric.isEmpty())  {
                    price.setError("Field Cannot be Empty");
                }
                if(customer.isEmpty())  {
                    customerName.setError("Field Cannot be Empty");
                }
                if(mobnum.isEmpty())  {
                    mobileNum.setError("Field Cannot be Empty");
                }
                if(mobnum.length() != 10)   {
                    mobileNum.setError("Enter correct mobile number");
                }

                if(!brand.isEmpty() && !model.isEmpty() && !loc.isEmpty()
                        && !pric.isEmpty() && !customer.isEmpty() && !mobnum.isEmpty() && mobnum.length() == 10)  {
                    requestReady =  true;
                }

                if(requestReady) {
                    request.setLead_brand(brand);
                    request.setLead_model(model);
                    request.setLead_location(loc);
                    request.setLead_price(pric);

                    request.setCustomer_name(customer);
                    request.setMobile_no(mobnum);
                    request.setEmailId(emailId.getText().toString());

                    addLeadRequest(request);

                }else{

                    Toast.makeText(this, "Some mandatory fields are empty!", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    public void addLeadRequest(LeadRequest request)    {

        DatabaseReference leadRef = db.child("LeadRequests");
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String timeStampStr = encodeString(timestamp.toString().substring(0, 18));
        leadRef.child(fbadapter.getCurrentUser()).child(timeStampStr).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("soni-", "new lead request added at " + timeStampStr);

                Intent i = new Intent(getApplicationContext(), HomePage.class);
                i.putExtra("gotoTab", 2);
                startActivity(i);
            }
        });

    }
}
