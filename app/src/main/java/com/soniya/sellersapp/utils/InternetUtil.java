package com.soniya.sellersapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetUtil {

    Context context;

    private InternetUtil(){}

    public static boolean isOnline(Context context)   {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected())   {
            return false;
        }
        return true;
    }
}
