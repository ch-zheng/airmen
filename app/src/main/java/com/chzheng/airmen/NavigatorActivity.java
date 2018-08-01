package com.chzheng.airmen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.chzheng.airmen.databinding.ActivityNavigatorBinding;
import com.chzheng.airmen.memos.ServerMemo;
import com.chzheng.airmen.memos.UpdateMemo;
import com.chzheng.airmen.views.MapView;

public class NavigatorActivity extends AppCompatActivity {
    private static final String TAG = "Navigator Activity";
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    private ActivityNavigatorBinding mBinding;
    private long mLastUpdateTime = System.currentTimeMillis();
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_navigator);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.navigator);
        mMapView = findViewById(R.id.map);

    }

    @Override
    protected void onStart() {
        super.onStart();
        sHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof UpdateMemo) {
                    final UpdateMemo memo = (UpdateMemo) msg.obj;
                    if (System.currentTimeMillis() - mLastUpdateTime > 100) {
                        mBinding.setUpdate(memo);
                        mMapView.setUpdate(memo);
                        mLastUpdateTime = System.currentTimeMillis();
                    }
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

    @Override
    protected void onStop() {
        super.onStop();
        sHandler = new Handler(Looper.getMainLooper());
    }
}
