package com.soniya.sellersapp.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAdapter {

    Context context;
    Location location;

    public LocationAdapter(Context context, Location location) {
        this.context = context;
        this.location = location;
    }

    public String getAddress() {
        String locinfo = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addrList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addrList.get(0);
            //locinfo= address.getLocality() + " " + address.getSubLocality() + " " + address.getPostalCode();

            if(addrList.get(0).getSubThoroughfare() !=null) {
                locinfo = address.getSubThoroughfare() + ", ";
            }
            if(addrList.get(0).getThoroughfare() != null){
                locinfo = locinfo + address.getThoroughfare() + ", ";
            }
            if(addrList.get(0).getLocality() != null){
                locinfo = locinfo + address.getLocality() + ", ";
            }
            if(addrList.get(0).getCountryName() != null){
                locinfo = locinfo + address.getCountryName() + ", ";
            }
            if(addrList.get(0).getPostalCode() != null){
                locinfo = locinfo + address.getPostalCode();
            }

            Log.i("soni-", "locaton " + locinfo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return locinfo;

    }
}
