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

import java.net.InetAddress;
import java.util.LinkedHashSet;

public class LobbyActivity extends AppCompatActivity {
    private static final String TAG = "Lobby";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.lobby);
        mListView = findViewById(R.id.list);
        mListView.setAdapter(new AddressListAdapter(this, null));
        sHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case CLIENT_LIST:
                            mListView.setAdapter(new AddressListAdapter(
                                    LobbyActivity.this,
                                    (LinkedHashSet<InetAddress>) ((ServerMemo) msg.obj).getData())
                            );
                            break;
                        case ROLE:
                            final String[] roles = getResources().getStringArray(R.array.roles_array);
                            final String assignment = (String) memo.getData();
                            Log.d(TAG, roles.toString()); //DEBUGGING
                            Log.d(TAG, assignment); //DEBUGGING
                            Class activity = MainActivity.class;
                            if (assignment.equals(roles[1])) activity = PilotActivity.class;
                            else if (assignment.equals(roles[2])) activity = BombardierActivity.class;
                            else if (assignment.equals(roles[3])) activity = NavigatorActivity.class;
                            else if (assignment.equals(roles[4])) activity = SignallerActivity.class;
                            Log.d(TAG, "Role assignment");
                            startActivity(new Intent(LobbyActivity.this, activity));
                            break;
                        case SHUTDOWN:
                            Log.d(TAG, "Server shutdown");
                            startActivity(new Intent(LobbyActivity.this, MainActivity.class));
                            break;
                    }
                } else if (msg.obj instanceof java.lang.Exception) {
                    Toast.makeText(LobbyActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LobbyActivity.this, MainActivity.class));
                }
            }
        };
    }
}
