package com.chzheng.airmen;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private final static int PORT = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        Animation bob = AnimationUtils.loadAnimation(this, R.anim.bob);
        findViewById(R.id.image_airplane).startAnimation(bob);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: Kill running server/client instances
    }

    public void begin(View view) {
        mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (info.groupFormed) {
                    if (info.isGroupOwner) {
                        Intent intent = new Intent(MainActivity.this, OwnerActivity.class);
                        intent.putExtra(String.valueOf(R.id.port), PORT);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                        intent.putExtra(String.valueOf(R.id.port), PORT);
                        intent.putExtra(String.valueOf(R.id.address), info.groupOwnerAddress);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_group_formed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
