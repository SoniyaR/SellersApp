package com.soniya.sellersapp;

import java.util.ArrayList;

public class ProfileListener {

    FirebaseDataFactory dataFactory;

    public interface RetrieveStatsListener    {
        public void onDataRetrieve (ProfileStats data);
        public void onDataCancelled();
    }

    public RetrieveStatsListener retrieveStatsListener;

    public ProfileListener(){
        dataFactory = new FirebaseDataFactory();
        retrieveStatsListener = null;
    }

    public void setRetrieveProfileStats(RetrieveStatsListener listener){
        this.retrieveStatsListener = listener;
        dataFactory.retrieveStats(retrieveStatsListener);
    }

}
