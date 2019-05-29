package com.soniya.sellersapp;

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

    ArrayList<CarInfoSerial> carsList = new ArrayList<>();

    TabAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.clear();
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = mFragmentList.get(i);
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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }


}
