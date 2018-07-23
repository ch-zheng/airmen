package com.chzheng.airmen;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;

public interface WifiP2pListener {
    //WIFI_P2P_CONNECTION_CHANGED_ACTION
    void onConnectionChange(WifiP2pInfo connectionInfo, NetworkInfo networkInfo, WifiP2pGroup groupInfo);
    //WIFI_P2P_PEERS_CHANGED_ACTION
    void onPeersChange(WifiP2pDeviceList deviceList);
    //WIFI_P2P_STATE_CHANGED_ACTION
    //void onStateChange(int state);
    //WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
    //void onDeviceChange();
}
