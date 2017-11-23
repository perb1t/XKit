package com.shijiwei.xkit.sample.behavior.tablayout;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shijiwei.xkit.R;
import com.shijiwei.xkit.app.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2017/9/20.
 *
 * @VERSION 1.0
 */

public class TabFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private List<String> dataSet = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.behavior_tab_fragment;
    }

    @Override
    public void initialData(Bundle savedInstanceState) {
        for (int i = 0; i < 40; i++) {
            dataSet.add("  data  0");
        }
    }

    @Override
    public void initialView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        RecyAdapter adapter = new RecyAdapter(dataSet);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {

                return false;
            }
        });
    }

    @Override
    public void initialEvn() {

    }
}
