package com.chzheng.airmen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chzheng.airmen.networking.Client;

public class ServerBrowserActivity extends AppCompatActivity
        implements WifiP2pListener, PortDialogFragment.PortDialogListener {
    private static final String TAG = "Server browser";
    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WifiP2pManager.ActionListener mActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Log.i(TAG, "Wi-Fi p2p action success");
        }
        @Override
        public void onFailure(int i) {
            switch (i) {
                case WifiP2pManager.P2P_UNSUPPORTED:
                    Log.e(TAG, "Wi-Fi p2p is unsupported");
                    break;
                case WifiP2pManager.ERROR:
                    Log.e(TAG, "Error on discovering peers");
                    break;
                case WifiP2pManager.BUSY:
                    Log.w(TAG, "Discovering peers; busy");
                    break;
            }
        }
    };
    private WifiP2pInfo mConnectionInfo;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_browser);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.server_browser);
        mListView = findViewById(R.id.list);
        mListView.setAdapter(new PeersListAdapter(this, null));
        //WifiP2pManager
        mWifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiP2pBroadcastReceiver(this);
        //IntentFilter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //ListView click listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = ((WifiP2pDevice) view.getTag(R.id.wifip2pdevice)).deviceAddress;
                config.groupOwnerIntent = 0;
                mWifiManager.connect(mChannel, config, mActionListener);
                findViewById(android.R.id.content).findViewById(R.id.connection_progress).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWifiManager.removeGroup(mChannel, mActionListener);
        mWifiManager.discoverPeers(mChannel, mActionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onPeersChange(WifiP2pDeviceList peerList) {
        mListView.setAdapter(new PeersListAdapter(this, peerList));
    }

    @Override
    public void onConnectionChange(WifiP2pInfo connectionInfo, NetworkInfo networkInfo, WifiP2pGroup groupInfo) {
        if (connectionInfo.groupFormed) {
            mConnectionInfo = connectionInfo;
            findViewById(android.R.id.content).findViewById(R.id.connection_progress).setVisibility(View.INVISIBLE);
            PortDialogFragment dialog = new PortDialogFragment();
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDialogPositiveClick(int port) {
        new Thread(new Client(mConnectionInfo.groupOwnerAddress, port)).start();
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick() {}
}