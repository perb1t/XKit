package com.shijiwei.xkit.sample.behavior.tablayout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shijiwei.xkit.R;

import java.util.List;

/**
 * Created by shijiwei on 2017/9/20.
 *
 * @VERSION 1.0
 */

public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.ViewHolder> {

    private List<String> dataSet;

    public RecyAdapter(List<String> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_single_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.lable);
        }
    }
}
