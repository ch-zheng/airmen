package com.chzheng.airmen;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class AddressListAdapter extends BaseAdapter {
    private static final String TAG = "AddressListAdapter";
    private Context mContext;
    private LinkedHashMap<InetAddress, CharSequence> mDataSet = new LinkedHashMap<>();

    public AddressListAdapter(Context context, @Nullable LinkedHashSet<InetAddress> addresses) {
        mContext = context;
        if (addresses != null) {
            for (InetAddress address : addresses) {
                mDataSet.put(address, null);
            }
        }
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSet.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //Initialize new ViewHolder
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_player, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.hostname = convertView.findViewById(R.id.hostname);
            viewHolder.address = convertView.findViewById(R.id.address);
            viewHolder.role = convertView.findViewById(R.id.role);
            //Spinner initialization
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    mContext,
                    R.array.roles_array,
                    android.R.layout.simple_spinner_item
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            viewHolder.role.setAdapter(spinnerAdapter);
            viewHolder.role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mDataSet.put((InetAddress) getItem(position), (CharSequence) adapterView.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {}
            });
            if (mContext instanceof LobbyActivity) {{
                viewHolder.role.setEnabled(false);
            }}
            convertView.setTag(R.id.viewholder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.viewholder);
        }
        //Update view; this bullshit prevents a NetworkOnMainThreadException
        final ViewHolder TEMP = viewHolder;
        Thread quickie = new Thread(new Runnable() {
            @Override
            public void run() {
                TEMP.hostname.setText(((InetAddress) getItem(position)).getHostName());
                TEMP.address.setText(((InetAddress) getItem(position)).getHostAddress());
            }
        });
        quickie.start();
        try { quickie.join(); }
        catch (InterruptedException e) { Log.e(TAG, e.getMessage()); }
        return convertView;
    }

    public LinkedHashMap<InetAddress, CharSequence> getDataSet() {
        return mDataSet;
    }

    private static class ViewHolder {
        public TextView hostname, address;
        public Spinner role;
    }
}
