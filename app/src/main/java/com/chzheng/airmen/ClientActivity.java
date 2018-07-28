package com.chzheng.airmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.networking.Client;

import java.net.InetAddress;
import java.util.LinkedHashSet;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.client);
        sHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    Log.d(TAG, "Received server memo");
                    switch (memo.getAction()) {
                        case ROLE:
                            final String[] roles = getResources().getStringArray(R.array.roles_array);
                            final String assignment = (String) memo.getData();
                            Class activity = MainActivity.class;
                            if (assignment.equals(roles[1])) activity = PilotActivity.class;
                            else if (assignment.equals(roles[2])) activity = BombardierActivity.class;
                            else if (assignment.equals(roles[3])) activity = NavigatorActivity.class;
                            else if (assignment.equals(roles[4])) activity = SignallerActivity.class;
                            Log.d(TAG, "Role assignment");
                            startActivity(new Intent(ClientActivity.this, activity));
                            break;
                        case SHUTDOWN:
                            Log.d(TAG, "Server shutdown");
                            startActivity(new Intent(ClientActivity.this, MainActivity.class));
                            break;
                    }
                } else if (msg.obj instanceof java.lang.Exception) {
                    Toast.makeText(ClientActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ClientActivity.this, MainActivity.class));
                }
            }
        };
        final InetAddress address = (InetAddress) getIntent().getSerializableExtra(String.valueOf(R.id.address));
        final int port = getIntent().getIntExtra(String.valueOf(R.id.port), -1);
        new Thread(new Client(address, port)).start();
    }
}
