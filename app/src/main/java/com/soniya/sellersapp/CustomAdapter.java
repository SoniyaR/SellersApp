package com.soniya.sellersapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends SimpleAdapter {
    HashMap<String, String> imgMap = new HashMap<>();
    List<String> urlList = new ArrayList<>();

    public CustomAdapter(Context context, List<HashMap<String, Object>> hmlist, int resource, String[] from, int[] to)  {

        super(context, hmlist, resource, from, to);

        for(HashMap hm: hmlist) {
            imgMap.put(hm.get("vehicle_no").toString(), hm.get("carImage").toString());
            urlList.add(hm.get("carImage").toString());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);

        Log.i("soni-", "in get view - customadapter");

        if(v instanceof ImageView)  {
            ImageView imgView = (ImageView) v;
            //String url = getItem(position).toString();
            String url = urlList.get(position);

            Log.i("soni-", "v is image view " + url);

            Picasso.with(v.getContext()).load(Uri.parse(url)).resize(100, 100).into(imgView);
        }

        return v;
    }
}
