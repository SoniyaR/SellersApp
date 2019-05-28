package com.soniya.sellersapp;

import android.app.Activity;
import android.content.Context;
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

public class CustomAdapter extends ArrayAdapter<CarInfoSerial> {
    HashMap<String, String> imgMap = new HashMap<>();
    List<String> urlList = new ArrayList<>();
    Context context;

    ArrayList<CarInfoSerial> carList;

    public CustomAdapter(Context context, ArrayList<CarInfoSerial> carsList, int resource){
        super(context, resource, carsList);
        this.context = context;
        this.carList = carsList;
    }



    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

    /*public CustomAdapter(Context context, List<HashMap<String, Object>> hmlist, int resource, String[] from, int[] to)  {

        super(context, hmlist, resource, from, to);
        this.context = context;

        for(HashMap hm: hmlist) {
            imgMap.put(hm.get("vehicle_no").toString(), hm.get("carImage").toString());
            urlList.add(hm.get("carImage").toString());
        }
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.carslist_layout, null, true);


        }

        CarInfoSerial info = getItem(position);

        ImageView imgView = (ImageView) convertView.findViewById(R.id.carImageView);
        Picasso.with(context).load(info.getImage_uri_list().get(0)).resize(100, 100).into(imgView);

        TextView model= (TextView) convertView.findViewById(R.id.modelName);
        model.setText(decodeString(info.getModel_name()));

        TextView loc = (TextView) convertView.findViewById(R.id.location);
        loc.setText(info.getLocation());

        TextView price = (TextView) convertView.findViewById(R.id.sellingprice);
        price.setText(info.getSellingprice());

        return convertView;
                //super.getView(position, convertView, parent);
    }
}
