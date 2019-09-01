package com.soniya.sellersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.pojo.ProfileStats;
import com.soniya.sellersapp.pojo.UserInformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    String user;

    List<HashMap<String, Object>> hmlist = new ArrayList<>();
    SimpleAdapter adapter;

    ListView feedListView;
    ImageView profilePictureView;
    Bitmap currentPP = null;
    TextView myname;
    TextView mymobile;
    TextView invWorth;
    TextView invItems;
    TextView soldWorth;
    TextView soldItems;
    TextView avgSellWorth;
    TextView sellThisMonth;
    BarChart chart;
    Button showChart;
    boolean chartDataAvailable = false;
    HashMap<String, Date> hm;
    ArrayList<Date> dateList;


    FirebaseAdapter fbAdapter = new FirebaseAdapter();
    ProfileListener listenerObj;

    TextView followingTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.editprofile:
//                Intent i = new Intent(getApplicationContext(), UploadNewInfo.class);
//                startActivity(i);
                break;

            case R.id.logout:
                fbAdapter.logoutUser();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                break;

            default:
                return false;
        }

        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setTitle("My Profile");

        user = fbAdapter.getCurrentUser();

        profilePictureView = findViewById(R.id.profilePicView) ; //TODO use storage and set later
        profilePictureView.setImageResource(R.drawable.noprofilepic);

        myname = findViewById(R.id.myname);

        mymobile = findViewById(R.id.mymobilenum);
        Drawable img = ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.sym_action_call);
        img.setBounds(0, 0, img.getMinimumWidth(), 0);
        mymobile.setCompoundDrawables(img, null, null, null);

        invWorth = findViewById(R.id.inventoryworth);
        invItems = findViewById(R.id.inventoryitems);
        soldWorth = findViewById(R.id.soldworth);
        soldItems = findViewById(R.id.solditems);
        avgSellWorth = findViewById(R.id.avgsoldworth);
        sellThisMonth = findViewById(R.id.sellmonthVal);
        showChart = findViewById(R.id.showChartButton);
        showChart.setOnClickListener(this);

        chart = findViewById(R.id.barchart);
        hm = new HashMap<>();

        if(user !=null){
            TextView myname = findViewById(R.id.myname);
            myname.setText(user);
        }

        retrieveProfile();

        listenerObj = new ProfileListener();
        listenerObj.setRetrieveProfileStats(new ProfileListener.RetrieveStatsListener() {
            @Override
            public void onDataRetrieve(ProfileStats data) {
                invWorth.setText(String.valueOf(data.getTotalWorth()) + " " + data.getWorthUnit());
                invItems.setText(String.valueOf(data.getAvailableInventory()));
                soldWorth.setText(String.valueOf(data.getSoldWorth()) + " " + data.getWorthUnit());
                soldItems.setText(String.valueOf(data.getSoldInventory()));
                avgSellWorth.setText(String.valueOf(data.getAvgSellperMonth()) + " " + data.getWorthUnit());
                sellThisMonth.setText(String.valueOf(data.getAvgSellThisMonth()));
            }

            @Override
            public void onDataCancelled() {

            }
        });

    }

    public static String encodeString(String string) {
        if(string == null || (string !=null && string.isEmpty())){
            return "";
        }
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        if(string == null || (string !=null && string.isEmpty())){
            return "";
        }
        return string.replace(",", ".");
    }


    public void retrieveProfile(){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("userInfo").child(encodeString(user));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
//                    Log.i("soni-profile", dataSnapshot.getValue().toString());
                    myname.setText(decodeString(user));

                    UserInformation userinfo = dataSnapshot.getValue(UserInformation.class);
                    mymobile.setText(userinfo.getMobileNo());
                    hm = userinfo.getSoldOrders();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public ArrayList<BarEntry> getBarChartData(){
        ArrayList<BarEntry> entries = new ArrayList<>();
       // entries.add(new BarEntry());
        if(hm!=null && !hm.isEmpty()){
            Date signup_date = fbAdapter.getSignupDate();
            Date curr_date = new Date();
            long dur = curr_date.getTime() - signup_date.getTime();
            Log.i("soni-", "date diff is " + dur);
            chartDataAvailable = true;
            for(String key: hm.keySet()){
                dateList.add(hm.get(key));
                Log.i("soni-","barchart- " + hm.get(key).toString());
            }
            //if diff betn signup date and curr date is less than 30days
            //show days data



            //find num of cars sold in last 3 months

        }

        entries.add(new BarEntry(1, 13));
        entries.add(new BarEntry(2, 10));
        entries.add(new BarEntry(3, 8));
        entries.add(new BarEntry(4, 14));
        chartDataAvailable = true;

        return entries;

    }


/*

    public void retrieveMyProfile(){
        ParseQuery<ParseObject> userinfo = ParseQuery.getQuery("userInfo");
        userinfo.whereEqualTo("username", encodeString(user));
        userinfo.orderByDescending("updatedAt");
        userinfo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e== null && objects.size() > 0){
                    ParseObject obj = objects.get(0);

                    ParseFile imgFile= obj.getParseFile("profilePicture");
                    if(imgFile !=null) {
                        try {
                            byte[] arr = imgFile.getData();
                            currentPP = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                            profilePictureView.setImageBitmap(currentPP);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        currentPP = BitmapFactory.decodeResource(getResources(), R.drawable.noprofilepic);
                    }

                    currentAboutme = obj.getString("aboutme");
                    if(currentAboutme != null && !currentAboutme.isEmpty()) {
                        aboutmeTextView.setText(currentAboutme);
                    }else{
                        aboutmeTextView.setText("...");
                    }

                    if(obj.get("following")!=null && obj.getList("following")!=null){

                        int followingCount = obj.getList("following").size();
                        followingTextView.setText("following\n" + String.valueOf(followingCount));

                    }
                }
            }
        });


    }
*/

   /* public void retrieveFeedContent(){

        ParseQuery<ParseObject> feedObj = ParseQuery.getQuery("activeOrders");
        feedObj.whereEqualTo("username", encodeString(user));
        feedObj.orderByDescending("createdAt");
        feedObj.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null && objects !=null) {
                    if(objects.size() > 0)  {

                        for(ParseObject post: objects) {
                            //Log.i("soni-", "found my feed content "+user);
                            HashMap<String, Object> hm = new HashMap();

                            //get username, post time and profile picture
                            String name = post.getString("username");
                            hm.put("username", name);

                            Date date = post.getCreatedAt();
                            DateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
                            if(date !=null) {
                                String timeStr = formatter.format(date);
                                // 2019-02-14T07:55:59.901Z
                                hm.put("time", timeStr);
                            }

                            if(currentPP != null){
                                hm.put("profilepic", currentPP);
                            }else if(post.getParseFile("profilePicture") !=null) {
                                ParseFile img = post.getParseFile("profilePicture");
                                    try {
                                        byte[] dataArr = img.getData();
                                        if (dataArr.length > 0) {
                                            Bitmap ppImg = BitmapFactory.decodeByteArray(dataArr, 0, dataArr.length);
                                            hm.put("profilepic", ppImg);
                                        }
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                            }
                            else {
                                hm.put("profilepic", BitmapFactory.decodeResource(getResources(), R.drawable.noprofilepic));
                            }

                            //get feed caption and feed picture
                            String caption = post.getString("caption");

                            //get feed caption and feed picture
                            if(post.get("feedImage") !=null) {
                                ParseFile feedImag = post.getParseFile("feedImage");
                                byte [] arr = new byte[0];
                                try {
                                    arr = feedImag.getData();
                                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                                    hm.put("feedimage", imageBitmap);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            if(post.getString("caption") !=null && !post.getString("caption").isEmpty()) {
                                String feedCaption = post.getString("caption");
                                //Log.i("caption", feedCaption);
                                hm.put("caption", feedCaption);
                            }

                            hmlist.add(hm);
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(MyProfile.this, "Nothing on feed yet!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MyProfile.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }*/
/*
    public void addNewPost(){

        Intent i = new Intent(getApplicationContext(), NewPost.class);
        startActivity(i);
        //finish();

    }

    */

    @Override
    public void onClick(View v) {

        switch(v.getId())   {

            case R.id.showChartButton:
                Log.i("soni-", "showChart clicked " + chartDataAvailable);
                loadChart();
                if(chartDataAvailable){

                    showChart.setVisibility(View.INVISIBLE);
                    chart.setVisibility(View.VISIBLE);

                }

                break;

            default:
                break;
        }

    }

    private void loadChart() {
        BarDataSet dataSet = new BarDataSet(getBarChartData(), "Sales per month");
                            chart.animateY(3000);

            ArrayList<String> months = new ArrayList<>();
                            months.add("Jan");
                            months.add("Feb");
                            months.add("Mar");
                            months.add("Apr");
            BarData data= new BarData( dataSet);
                            chart.setData(data);
    }
}
