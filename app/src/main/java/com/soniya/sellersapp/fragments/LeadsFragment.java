package com.soniya.sellersapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.soniya.sellersapp.AddNewLead;
import com.soniya.sellersapp.adapters.OrdersFragmentAdapter;
import com.soniya.sellersapp.pojo.LeadRequest;
import com.soniya.sellersapp.R;
import com.soniya.sellersapp.adapters.FirebaseAdapter;

import java.util.ArrayList;

public class LeadsFragment extends Fragment {

    ListView leadListView;
    FloatingActionButton addLeadButton;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ArrayList<LeadRequest> leads = new ArrayList<>();
    FirebaseAdapter fbAdapter;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_leads, container, false);
        leadListView = rootView.findViewById(R.id.leadsList);
        registerForContextMenu(leadListView);
        fbAdapter = new FirebaseAdapter();
        context = getActivity();

        addLeadButton = rootView.findViewById(R.id.addleadfab);
        addLeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newlead = new Intent(context, AddNewLead.class);
                startActivity(newlead);
            }
        });

        leads.clear();
        DatabaseReference leadRef = db.child("LeadRequests").child(fbAdapter.getCurrentUser());
        leadRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null && dataSnapshot.getValue() !=null)   {

                    //test for object type
                    /*if(dataSnapshot.getValue() instanceof HashMap)  {
                        Log.i("soni-", "leadreqs in hashmap");
                    }else{
                        Log.i("soni-", "leadreqs in not hashmap");
                    }*/

                    for(DataSnapshot data :  dataSnapshot.getChildren())    {
                        LeadRequest request = data.getValue(LeadRequest.class);
                        leads.add(request);
                    }

                    if(!leads.isEmpty() && leads.size() > 0) {
                        OrdersFragmentAdapter adapter = new OrdersFragmentAdapter(context, leads, R.layout.leadslist_layout);
                        leadListView.setAdapter(adapter);
                    }else{
                        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
                        leadListView.setAdapter(arrayAdapter);
                    }

                }
                else{
//                    ConstraintLayout constraintLayout = rootView.findViewById(R.id.leads_backlayout);
//                    TextView noReqText = new TextView(getActivity());
//                    noReqText.setText("No Requests yet...");
//                    LinearLayout linearLayout = new LinearLayout(getActivity());
//                    linearLayout.setOrientation(LinearLayout.VERTICAL);
//                    linearLayout.addView(noReqText);
//                    constraintLayout.addView(linearLayout);
                    Log.i("soni-", "DataSnapshot for requests is null tab3frag");
//                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new String[]{"Nothing to show"});
//                    leadListView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        leadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("soni-", "clicked on lead " + leads.get(position).getLead_brand() + "-" + leads.get(position).getLead_model());
            }
        });

        return rootView;
    }

    //context menu methods below
    //TODO


}
