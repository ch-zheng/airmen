package com.chzheng.airmen;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TestAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mDataSet = new ArrayList<>();

    public TestAdapter(Context context, @Nullable ArrayList<String> messages) {
        mContext = context;
        if (messages != null) mDataSet = messages;
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_test, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.content = convertView.findViewById(R.id.message);
            convertView.setTag(R.id.viewholder, viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag(R.id.viewholder);
        viewHolder.content.setText((String) getItem(position));
        return convertView;
    }

    private static class ViewHolder {
        public TextView content;
    }
}
