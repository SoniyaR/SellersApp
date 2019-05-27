package com.soniya.sellersapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    //private String [] titleList = {"My Orders", "Other Orders"};

    ArrayList<CarInfo> carsList = new ArrayList<>();

    TabAdapter(FragmentManager fm, ArrayList<CarInfo> carsArraylist) {
        super(fm);
        mFragmentList.clear();
        carsList.clear();
        carsList.addAll(carsArraylist);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = mFragmentList.get(i);
        Bundle bundle = new Bundle();
        bundle.putSerializable("carsArraylist", carsList);
        fragment.setArguments(bundle);
       return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentTitleList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
    public void removeAllFragments(){
        mFragmentList.clear();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }


}
