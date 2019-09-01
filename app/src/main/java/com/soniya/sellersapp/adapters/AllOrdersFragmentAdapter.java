package com.soniya.sellersapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soniya.sellersapp.R;
import com.soniya.sellersapp.pojo.CarInfo;
import com.soniya.sellersapp.pojo.CarInfoSerial;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllOrdersFragmentAdapter extends BaseAdapter {

    private Context context;
    private List carsList;
    private int resource;

    public AllOrdersFragmentAdapter(Context context, List carsList, int resource){
        this.context = context;
        this.carsList = carsList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return carsList.size();
    }

    @Override
    public Object getItem(int position) {
        return carsList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, null, true);
        }

        if(carsList!=null){
            if(carsList.get(position) instanceof CarInfo){
                CarInfo carInfo = (CarInfo) carsList.get(position);
                ImageView imageView = convertView.findViewById(R.id.carImageGrid);
                TextView name = convertView.findViewById(R.id.modelNameGrid);
                TextView loc = convertView.findViewById(R.id.locationGrid);
                TextView price = convertView.findViewById(R.id.sellingpriceGrid);

                name.setText(carInfo.getBrand_name() + " " + carInfo.getModel_name());
                loc.setText(carInfo.getLocation());
                price.setText(context.getResources().getString(R.string.rupee) + " " + carInfo.getSellingprice());

                if(carInfo.getThumbnailUriString() !=null && !carInfo.getThumbnailUriString().isEmpty()) {
                    Picasso.get().load(Uri.parse(carInfo.getThumbnailUriString())).resize(0, 140).into(imageView);
                }else if(carInfo.getImage_uri_list()!=null && carInfo.getImage_uri_list().size()>0){
                    Picasso.get().load(Uri.parse(carInfo.getImage_uri_list().get(0))).resize(0, 140).into(imageView);
                }else   {
                    Picasso.get().load(R.drawable.nocarpicture).resize(0, 140).into(imageView);
                }

            }else if(carsList.get(position) instanceof CarInfoSerial){
                CarInfoSerial carInfo = (CarInfoSerial) carsList.get(position);
                ImageView imageView = convertView.findViewById(R.id.carImageGrid);
                TextView name = convertView.findViewById(R.id.modelNameGrid);
                TextView loc = convertView.findViewById(R.id.locationGrid);
                TextView price = convertView.findViewById(R.id.sellingpriceGrid);

                name.setText(carInfo.getBrand_name() + " " + carInfo.getModel_name());
                loc.setText(carInfo.getLocation());
                price.setText(context.getResources().getString(R.string.rupee) + " " + carInfo.getSellingprice());

                if(carInfo.getThumbnailUriString() !=null && !carInfo.getThumbnailUriString().isEmpty()) {
                    Picasso.get().load(Uri.parse(carInfo.getThumbnailUriString())).resize(0, 140).into(imageView);
                }else if(carInfo.getImage_uri_list()!=null && carInfo.getImage_uri_list().size()>0){
                    Picasso.get().load(Uri.parse(carInfo.getImage_uri_list().get(0))).resize(0, 140).into(imageView);
                }else   {
                    Picasso.get().load(R.drawable.nocarpicture).resize(0, 140).into(imageView);
                }

            }
        }

        return convertView;
    }
}
