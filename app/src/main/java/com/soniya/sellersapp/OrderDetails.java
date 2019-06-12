package com.soniya.sellersapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener {

    TextView brandmodel;
    TextView availability;
    TextView price;
    TextView vehicleNum;
    Button soldButton;
    ArrayList<String> urlList = new ArrayList<>();
    TextView description;
    TextView ownerDetails;
    TextView color;
    TextView fuel;
    TextView locDetails;
    TextView yearDetails;
    LinearLayout collapseLinear;
    TextView showmoreless;
    TextView kmsDriven;

    DatabaseReference carInfoReference;
    DatabaseReference updateRef;
    LinearLayout gallery;

    boolean editmode=false;

    char space = ' ';
    char replacechar = '_';

    List<Bitmap> carImagesList = new ArrayList<>();

    boolean modelchanged = false;
    boolean pricechanged = false;

    String oldPriceVal= "";
    String oldModelVal= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        setTitle("Car Information");

        editmode = false;

        brandmodel = findViewById(R.id.brandModelDetails);
        locDetails = findViewById(R.id.locationdetails);
        price = findViewById(R.id.price);
        color = findViewById(R.id.colorDetails);
        //brand = findViewById(R.id.brandDetails);
        fuel = findViewById(R.id.fuelEditDetails);
        yearDetails = findViewById(R.id.yearDetails);
        vehicleNum = findViewById(R.id.vehNumDetailsView);
        collapseLinear = findViewById(R.id.collapseLinear);
        collapseLinear.setVisibility(View.GONE);
        soldButton = findViewById(R.id.soldButton);
        description = findViewById(R.id.descriptionDetails);
        ownerDetails = findViewById(R.id.ownerwhich);
        showmoreless = findViewById(R.id.showMoreLess);
        showmoreless.setOnClickListener(this);
        kmsDriven = findViewById(R.id.kmsdriven);

        HorizontalScrollView scrollView = findViewById(R.id.horizontalScrollView);
        scrollView.setOnClickListener(this);

        gallery = findViewById(R.id.imgGallery);
        gallery.setOnClickListener(this);

        updateRef = FirebaseDatabase.getInstance().getReference().child("InfoUpdates");

        Intent intent = getIntent();

        if(intent.getExtras() != null && intent.hasExtra("selVehicleNum"))  {
            // call retrieve car info
            String vehicleNo = intent.getStringExtra("selVehicleNum").replace(space, replacechar);
            vehicleNum.setText(intent.getStringExtra("selVehicleNum"));

            carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo").child(vehicleNo);
            carInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if( dataSnapshot != null && dataSnapshot.getValue()!=null)   {

                        CarInfo carInfo = dataSnapshot.getValue(CarInfo.class);

                        urlList = carInfo.getImage_uri_list();
                        loadImages();

                        brandmodel.setText(carInfo.getBrand_name() + " " + carInfo.getModel_name());
                        color.setText(carInfo.getColor());
                        locDetails.setText(carInfo.getLocation());
                        Drawable img = ContextCompat.getDrawable(getApplicationContext(), R.drawable.map_default_map_marker);
                        img.setBounds(0, 0, img.getMinimumWidth()/2, img.getMinimumHeight()/2);
                        locDetails.setCompoundDrawables(img, null, null, null);
                        price.setText(carInfo.getSellingprice());
                        if(carInfo.getDescription() !=null && !carInfo.getDescription().isEmpty()) {
                            description.setText(carInfo.getDescription());
                            description.setVisibility(View.VISIBLE);
                        }else{
                            description.setVisibility(View.GONE);
                        }
                        fuel.setText(carInfo.getFuelType());
                        yearDetails.setText(carInfo.getYear());
                        kmsDriven.setText(carInfo.getKmsDriven());
                        ownerDetails.setText("Owner : " + carInfo.getOwner());

                        if(carInfo.getAvailability().equalsIgnoreCase("Sold"))  {
                            Log.i("soni-", "this model is sold!");
                            soldButton.setText("Sold");
                            soldButton.setBackgroundColor(Color.GRAY);
                            soldButton.setEnabled(false);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(intent.hasExtra("forEdit") && intent.getBooleanExtra("forEdit", false)) {
                editmode = true;

            }
        }

        /*pricedialog = new AlertDialog.Builder(this).create();
        pricedialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("soni-which OrdDetails", Integer.toString(which));
                if(editmode) {
                    if(!price.getText().toString().equals(priceEdit.getText().toString()))  {
                        pricechanged = true;
                    }
                    oldPriceVal = price.getText().toString();
                    price.setText(priceEdit.getText());
                }
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardetail_menu, menu);

        if(editmode)    {
            menu.getItem(0).setTitle("Save");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())    {

            case R.id.editCarInfo:

                /*if(!editmode) {
                    editmode = true;
                    //makeEditable();
                    item.setTitle("Save");
                }else{
                    editmode = false;
                    item.setTitle("Edit");
                    updateCarInfo();
                }*/
                break;

            case android.R.id.home:

                onBackPressed();
                break;

            case R.id.markAvailable:

                new AlertDialog.Builder(this)
                        .setTitle("do you want to activate the order again?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //make it available agn
                                //availability.setText("Available");
                                activateOrder();
                            }
                        })
                        .setNegativeButton("no", null)
                        .show();
                break;


        }

        return true;
    }

    private void activateOrder() {

        if(availability.getText().toString().equalsIgnoreCase("Sold")) {
            availability.setText("Available");
            soldButton.setText("Available");
            soldButton.setBackgroundResource(R.drawable.round_button);
            soldButton.setEnabled(true);
            carInfoReference.child("availability").setValue("Available");
            //updateCarAvailability();

            FirebaseDataFactory dataFactory = new FirebaseDataFactory();
            dataFactory.activateOrder(vehicleNum.getText().toString().replace(space, replacechar));
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(soldButton.getText().toString().equalsIgnoreCase("Sold"))    {
            menu.findItem(R.id.markAvailable).setEnabled(true);
        }else{
            menu.findItem(R.id.markAvailable).setEnabled(false);
        }

        return true;
    }


    /*
    method to add images to imageGallery view at top of screen

     */

    private void loadImages() {

        //trying below for sliding scroll view of images

        LayoutInflater inflater = LayoutInflater.from(this);

        for(int i = 0; i < urlList.size(); i++)  {

            View view = inflater.inflate(R.layout.imgitem, gallery, false);
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageResource(R.mipmap.ic_launcher);
            //Glide.with(getApplicationContext()).load(urlList.get(i)).into(imageView);
            Picasso.with(getApplicationContext()).load(urlList.get(i)).into(imageView);
            gallery.addView(view);
        }

    }

   /* private void loadImagesToList() {
        //retrieve images from urls to list of bitmap
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for(String uri : urlList)   {
            uriArrayList.add(Uri.parse(uri));
        }

    }*/

    @Override
    public void onClick(View v) {
        
        switch (v.getId())  {

            case R.id.showMoreLess:
                if(collapseLinear.getVisibility() == View.GONE) {
                    expand();
                    showmoreless.setText("Show Less <<");
                }else {
                    collapse();
                    showmoreless.setText("Show more >>");
                }
                break;

            case R.id.soldButton:
                if(availability.getText().toString().equalsIgnoreCase("Available")) {
                    availability.setText("Sold");
                    soldButton.setText("Sold");
                    soldButton.setBackgroundColor(Color.GRAY);
                    soldButton.setEnabled(false);
                    carInfoReference.child("availability").setValue("sold");
                    updateCarAvailability();
                }
                
                break;

            case R.id.horizontalScrollView:
            case R.id.imgGallery:
                //opens all images to show
                displayCarImages();
                break;

        }
    }

    public void expand()    {
        collapseLinear.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        collapseLinear.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, collapseLinear.getMeasuredHeight());
        mAnimator.start();
    }

    public void collapse()  {
        int finalHeight = collapseLinear.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                collapseLinear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end)
    {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = collapseLinear.getLayoutParams();
                layoutParams.height = value;
                collapseLinear.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void updateCarAvailability() {

        String newstatus = availability.getText().toString();
        carInfoReference.child("availability").setValue(newstatus);
        FirebaseDataFactory dataFactory = new FirebaseDataFactory();
        dataFactory.moveToSoldHistory(vehicleNum.getText().toString().replace(space, replacechar));

    }

    private void displayCarImages() {
        //TODO
        Intent displayIntent = new Intent(getApplicationContext(), DisplayImages.class);
        displayIntent.putExtra("modelname", brandmodel.getText().toString());
        displayIntent.putExtra("urlList", urlList);
        displayIntent.putExtra("vehicle_no", vehicleNum.getText().toString().replace(space, replacechar));
        startActivity(displayIntent);

    }
}
