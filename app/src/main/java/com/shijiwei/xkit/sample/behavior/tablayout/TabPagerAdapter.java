package com.shijiwei.xkit.sample.behavior.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by shijiwei on 2017/9/20.
 *
 * @VERSION 1.0
 */

public class TabPagerAdapter extends FragmentPagerAdapter {


    private List<String> mTitleSet;
    private List<Fragment> mFragmentSet;

    public TabPagerAdapter(FragmentManager fm, List<String> mTitleSet, List<Fragment> mFragmentSet) {
        super(fm);
        this.mTitleSet = mTitleSet;
        this.mFragmentSet = mFragmentSet;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentSet.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentSet.size();
    }

    //此方法用来显示tab上的名字

    @Override
    public CharSequence getPageTitle(int position) {

        return mTitleSet.get(position);
    }
}
