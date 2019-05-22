package com.soniya.sellersapp;

import android.content.Intent;
import android.os.Bundle;
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

    char space = ' ';
    char replacechar = '_';

    ArrayList<CarInfo> carsArraylist = new ArrayList<>();
    ArrayList<String> activeOrders = new ArrayList<>();
    ArrayList<CarInfo> myCarslist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        carsListView = rootView.findViewById(R.id.tab1listView);
        registerForContextMenu(carsListView);

        if(getArguments() !=null) {
            carsArraylist = (ArrayList<CarInfo>) getArguments().getSerializable("carsArrayList");
        }

        if(carsArraylist!=null && carsArraylist.size()>0) {
            CarInfo carInfoInstance = new CarInfo();
            carInfoInstance.setCarNumbersListener(new CarInfo.CarNumbersListener() {
                @Override
                public void onRetrieve(ArrayList<String> data) {
                    if (data != null && data.size() > 0) {
                        activeOrders = data;

                        for (CarInfo carInfo : carsArraylist) {
                            if (activeOrders.contains(carInfo.getVehicle_no())) {
                                myCarslist.add(carInfo);
                            }
                        }

                        if (myCarslist != null && myCarslist.size() > 0) {
                            CustomAdapter carListAdapter = new CustomAdapter(Tab1Fragment.this.getActivity(), myCarslist, R.layout.carslist_layout);
                            carsListView.setAdapter(carListAdapter);
                        } else {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(Tab1Fragment.this.getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                            carsListView.setAdapter(arrayAdapter);
                        }
                    } else {

                        ArrayAdapter arrayAdapter = new ArrayAdapter(Tab1Fragment.this.getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                        carsListView.setAdapter(arrayAdapter);

                    }
                }

                @Override
                public void onProgress() {
                    Toast.makeText(getActivity(), "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                }
            });
        } else  {
            ArrayAdapter arrayAdapter = new ArrayAdapter(Tab1Fragment.this.getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
            carsListView.setAdapter(arrayAdapter);

        }
        /*CarInfo carInfoInstance = new CarInfo();
        carInfoInstance.setCarNumbersListener(data -> {
            if (data != null && data.size() > 0) {
                activeOrders = data;

                carInfoInstance.setCarInfoListener(new CarInfo.CarInfoListener() {
                    @Override
                    public void onDataRetrieved(ArrayList<CarInfo> data) {
                        if (data != null && data.size() > 0) {

                            if (!activeOrders.isEmpty() && activeOrders.size() > 0) {

                                for (CarInfo carInfo : data) {
                                    if (activeOrders.contains(carInfo.getVehicle_no())) {
                                        carsArraylist.add(carInfo);
                                    }
                                }

                                CustomAdapter carListAdapter = new CustomAdapter(getActivity(), carsArraylist, R.layout.carslist_layout);

                                carsListView.setAdapter(carListAdapter);
                            } else {
                                Log.i("soni-", "activeOrders is empty");
                                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                                carsListView.setAdapter(arrayAdapter);
                            }

                        } else {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                            carsListView.setAdapter(arrayAdapter);
                        }

                    }

                    @Override
                    public void onProgress() {
                        Toast.makeText(getActivity(), "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        */

        carsListView.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(getActivity(), OrderDetails.class);

            intent.putExtra("selVehicleNum", myCarslist.get(position).getVehicle_no());
            startActivity(intent);

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
                Log.i("soni-contextmenu", "deleteRecord selected");
                new FirebaseDataFactory().deleteRecord(selVehicleNum);
                return true;

            case R.id.editRecord:
                Log.i("soni-contextmenu", "editRecord selected");
                Intent editIntent = new Intent(getActivity(), OrderDetails.class);
                editIntent.putExtra("forEdit", true);
                editIntent.putExtra("selVehicleNum", myCarslist.get(contextSelPosition).getVehicle_no());
                startActivity(editIntent);
                return true;

        }

        return super.onContextItemSelected(item);

    }
}
