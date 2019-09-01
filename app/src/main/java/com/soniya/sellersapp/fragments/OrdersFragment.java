package com.soniya.sellersapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soniya.sellersapp.AppListeners;
import com.soniya.sellersapp.adapters.AllOrdersFragmentAdapter;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;
import com.soniya.sellersapp.adapters.OrdersFragmentAdapter;
import com.soniya.sellersapp.FirebaseDataFactory;
import com.soniya.sellersapp.OrderDetails;
import com.soniya.sellersapp.R;
import com.soniya.sellersapp.UploadNewInfo;
import com.soniya.sellersapp.adapters.FirebaseAdapter;
import com.soniya.sellersapp.utils.InternetUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class OrdersFragment extends Fragment {

    GridView carsGridView;
    int contextSelPosition= 0;
    String selVehicleNum = "";
    FloatingActionButton addCarInfoButton;
    char space = ' ';
    char replacechar = '_';

    ArrayList<CarInfoSerial> carsArraylist = new ArrayList<>();
    ArrayList<String> activeOrders = new ArrayList<>();
    ArrayList<CarInfo> myCarslist = new ArrayList<>();

    AllOrdersFragmentAdapter carListAdapter;
    Context context;
   //stats below
    long unsoldWorth;
    FirebaseAdapter fbAdapter;
    TextView nodataText;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        context = getActivity();
        fbAdapter = new FirebaseAdapter();
        nodataText = rootView.findViewById(R.id.nodata_orders_frag);
        progressBar = rootView.findViewById(R.id.progress_orders_frag);

        addCarInfoButton = rootView.findViewById(R.id.addnewCarInfo);
        addCarInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new FirebaseAdapter().getFirebaseUser().isEmailVerified()) {
                    Intent i = new Intent(context, UploadNewInfo.class);
                    startActivity(i);
                } else {
                    Log.i("soni-tab1", "cant add data, email not verified");
                }
            }
        });

        carsGridView = rootView.findViewById(R.id.orders_grid_view);
//        carsListView.setVisibility(View.GONE);
        myCarslist.clear();
        carListAdapter = new AllOrdersFragmentAdapter(context, myCarslist, R.layout.cars_grid_layout);
        carsGridView.setAdapter(carListAdapter);

        registerForContextMenu(carsGridView);

        unsoldWorth = 0;

        carsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrderDetails.class);
                intent.putExtra("selVehicleNum", myCarslist.get(position).getVehicle_no());
                startActivity(intent);

            }
        });

        return rootView;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.action_tab1_context, menu);
        menu.setHeaderTitle("Select Action");
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contextSelPosition = contextMenuInfo.position;
        selVehicleNum = myCarslist.get(contextSelPosition).getVehicle_no().replace(space, replacechar);
        if(selVehicleNum != null) {
            Log.i("soni-hp-veh", selVehicleNum);
            Log.i("soni-hp-id", String.valueOf(v.getId()));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId())   {

            case R.id.deleteRecord:
                Log.i("soni-contextmenu", "deleteRecord selected " + selVehicleNum);
                new FirebaseDataFactory().deleteRecord(selVehicleNum);
                return true;

            case R.id.editRecord:
                Log.i("soni-contextmenu", "editRecord selected");
                Intent editIntent = new Intent(context, OrderDetails.class);
                editIntent.putExtra("forEdit", true);
                editIntent.putExtra("selVehicleNum", myCarslist.get(contextSelPosition).getVehicle_no());
                startActivity(editIntent);
                return true;

        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (InternetUtil.isOnline(context)) {
            loadData();
        }
        else {
            loadFromLocal();

        }
    }

    private void loadFromLocal() {

        if(carsArraylist!=null && carsArraylist.size()>0){
            //set ui data

        }
        else{
            //local data does not exist
            Snackbar.make(getActivity().findViewById(R.id.orders_frag), "Not connected to Internet!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();
                }
            });
        }
    }

    //first method to start data loading
    //fetches users vehicle numbers array first
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        nodataText.setVisibility(View.INVISIBLE);

        if (InternetUtil.isOnline(context)) {
            carsArraylist.clear();

            AppListeners carInfoInstance = new AppListeners();
            carInfoInstance.setCarNumbersListener(new AppListeners.CarNumbersListener() {

                @Override
                public void onRetrieve(ArrayList<String> data, ArrayList<String> paidforCars) {
                    if (data != null && data.size() > 0) {
                        activeOrders = data;
                        activeOrders.removeAll(Collections.singletonList(null));

                        if(activeOrders!=null && activeOrders.size()>0){
                            myCarslist.clear();
                            loadCarInfos();
                        }
                        else {
                            carsGridView.setVisibility(View.GONE);
                            nodataText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        carsGridView.setVisibility(View.GONE);
                        nodataText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onProgress() {
                    //Toast.makeText(OrdersFragment.this.getActivity(), "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{

            Snackbar.make(getActivity().findViewById(R.id.orders_frag), "Not connected to Internet!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadData();
                        }
                    });

        }

    }

    private void loadCarInfos() {
        Log.i("soni-", "loading UI data..." + activeOrders.toString());
        myCarslist.clear();
        DatabaseReference carInfoReference = FirebaseDatabase.getInstance().getReference().child("CarsInfo");

        carInfoReference.orderByChild("createdDateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                if(dataSnapshot!=null && dataSnapshot.hasChildren()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        if(activeOrders.contains(ds.getKey())){
                            CarInfo carInfo = ds.getValue(CarInfo.class);
                            carInfo.setVehicle_no(ds.getKey());
                            myCarslist.add(carInfo);
                            carListAdapter.notifyDataSetChanged();
                        }
                    }

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
}
