package com.chzheng.airmen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.chzheng.airmen.memos.NavigatorMemo;
import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.memos.UpdateMemo;
import com.chzheng.airmen.networking.ClientSender;

import java.util.ArrayList;

public class NavigatorActivity extends AppCompatActivity {
    private static final String TAG = "Navigator Activity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.navigator);
        mListView = findViewById(R.id.list);
        mListView.setAdapter(new TestAdapter(this, null));
        //Register Handler
        sHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof UpdateMemo) {
                    final ArrayList<String> messages = ((UpdateMemo) msg.obj).getMessages();
                    mListView.setAdapter(new TestAdapter(NavigatorActivity.this, messages));
                } else if (msg.obj instanceof ServerMemo) {
                    final ServerMemo memo = (ServerMemo) msg.obj;
                    switch (memo.getAction()) {
                        case SHUTDOWN:
                            startActivity(new Intent(NavigatorActivity.this, ReviewActivity.class));
                            break;
                    }
                }
                return false;
            }
        });
    }

    //Button method
    public void sendMessage(View view) {
        Message message = new Message();
        message.obj = new NavigatorMemo("Navigator message");
        ClientSender.sHandler.sendMessage(message);
    }
}
