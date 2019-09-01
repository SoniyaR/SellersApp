package com.soniya.sellersapp.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.soniya.sellersapp.pojo.CarInfoSerial;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    //private String [] titleList = {"My Orders", "Other Orders"};

    ArrayList<CarInfoSerial> carsList = new ArrayList<>();

    public TabAdapter(FragmentManager fm) {
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
