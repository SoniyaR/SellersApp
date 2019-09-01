package com.soniya.sellersapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soniya.sellersapp.AppListeners;
import com.soniya.sellersapp.adapters.AllOrdersFragmentAdapter;
import com.soniya.sellersapp.adapters.OrdersFragmentAdapter;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;
import com.soniya.sellersapp.OrderDetails;
import com.soniya.sellersapp.PaymentGateway;
import com.soniya.sellersapp.R;
import com.soniya.sellersapp.pojo.UserInfoSerial;
import com.soniya.sellersapp.pojo.UserInformation;
import com.soniya.sellersapp.adapters.FirebaseAdapter;

import java.util.ArrayList;

public class AllOrdersFragment extends Fragment implements View.OnClickListener {

    //    ListView carsListView;
    GridView carsGridView;

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

    ProgressBar progressBar;
    TextView nodataText;
    AllOrdersFragmentAdapter adapter;
    Context context;
    FirebaseAdapter fbadapter = new FirebaseAdapter();
    ArrayList<CarInfo> allCarslist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_orders, container, false);
        carsGridView = rootView.findViewById(R.id.grid_view);
        registerForContextMenu(carsGridView);
        context = getActivity();
        progressBar = rootView.findViewById(R.id.progress_allorders_frag);
        nodataText = rootView.findViewById(R.id.nodata_allorders_frag);
        nodataText.setVisibility(View.GONE);

        adapter = new AllOrdersFragmentAdapter(context, allCarslist, R.layout.cars_grid_layout);
        carsGridView.setAdapter(adapter);

        carsGridView.setOnItemClickListener((parent, view, position, id) -> {

            CarInfo selectedCarinfo = allCarslist.get(position);
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

    public void loadData(){

        allCarslist.clear();
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");
        carInfoReference.orderByChild("createdDateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.hasChildren()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        CarInfo carInfo = ds.getValue(CarInfo.class);
                        carInfo.setVehicle_no(ds.getKey());
                        allCarslist.add(carInfo);
                        adapter.notifyDataSetChanged();
                    }
                    nodataText.setVisibility(View.GONE);
                }else{
                    nodataText.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
                nodataText.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.action_tab2_context, menu);
        menu.setHeaderTitle("Select Action");
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextSelPosition = contextMenuInfo.position;
        selVehicleNum = allCarslist.get(contextSelPosition).getVehicle_no().replace(space, replacechar);
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

            case R.id.payButtonDialog:
                Log.i("soni-", "pay button dialog selected!");

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }else {
                    //continue checkout process
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("userInfo").child(decodeUsername(fbadapter.getCurrentUser()));
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
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
                }

                break;
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //continue to checkout process

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("userInfo").child(decodeUsername(fbadapter.getCurrentUser()));
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
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

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
