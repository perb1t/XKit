package com.shijiwei.xkit.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.app.base.BaseActivity;
import com.shijiwei.xkit.widget.menuscroll.MenuItemInfo;
import com.shijiwei.xkit.widget.menuscroll.ScrollMenuItem;
import com.shijiwei.xkit.widget.menuscroll.ScrollMenuView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2016/10/6.
 */
public class MenuScrollActivity extends BaseActivity {


    private ScrollMenuView mScrollMenuView;
    private List<MenuItemInfo> mMenuItems;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_menu_scroll;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {

    }

    @Override
    public void initialView() {
        init();
    }

    @Override
    public void initialEvn() {

    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    public void onNetworkStateChanged(int type, boolean isAvailable) {

    }

    private void init() {
        mScrollMenuView = (ScrollMenuView) findViewById(R.id.scroll_menu);
        mMenuItems = new ArrayList<>();
        mMenuItems.add(new MenuItemInfo("貓1", R.mipmap.cat, null));
        mMenuItems.add(new MenuItemInfo("狗2", R.mipmap.dogavator, null));
        mMenuItems.add(new MenuItemInfo("大海3", R.mipmap.sea, null));
        mMenuItems.add(new MenuItemInfo("貓4", R.mipmap.cat, null));
        mMenuItems.add(new MenuItemInfo("狗5", R.mipmap.dogavator, null));
        mMenuItems.add(new MenuItemInfo("大海6", R.mipmap.sea, null));
        mMenuItems.add(new MenuItemInfo("貓7", R.mipmap.cat, null));
        mMenuItems.add(new MenuItemInfo("狗8", R.mipmap.dogavator, null));
        mMenuItems.add(new MenuItemInfo("大海9", R.mipmap.sea, null));
        mMenuItems.add(new MenuItemInfo("貓10", R.mipmap.cat, null));
        mMenuItems.add(new MenuItemInfo("狗11", R.mipmap.dogavator, null));
        mMenuItems.add(new MenuItemInfo("大海12", R.mipmap.sea, null));
        mMenuItems.add(new MenuItemInfo("貓13", R.mipmap.cat, null));
        mMenuItems.add(new MenuItemInfo("狗14", R.mipmap.dogavator, null));
        mMenuItems.add(new MenuItemInfo("大海15", R.mipmap.sea, null));

        try {
            mScrollMenuView.setMenuItemSet(mMenuItems, 4, 2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mScrollMenuView.setIconWidth(100);
        mScrollMenuView.setLableTextSize(15);

        mScrollMenuView.setOnItemClickListener(new ScrollMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(ScrollMenuItem item) {
                Log.e("-----", item.getLable());
                Toast.makeText(MenuScrollActivity.this, item.getLable(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
