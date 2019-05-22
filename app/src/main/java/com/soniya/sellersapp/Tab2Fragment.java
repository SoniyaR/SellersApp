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

public class Tab2Fragment extends Fragment {

    ListView carsListView;

    int contextSelPosition= 0;
    String selVehicleNum = "";

    char space = ' ';
    char replacechar = '_';

    ArrayList<CarInfo> carsArraylist = new ArrayList<>();
    ArrayList<String> activeOrders = new ArrayList<>();
    ArrayList<CarInfo> otherCarslist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        carsListView = rootView.findViewById(R.id.tab2listView);
        registerForContextMenu(carsListView);

        if(getArguments()!=null) {
            Log.i("soni-", "we have carsarraylist tab2frag");
            carsArraylist = (ArrayList<CarInfo>) getArguments().getSerializable("carsArrayList");
        }

        if(carsArraylist !=null && carsArraylist.size()>0) {

            CarInfo carInfoInstance = new CarInfo();
            carInfoInstance.setCarNumbersListener(new CarInfo.CarNumbersListener() {
                @Override
                public void onRetrieve(ArrayList<String> data) {
                    if (data != null && data.size() > 0) {
                        activeOrders = data;

                        for (CarInfo carInfo : carsArraylist) {
                            if (!activeOrders.contains(carInfo.getVehicle_no())) {
                                otherCarslist.add(carInfo);
                            }
                        }

                        if (otherCarslist != null && otherCarslist.size() > 0) {
                            CustomAdapter carListAdapter = new CustomAdapter(Tab2Fragment.this.getActivity(), otherCarslist, R.layout.carslist_layout);
                            carsListView.setAdapter(carListAdapter);
                        }/* else {
                            ArrayAdapter arrayAdapter = new ArrayAdapter(Tab2Fragment.this.getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                            carsListView.setAdapter(arrayAdapter);
                        }*/
                    } else {

                        CustomAdapter carListAdapter = new CustomAdapter(Tab2Fragment.this.getActivity(), carsArraylist, R.layout.carslist_layout);
                        carsListView.setAdapter(carListAdapter);

                    }
                }

                @Override
                public void onProgress() {
                    Toast.makeText(getActivity(), "Retrieving Cars List", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            ArrayAdapter arrayAdapter = new ArrayAdapter(Tab2Fragment.this.getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
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
                                    if (!activeOrders.contains(carInfo.getVehicle_no())) {
                                        carsArraylist.add(carInfo);
                                    }
                                }
                                CustomAdapter carListAdapter = new CustomAdapter(getActivity(), data, R.layout.carslist_layout);
                                carsListView.setAdapter(carListAdapter);
                            }else{
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

            intent.putExtra("selVehicleNum", otherCarslist.get(position).getVehicle_no());
            startActivity(intent);

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
}
