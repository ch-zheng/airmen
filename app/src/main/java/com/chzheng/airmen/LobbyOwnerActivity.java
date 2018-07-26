package com.chzheng.airmen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.networking.Client;
import com.chzheng.airmen.networking.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;

public class LobbyOwnerActivity extends AppCompatActivity implements WifiP2pListener {
    private static final String TAG = "Lobby";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private WifiP2pManager mWifiManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WifiP2pManager.ActionListener mActionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            Log.i(TAG, "Success");
        }
        @Override
        public void onFailure(int i) {
            switch (i) {
                case WifiP2pManager.P2P_UNSUPPORTED:
                    Log.e(TAG, "Wi-Fi Direct is unsupported");
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
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_owner);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.lobby);
        mListView = findViewById(R.id.list);
        mListView.setAdapter(new AddressListAdapter(this, null));
        //Server-Client setup
        final int PORT = getIntent().getIntExtra(Integer.toString(R.id.port), 1716);
        new Thread(new Server(PORT)).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress localAddress = InetAddress.getLocalHost();
                    new Thread(new Client(localAddress, PORT)).start();
                }
                catch (UnknownHostException e) { Log.e(TAG, e.getMessage(), e); }
            }
        }).start();
        //Handler
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case CLIENT_LIST:
                            mListView.setAdapter(new AddressListAdapter(
                                    LobbyOwnerActivity.this,
                                    (LinkedHashSet<InetAddress>) ((ServerMemo) msg.obj).getData())
                            );
                            break;
                        case ROLE:
                            final String[] roles = getResources().getStringArray(R.array.roles_array);
                            final String assignment = (String) memo.getData();
                            Class activity = MainActivity.class;
                            if (assignment.equals(roles[1])) activity = PilotActivity.class;
                            else if (assignment.equals(roles[2])) activity = BombardierActivity.class;
                            else if (assignment.equals(roles[3])) activity = NavigatorActivity.class;
                            else if (assignment.equals(roles[4])) activity = SignallerActivity.class;
                            Log.d(TAG, "Role assignment");
                            startActivity(new Intent(LobbyOwnerActivity.this, activity));
                            break;
                        case SHUTDOWN:
                            Log.d(TAG, "Server shutdown");
                            startActivity(new Intent(LobbyOwnerActivity.this, MainActivity.class));
                            break;
                    }
                } else if (msg.obj instanceof java.lang.Exception) {
                    Toast.makeText(LobbyOwnerActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LobbyOwnerActivity.this, MainActivity.class));
                }
                return false;
            }
        });
        //WifiP2pManager
        mWifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiP2pBroadcastReceiver(this);
        //IntentFilter
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWifiManager.removeGroup(mChannel, mActionListener);
        mWifiManager.discoverPeers(mChannel, mActionListener);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onConnectionChange(WifiP2pInfo connectionInfo, NetworkInfo networkInfo, WifiP2pGroup groupInfo) {
        mWifiManager.discoverPeers(mChannel, mActionListener);
    }

    @Override
    public void onPeersChange(WifiP2pDeviceList deviceList) {}

    //Button method
    public void startGame(View view) {
        Message message = new Message();
        message.obj = new ServerMemo(ServerMemo.Action.ROLE_ASSIGNMENT, ((AddressListAdapter) mListView.getAdapter()).getDataSet());
        Server.sHandler.sendMessage(message);
    }
}
