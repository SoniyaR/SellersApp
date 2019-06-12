package com.soniya.sellersapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class Tab2Fragment extends Fragment implements View.OnClickListener {

    ListView carsListView;

    int contextSelPosition= 0;
    String selVehicleNum = "";

    char space = ' ';
    char replacechar = '_';
    ArrayList<String> paidforUsrs = new ArrayList<>();
    ArrayList<String> paidforCarNums = new ArrayList<>();
    ImageView dialogCarImage;
    TextView dialogModel;
    TextView dialogPrice;
    TextView dialogLocation;
    Button payButton;

    Context context;
    FirebaseAdapter fbadapter = new FirebaseAdapter();

    ArrayList<CarInfoSerial> carsArraylist = new ArrayList<>();
    ArrayList<String> activeOrders = new ArrayList<>();
    ArrayList<CarInfoSerial> otherCarslist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        carsListView = rootView.findViewById(R.id.tab2listView);
        registerForContextMenu(carsListView);
        context = getActivity();

        if(getArguments()!=null) {

            if(getArguments().getSerializable("carsArrayList") !=null) {
                carsArraylist = (ArrayList<CarInfoSerial>) getArguments().getSerializable("carsArrayList");
            }
            /*else if(getArguments().getString("paymentStatus") !=null)   {
                paymentStatus = getArguments().getString("paymentStatus");
            }*/
        }



        if(carsArraylist !=null && carsArraylist.size()>0) {
            otherCarslist.clear();
           // Log.i("soni-", "we have carsarraylist tab2frag , size = " + carsArraylist.size());
            //CustomAdapter carListAdapter = new CustomAdapter(Tab2Fragment.this.getActivity(), carsArraylist, R.layout.carslist_layout);
//            carsListView.setAdapter(carListAdapter);
//            carListAdapter.notifyDataSetChanged();

            AppListeners carInfoInstance = new AppListeners();
            carInfoInstance.setCarNumbersListener(new AppListeners.CarNumbersListener() {
                @Override
                public void onRetrieve(ArrayList<String> data, ArrayList<String> paidforCars) {
                    if (data != null && data.size() > 0) {
                        activeOrders = data;
                       // paidforUsrs = paidforUsers;
                        paidforCarNums = paidforCars;

                        for (CarInfoSerial carInfoSerial : carsArraylist) {
                            //Log.i("soni-", carInfoSerial.getVehicle_no() + paymentStatus);
                            if (!activeOrders.contains(carInfoSerial.getVehicle_no())) {
                                otherCarslist.add(carInfoSerial);
                            }
                        }

                        if (otherCarslist != null && otherCarslist.size() > 0) {
                            //Log.i("soni-", "we have othercarslist tab2frag");
                            CustomAdapter carListAdapter = new CustomAdapter(context, otherCarslist, R.layout.carslist_layout);
                            carsListView.setAdapter(carListAdapter);
                        }
                    } else {

                        CustomAdapter carListAdapter = new CustomAdapter(context, carsArraylist, R.layout.carslist_layout);
                        carsListView.setAdapter(carListAdapter);

                    }
                }

                @Override
                public void onProgress() {
                    Toast.makeText(context, "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
            carsListView.setAdapter(arrayAdapter);

        }

        carsListView.setOnItemClickListener((parent, view, position, id) -> {

            CarInfoSerial selectedCarinfo = otherCarslist.get(position);
            if(paidforCarNums !=null && !paidforCarNums.isEmpty() && paidforCarNums.contains(selectedCarinfo.getVehicle_no())
                    /*|| (paidforUsrs!=null && !paidforUsrs.isEmpty() || paidforUsrs.contains(selectedCarinfo.getPostedBy()))*/)  {
                Intent intent = new Intent(context, OrderDetails.class);
                intent.putExtra("selVehicleNum", selectedCarinfo.getVehicle_no());
                startActivity(intent);

            }
            else {

                AlertDialog.Builder paymentAlert = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.payment_dialog_layout, null);
//                dialogCarImage= dialogView.findViewById(R.id.carimgdialog);
//                Picasso.with(context).load(Uri.parse(selectedCarinfo.getThumbnailUriString())).resize(0, 140).into(dialogCarImage);
//                dialogModel= dialogView.findViewById(R.id.dialogmodel);
//                dialogModel.setText(selectedCarinfo.getBrand_name() + " " + selectedCarinfo.getModel_name());
//                dialogPrice= dialogView.findViewById(R.id.dialogprice);
//                dialogPrice.setText(selectedCarinfo.getSellingprice());
                payButton = dialogView.findViewById(R.id.payButtonDialog);
                payButton.setOnClickListener(this);
                paymentAlert.setView(dialogView);
                paymentAlert.show();
            }

        });

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.action_tab2_context, menu);
        menu.setHeaderTitle("Select Action");
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextSelPosition = contextMenuInfo.position;
        selVehicleNum = otherCarslist.get(contextSelPosition).getVehicle_no().replace(space, replacechar);
        if(selVehicleNum != null) {
            Log.i("soni-hp-veh", selVehicleNum);
            Log.i("soni-hp-id", String.valueOf(v.getId()));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId())   {

            case R.id.viewRecord:
                Log.i("soni-contextmenu", "tab2 - view record selected");
                //new FirebaseDataFactory().deleteRecord(selVehicleNum);
                return true;


        }

        return super.onContextItemSelected(item);

    }

    private String decodeUsername(String string)    {
        return string.replace(",", ".");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())  {
            /*case R.id.dialogimg1:
                Log.i("soni-", "img1 dialog selected!");
                break;

            case R.id.dialogimg2:
                Log.i("soni-", "img2 dialog selected!");
                break;*/

            case R.id.payButtonDialog:
                Log.i("soni-", "pay button dialog selected!");

                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("userInfo").child(decodeUsername(fbadapter.getCurrentUser()));
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot !=null && dataSnapshot.getValue() !=null) {
                            UserInformation userinfo = dataSnapshot.getValue(UserInformation.class);
                            if (userinfo != null && userinfo.getEmailId() != null) {
                                Log.i("soni-PaymentGateway", "email-" + userinfo.getEmailId());
                                Log.i("soni-PaymentGateway", "mob-" + userinfo.getMobileNo());

                                UserInfoSerial infoSerial = new UserInfoSerial(userinfo);
                                Intent payIntent = new Intent(context, PaymentGateway.class);
                                payIntent.putExtra("UserInfo", infoSerial);
                                startActivity(payIntent);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
        }

    }
}
