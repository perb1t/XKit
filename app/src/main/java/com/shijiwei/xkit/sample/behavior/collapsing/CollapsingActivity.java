package com.shijiwei.xkit.sample.behavior.collapsing;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.app.base.BaseActivity;

/**
 * Created by shijiwei on 2017/9/20.
 *
 * @VERSION 1.0
 */

public class CollapsingActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    @Override
    public int getLayoutResId() {
        return R.layout.activity_collapsing;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {

    }

    @Override
    public void initialView() {
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("sdfdsfsd");
//        toolbar.setTitleTextColor(Color.YELLOW);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (-verticalOffset >= appBarLayout.getTotalScrollRange() - 44){
            getWindow().setStatusBarColor(getResources().getColor(R.color.apple_blue));
        }

        if (verticalOffset >= -44){
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
    }
}
