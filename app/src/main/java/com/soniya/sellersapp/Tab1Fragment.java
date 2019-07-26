package com.soniya.sellersapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Tab1Fragment extends Fragment {

    ListView carsListView;
    int contextSelPosition= 0;
    String selVehicleNum = "";
    FloatingActionButton addCarInfoButton;

    char space = ' ';
    char replacechar = '_';

    ArrayList<CarInfoSerial> carsArraylist = new ArrayList<>();
    ArrayList<String> activeOrders = new ArrayList<>();
    ArrayList<CarInfoSerial> myCarslist = new ArrayList<>();

    CustomAdapter carListAdapter;

    Context context;
   /* public static Fragment getInstance(int position)   {
        Log.i("soni-tab1frag", "position = " + String.valueOf(position));
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        Tab1Fragment tabfrag = new Tab1Fragment();
        tabfrag.setArguments(bundle);
        return tabfrag;
    }*/

   //stats below
    long unsoldWorth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        context = getActivity();
        addCarInfoButton = rootView.findViewById(R.id.addnewCarInfo);
        addCarInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent newlead = new Intent(context, AddNewLead.class);
//                startActivity(newlead);
                if (new FirebaseAdapter().getFirebaseUser().isEmailVerified()) {
                    Intent i = new Intent(context, UploadNewInfo.class);
                    startActivity(i);
                } else {
                    Log.i("soni-tab1", "cant add data, email not verified");
                }
            }
        });

        carsListView = rootView.findViewById(R.id.tab1listView);
        registerForContextMenu(carsListView);

        if(getArguments() !=null) {
            carsArraylist = (ArrayList<CarInfoSerial>) getArguments().getSerializable("carsArrayList");
        }

        unsoldWorth = 0;

        if(carsArraylist!=null && carsArraylist.size()>0) {
            myCarslist.clear();
            //Log.i("soni-", "we have carsarraylist tab1frag , size = " + carsArraylist.size());
            AppListeners carInfoInstance = new AppListeners();
            carInfoInstance.setCarNumbersListener(new AppListeners.CarNumbersListener() {

                @Override
                public void onRetrieve(ArrayList<String> data, ArrayList<String> paidforCars) {
                    if (data != null && data.size() > 0) {
                        activeOrders = data;

                        for (CarInfoSerial carInfoSerial : carsArraylist) {
                            if (activeOrders.contains(carInfoSerial.getVehicle_no())) {
                                myCarslist.add(carInfoSerial);
                                unsoldWorth += Long.valueOf(carInfoSerial.getSellingprice());
                            }
                        }

                        if (myCarslist != null && myCarslist.size() > 0) {
                            carListAdapter = new CustomAdapter(context, myCarslist, R.layout.carslist_layout);
                            carsListView.setAdapter(carListAdapter);
                            carListAdapter.notifyDataSetChanged();
                        } else {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                            carsListView.setAdapter(arrayAdapter);
                        }
                    } else {

                        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                        carsListView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                    }
                }

                @Override
                public void onProgress() {
                    //Toast.makeText(Tab1Fragment.this.getActivity(), "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                }
            });
        } else  {
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
            carsListView.setAdapter(arrayAdapter);

        }

        carsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

}
