package com.chzheng.airmen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiP2pBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Wi-Fi BroadcastReceiver";
    private WifiP2pListener mListener;

    public WifiP2pBroadcastReceiver(WifiP2pListener listener) {
        super();
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                Log.i(TAG, "Wi-Fi p2p connectivity state changed");
                mListener.onConnectionChange(
                        (WifiP2pInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO),
                        (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO),
                        (WifiP2pGroup) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                );
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                Log.i(TAG, "Wi-Fi p2p available peer list changed");
                mListener.onPeersChange((WifiP2pDeviceList) intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST));
                break;
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                Log.i(TAG, "Wi-Fi p2p state changed");
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, 0);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) Log.i(TAG, "Wi-Fi p2p is enabled");
                else if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) Log.w(TAG, "Wi-Fi p2p is disabled");
                //mActivity.onStateChange(state);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Log.i(TAG, "Device Wi-Fi p2p device details changed");
                //mActivity.onDeviceChange();
                break;
        }
    }
}