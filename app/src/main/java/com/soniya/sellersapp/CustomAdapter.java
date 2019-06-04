package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends ArrayAdapter{
    HashMap<String, String> imgMap = new HashMap<>();
    List<String> urlList = new ArrayList<>();
    Context context;
    int resourceId;

    ArrayList dataArrayList;

    public CustomAdapter(Context context, ArrayList dataList, int resource){ //data list could be cars list or leads list
        super(context, resource, dataList);
        this.context = context;
        this.dataArrayList = dataList;
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null, true);
        }
        Log.i("soni-","customadpter resourceId = " + String.valueOf(resourceId) + " from " + String.valueOf(R.layout.leadslist_layout) + " , " + String.valueOf(R.layout.carslist_layout));
        if(dataArrayList.get(position) instanceof CarInfoSerial /*|| resourceId == R.layout.leadslist_layout*/) {

            CarInfoSerial info = (CarInfoSerial) getItem(position);

            ImageView imgView = convertView.findViewById(R.id.carImageView);
            if(info.getThumbnailUriString() !=null && !info.getThumbnailUriString().isEmpty()) {
                Picasso.with(context).load(Uri.parse(info.getThumbnailUriString())).resize(0, 140).into(imgView);
            }else {
                Picasso.with(context).load(Uri.parse(info.getImage_uri_list().get(0))).resize(0, 140).into(imgView);
            }

            TextView model = convertView.findViewById(R.id.modelName);
            model.setText(info.getModel_name());

            TextView loc = convertView.findViewById(R.id.location);
            loc.setText(info.getLocation());

            TextView price = convertView.findViewById(R.id.sellingprice);
            price.setText(info.getSellingprice());

        }
        else if(dataArrayList.get(position) instanceof LeadRequest) {

            LeadRequest request = (LeadRequest) getItem(position);

            TextView brand = convertView.findViewById(R.id.lead_brand);
            brand.setText(request.getLead_brand());

            TextView model = convertView.findViewById(R.id.lead_model);
            model.setText(request.getLead_model());

            TextView loc = convertView.findViewById(R.id.lead_location);
            loc.setText(request.getLead_location());

            TextView price = convertView.findViewById(R.id.lead_price);
            price.setText(request.getLead_price());

        }
        else if(dataArrayList.get(position) instanceof CarInfoSerial)   {

            CarInfo info = (CarInfo) getItem(position);

            ImageView imgView = convertView.findViewById(R.id.carImageView);
            if(info.getThumbnailUriString() !=null && !info.getThumbnailUriString().isEmpty()) {
                Picasso.with(context).load(Uri.parse(info.getThumbnailUriString())).resize(0, 140).into(imgView);
            }else {
                Picasso.with(context).load(Uri.parse(info.getImage_uri_list().get(0))).resize(0, 140).into(imgView);
            }

            TextView model = convertView.findViewById(R.id.modelName);
            model.setText(info.getModel_name());

            TextView loc = convertView.findViewById(R.id.location);
            loc.setText(info.getLocation());

            TextView price = convertView.findViewById(R.id.sellingprice);
            price.setText(info.getSellingprice());
        }

        return convertView;
    }
}
