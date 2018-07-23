package com.chzheng.airmen;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeersListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<WifiP2pDevice> mDataSet = new ArrayList<>();

    public PeersListAdapter(Context context, WifiP2pDeviceList dataSet) {
        mContext = context;
        if (dataSet != null) {
            mDataSet = new ArrayList<>(dataSet.getDeviceList());
            Collections.sort(mDataSet, new Comparator<WifiP2pDevice>() {
                @Override
                public int compare(WifiP2pDevice one, WifiP2pDevice two) {
                    return one.deviceName.compareTo(two.deviceName);
                }
            });
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_peer, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mDeviceNameText = convertView.findViewById(R.id.device_name);
            viewHolder.mDeviceAddressText = convertView.findViewById(R.id.device_address);
            convertView.setTag(R.id.viewholder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.viewholder);
        }
        viewHolder.mDeviceNameText.setText(mDataSet.get(position).deviceName);
        viewHolder.mDeviceAddressText.setText(mDataSet.get(position).deviceAddress);
        convertView.setTag(R.id.wifip2pdevice, mDataSet.get(position));
        return convertView;
    }

    private static class ViewHolder {
        public TextView mDeviceNameText, mDeviceAddressText;
    }
}
