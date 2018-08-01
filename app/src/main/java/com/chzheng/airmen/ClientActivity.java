package com.chzheng.airmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.networking.Client;

import java.net.InetAddress;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.client);
        final InetAddress address = (InetAddress) getIntent().getSerializableExtra(String.valueOf(R.id.address));
        final int port = getIntent().getIntExtra(String.valueOf(R.id.port), -1);
        new Thread(new Client(address, port)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case ROLE:
                            final String[] roles = getResources().getStringArray(R.array.roles_array);
                            final String assignment = (String) memo.getData();
                            Class activity = MainActivity.class;
                            if (assignment.equals(roles[1])) activity = PilotActivity.class;
                            else if (assignment.equals(roles[2])) activity = BombardierActivity.class;
                            else if (assignment.equals(roles[3])) activity = NavigatorActivity.class;
                            else if (assignment.equals(roles[4])) activity = SignallerActivity.class;
                            startActivity(new Intent(ClientActivity.this, activity));
                            break;
                        case SHUTDOWN:
                            Log.d(TAG, "Shutdown memo");
                            startActivity(new Intent(ClientActivity.this, MainActivity.class));
                            break;
                    }
                } else if (msg.obj instanceof java.lang.Exception) {
                    Log.d(TAG, "Exception memo");
                    Toast.makeText(ClientActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ClientActivity.this, MainActivity.class));
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        sHandler = new Handler(Looper.getMainLooper());
    }
}
