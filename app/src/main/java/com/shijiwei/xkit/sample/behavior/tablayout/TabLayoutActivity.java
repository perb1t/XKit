package com.shijiwei.xkit.sample.behavior.tablayout;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.app.base.BaseActivity;
import com.shijiwei.xkit.utility.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2017/9/20.
 *
 * @VERSION 1.0
 */

public class TabLayoutActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
//    private Toolbar toolbar;
    private ViewPager viewPager;

    private TabPagerAdapter adapter;
    private List<String> tabNames = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();


    @Override
    public int getLayoutResId() {
        return R.layout.activity_design;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {
        tabNames.add("tab1");
        tabNames.add("tab2");
        tabNames.add("tab3");

        fragments.add(new TabFragment());
        fragments.add(new TabFragment());
        fragments.add(new TabFragment());
    }

    @Override
    public void initialView() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabNames, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//        toolbar.setTitle("hello");
//        toolbar.setTitleTextColor(Color.RED);

    }

    @Override
    public void initialEvn() {
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void onNetworkStateChanged(int type, boolean isAvailable) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        LogUtil.e("======", "   " + verticalOffset);
    }
}
