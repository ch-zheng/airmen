package com.chzheng.airmen;

import android.content.Intent;
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

public class OwnerActivity extends AppCompatActivity {
    private static final String TAG = "OwnerActivity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.owner);
        mListView = findViewById(R.id.list);
        mListView.setAdapter(new AddressListAdapter(this, null));
        //Server-Client setup
        final int port = getIntent().getIntExtra(Integer.toString(R.id.port), -1);
        new Thread(new Server(port)).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress localAddress = InetAddress.getLocalHost();
                    new Thread(new Client(localAddress, port)).start();
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
                                    OwnerActivity.this,
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
                            startActivity(new Intent(OwnerActivity.this, activity));
                            break;
                        case SHUTDOWN:
                            startActivity(new Intent(OwnerActivity.this, MainActivity.class));
                            break;
                    }
                } else if (msg.obj instanceof java.lang.Exception) {
                    Toast.makeText(OwnerActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(OwnerActivity.this, MainActivity.class));
                }
                return false;
            }
        });
    }

    public void startGame(View view) {
        Log.d(TAG, "Sending server role assignments");
        Message message = Message.obtain();
        message.obj = new ServerMemo(ServerMemo.Action.ROLE_ASSIGNMENT, ((AddressListAdapter) mListView.getAdapter()).getDataSet());
        Server.sHandler.sendMessage(message);
    }
}
